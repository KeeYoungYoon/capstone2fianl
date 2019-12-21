package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;


public class Main2Activity extends AppCompatActivity {

    Button driving_start;
    Button record_view;
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        driving_start = findViewById(R.id.driving_start);

        driving_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), driving_record.class);
                startActivity(intent);
                finish();
            }
        });

        record_view = findViewById(R.id.record_view);

        record_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), record_view.class);


                int[][] flag=new int[1000][1000];//0급감속 1급가속 2과속 3방지턱
                int[][] ncout = new int[1000][4];//0급감속 1급가속 2과속 3방지턱 밤 위반 횟수
                int[][] cout = new int[1000][4];//0급감속 1급가속 2과속 3방지턱 위반 횟수
                double g=9.8;
                int k=0;
                int cnt=1;
                int num=1;
                int[][] check= new int[1000][1000];
                String line = "";
                String arr[][][]=new String[1000][1000][];//[0]날짜 시간 [1]위치정보 [2]위도 [3]경도 [4]속도 [5]가속도
                double[][] bump=new double[10000][3];
                double[][][][] map=new double[100][2][1000][2];//[n번쨰][a,b][갯수][위,경]	[m][n][0]={m번쨰 n위치 갯수,속도]
                float[][] farr= new float[1000][2];//[0]속도 [1]가속도
                float[][] fex= new float[1000][2];//전 입력값
                int sth=-1;//시작 시간
                int endh=-1;//종료 시간
                int[] hset=new int[100];
                int bumpcnt=0;
                int mapcnt=0;
                int c=0;
                int[][] source = new int [1000][2];
                int sid=0;
                try {//과속 파일 읽기
                   InputStream inputstream;
                   inputstream = getResources().openRawResource(R.raw.road);

                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputstream));
                    String[][] mapline=new String[10000][];
                    line = bufReader.readLine();
                    int m=1;
                    char[][] a = new char[1000000][2];
                    while((line = bufReader.readLine()) != null){
                        mapline[mapcnt]=line.split(",");
                        a[mapcnt][0]=mapline[mapcnt][0].charAt(0);
                        a[mapcnt][1]=mapline[mapcnt][0].charAt(2);
                        if(mapcnt!=0) {
                            if(a[mapcnt][0]==a[mapcnt-1][0]) {
                                if(a[mapcnt][1]=='0') {
                                    map[c][0][m][1]=Double.valueOf(mapline[mapcnt][1]);
                                    map[c][0][m][0]=Double.valueOf(mapline[mapcnt][2]);
                                    map[c][0][0][1]=Double.valueOf(mapline[mapcnt][3]);
                                    m++;
                                    mapcnt++;
                                }else if(a[mapcnt][1]=='1' && a[mapcnt-1][1]=='0'){
                                    map[c][0][0][0]=(double)m-1;
                                    m=1;
                                    map[c][1][m][1]=Double.valueOf(mapline[mapcnt][1]);
                                    map[c][1][m][0]=Double.valueOf(mapline[mapcnt][2]);
                                    m++;
                                    mapcnt++;
                                }else {
                                    map[c][1][m][1]=Double.valueOf(mapline[mapcnt][1]);
                                    map[c][1][m][0]=Double.valueOf(mapline[mapcnt][2]);
                                    m++;mapcnt++;
                                }
                            }
                            else{
                                c++;
                                map[c][0][0][0]=(double)m;
                                m=1;
                                map[c][0][m][1]=Double.valueOf(mapline[mapcnt][1]);
                                map[c][0][m][0]=Double.valueOf(mapline[mapcnt][2]);
                                map[c][0][0][1]=Double.valueOf(mapline[mapcnt][3]);
                                m++;mapcnt++;
                            }

                        }else {
                            map[c][0][m][1]=Double.valueOf(mapline[mapcnt][1]);
                            map[c][0][m][0]=Double.valueOf(mapline[mapcnt][2]);
                            map[c][0][0][1]=Double.valueOf(mapline[mapcnt][3]);
                            mapcnt++;
                            m++;
                        }

                    }bufReader.close();
                }catch (FileNotFoundException e) {
                }catch(IOException e){
                    //System.out.println(e);
                }


                try {//가속도 파일 읽기
                    InputStream inputstream;
                    inputstream = getResources().openRawResource(R.raw.bump);

                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputstream));
                    String[][] bumpline=new String[100000][];
                    line = bufReader.readLine();
                    while((line = bufReader.readLine()) != null){
                        bumpline[bumpcnt]=line.split(",");
                        bump[bumpcnt][2]=Double.valueOf(bumpline[bumpcnt][1]);
                        bump[bumpcnt][1]=Double.valueOf(bumpline[bumpcnt][2]);
                        bumpcnt++;
                    }
                    bufReader.close();
                }catch (FileNotFoundException e) {
                }catch(IOException e){
                    //System.out.println(e);
                }
                try{//주행기록
                    double i,j;
                    for(num=1;num<2;num++) {
                        File dir = new File (foldername);
                        //디렉토리 폴더가 없으면 생성함
                        if(!dir.exists()){
                            dir.mkdir();
                        }
                        //파일 output stream 생성
                        FileInputStream fin = new FileInputStream(foldername+"/"+num+".txt");
                        BufferedReader bufReader = new BufferedReader(new InputStreamReader(fin));
                        int road=-1;
                        int over=0;

                        k=0;
                        ncout[num][0]=ncout[num][1]=ncout[num][2]=ncout[num][3]=0;
                        cout[num][0]=cout[num][1]=cout[num][2]=cout[num][3]=0;
                        double now=0;
                        while((line = bufReader.readLine()) != null){//한파일 읽기
                            int hour;
                            arr[num][k]=line.split("	");
                            arr[num][k][0]=arr[num][k][0].substring(arr[num][k][0].lastIndexOf(": ")+2);
                            arr[num][k][1]=arr[num][k][1].substring(arr[num][k][1].lastIndexOf(": ")+2);
                            arr[num][k][3]=arr[num][k][3].substring(arr[num][k][3].lastIndexOf(": ")+2);
                            arr[num][k][2]=arr[num][k][2].substring(arr[num][k][2].lastIndexOf(": ")+2);
                            arr[num][k][4]=arr[num][k][4].substring(arr[num][k][4].lastIndexOf(": ")+2);
                            arr[num][k][5]=arr[num][k][5].substring(arr[num][k][5].lastIndexOf(": ")+2);
                            i=Double.valueOf(arr[num][k][2]);
                            j=Double.valueOf(arr[num][k][3]);
                            i=Math.round(i*1000000)/1000000.0;
                            j=Math.round(j*1000000)/1000000.0;
                            farr[k][0]= Float.valueOf(arr[num][k][4]);
                            farr[k][1]= Float.valueOf(arr[num][k][5]);
                            hour=Integer.valueOf(arr[num][k][0].substring(11,13));
                            if(sth==-1)
                                sth=hour*60+Integer.valueOf(arr[num][k][0].substring(14,16));
                            endh=hour*60+Integer.valueOf(arr[num][k][0].substring(14,16));
                            if(arr[num][k][1].contentEquals("network")) {
                                farr[k][0]=fex[k][0]+(fex[k][1]*10);
                            }
                            if(fex[k][0]<farr[k][0]) {//급가속

                                if(farr[k][1]>(g*0.2)) {

                                    if(hour>21 || hour<6)
                                        ncout[num][1]++;
                                    else
                                        cout[num][1]++;
                                    flag[num][cnt]=1;
                                    check[num][cnt]=k;
                                    cnt++;

                                }
                            }else if(fex[k][0]>farr[k][0]) {//급감속
                                if(farr[k][1]>(g*0.4)) {
                                    if(hour>21 || hour<6)
                                        ncout[num][0]++;
                                    else
                                        cout[num][0]++;
                                    flag[num][cnt]=0;
                                    check[num][cnt]=k;
                                    cnt++;

                                }
                            }
                            if(now!=0){//도로체크가 이미 된후
                                if(over!=1) {
                                    if(farr[k][0]>now){
                                        if(hour>21 || hour<6)
                                            ncout[num][2]++;
                                        else
                                            cout[num][2]++;
                                        flag[num][cnt]=2;
                                        check[num][cnt]=sid;
                                        source[sid][0]=k;
                                        source[sid][1]=road;
                                        cnt++;
                                        over=1;
                                        now=0;
                                    }
                                }
                            }
                            else{//도로체크
                                for(int m=0;m<c;m++) {
                                    for(int n=0;n<map[m][0][0][0];n++) {
                                        if(Math.abs(i-map[m][0][n][0])<=0.00005 && Math.abs(j-map[m][0][n][1])<=0.00005) {
                                            if(road!=m) {
                                                road=m;
                                                now=map[m][0][0][1];
                                                over=0;
                                                //break LOOP;
                                            }
                                        }
                                    }
                                }
                            }
                            for(int m=0;m<bumpcnt;m++) {//방지턱
                                if(Math.abs(i-bump[m][1])<=0.00005 && Math.abs(j-bump[m][2])<=0.00005) {
                                    if(check[num][cnt-1]==k || check[num][cnt-1]==k-1 ||check[num][cnt-1]== k-2 ||check[num][cnt-1]== k-3 ||check[num][cnt-1]== k-4 ||check[num][cnt-1]== k-5) {

                                        if(flag[num][cnt-1]==1) {
                                            if(hour>21 || hour<6) {
                                                ncout[num][1]--;
                                                ncout[num][3]++;
                                            }
                                            else {
                                                cout[num][1]--;
                                                cout[num][3]++;
                                            }
                                            flag[num][cnt-1]=3;
                                            check[num][cnt-1]=k;
                                            break;
                                        }else if(flag[num][cnt-1]==0) {
                                            if(hour>21 || hour<6) {
                                                ncout[num][0]--;
                                                ncout[num][3]++;
                                            }
                                            else {
                                                cout[num][0]--;
                                                cout[num][3]++;
                                            }
                                            flag[num][cnt-1]=3;
                                            check[num][cnt-1]=k;
                                            break;
                                        }
                                    }
                                }
                            }
                            System.arraycopy(farr[k], 0, fex[k+1], 0, 2);
                            k++;
                        }//한파일 읽기
                        bufReader.close();
                        sth=((endh-sth)/60)+1;
                        hset[num]=sth;//한파일 시간데이터
                        sth=-1;
                        endh=-1;
                        flag[num][0]=cnt-1;//한파일내 flag데이터 갯수
                        check[num][0]=cnt-1;//한파일내 flag데이터 갯수
                        cnt=1;

                        check[num+1][0]=-1;
                    }//모든파일 읽음
                }
                catch (FileNotFoundException e) {
                }catch(IOException e){
                    //System.out.println(e);
                }
                ncout[0][0]=ncout[0][1]=ncout[0][2]=ncout[0][3]=0;
                cout[0][0]=cout[0][1]=cout[0][2]=cout[0][3]=0;
                for(int i=1;i<num-1;i++) {
                    ncout[0][0] = ncout[0][0]+ncout[i][0];
                    ncout[0][1] = ncout[0][1]+ncout[i][1];
                    ncout[0][2] = ncout[0][2]+ncout[i][2];
                    ncout[0][3] = ncout[0][3]+ncout[i][3];
                    cout[0][0] = cout[0][0]+cout[i][0];
                    cout[0][1] = cout[0][1]+cout[i][1];
                    cout[0][2] = cout[0][2]+cout[i][2];
                    cout[0][3] = cout[0][3]+cout[i][3];
                }
                try {//파일 쓰기
                    File dir = new File (foldername);
                    //디렉토리 폴더가 없으면 생성함
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    //파일 output stream 생성
                    FileOutputStream fos = new FileOutputStream(foldername+"/"+"result.txt");
                    //파일쓰기
                    BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
                    bufWriter.write("Dec Night:Day\t" + ncout[0][0] +":" + cout[0][0] );
                    bufWriter.write("\n"+"Inc Night:Day\t" + ncout[0][1] +":" + cout[0][1] );
                    bufWriter.write("\n"+"Acc Night:Day\t" + ncout[0][2] +":" + cout[0][2] );
                    bufWriter.write("\n"+"Bump Night:Day\t" + ncout[0][3] +":" + cout[0][3] );
                    int total=0;;
                    for(int j=0;j<100;j++)
                        total=total+hset[j];

                    String[] value=new String[4];
                    value[0]=String.format("%.1f", (double)(ncout[0][0]+cout[0][0])/total);
                    value[1]=String.format("%.1f", (double)(ncout[0][1]+cout[0][1])/total);
                    value[2]=String.format("%.1f", (double)(ncout[0][2]+cout[0][2])/total);
                    value[3]=String.format("%.1f", (double)(ncout[0][3]+cout[0][3])/total);
                    bufWriter.write("\nDec perTime\t" + value[0]);
                    bufWriter.write("\n"+"Inc perTime\t" + value[1]  );
                    bufWriter.write("\n"+"Acc perTime\t" + value[2] );
                    bufWriter.write("\n"+"Bump perTime\t" + value[3]);
                    for(int j=1;j<100;j++) {
                        int key=check[j][0];
                        if(key==-1)
                            break;
                        bufWriter.write("\n"+(j)+"time");
                        value[0]=String.format("%.1f", (double)(ncout[j][0]+cout[j][0])/hset[j]);
                        value[1]=String.format("%.1f", (double)(ncout[j][1]+cout[j][1])/hset[j]);
                        value[2]=String.format("%.1f", (double)(ncout[j][2]+cout[j][2])/hset[j]);
                        value[3]=String.format("%.1f", (double)(ncout[j][3]+cout[j][3])/hset[j]);
                        bufWriter.write("\nDec perTime\t" + value[0]);
                        bufWriter.write("\n"+"Inc perTime\t" + value[1]  );
                        bufWriter.write("\n"+"Acc perTime\t" + value[2] );
                        bufWriter.write("\n"+"Bump perTime\t" + value[3]);
                        for(int n=1;n<key+1;n++) {
                            int set=check[j][n];
                            if(flag[j][n]==2) {
                                int ro,ti;
                                ti= source[set][0];
                                ro = source[set][1];
                                bufWriter.write("\n"+flag[j][n]+"\t"+arr[j][ti][0]
                                        +"\t"+map[ro][0][1][1]+"\t"+map[ro][0][1][0]
                                        +"\t"+map[ro][1][1][1]+"\t"+map[ro][1][1][0]);
                            }
                            else
                                bufWriter.write("\n"+flag[j][n]+"\t"+arr[j][set][0]+"\t"+arr[j][set][2] + "\t" + arr[j][set][3]);
                        }
                    }
                    bufWriter.close();
                }catch (FileNotFoundException e) {
                }catch(IOException e) {
                    //System.out.println(e);
                }
                startActivity(intent);
                finish();
            }
        });
    }
}
