package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

//import com.google.android.gms.maps.model.PolylineOptions;

public class driving_record_detail extends AppCompatActivity
        implements
            OnMapReadyCallback {
    private GoogleMap mMap;
    //private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
    //private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점
    Button back_to_main;
    TableLayout tableLayout;
    int text_count=0;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String tcount = intent.getStringExtra("count");
        text_count=Integer.parseInt(tcount);
        setContentView(R.layout.driving_record_detail);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        back_to_main = findViewById(R.id.back_to_main);
        tableLayout = findViewById(R.id.tableLayout);
        tv1 = findViewById(R.id.textView10);
        tv2 = findViewById(R.id.textView6);
        tv3 = findViewById(R.id.textView7);
        tv4 = findViewById(R.id.textView8);
        mapFragment.getMapAsync(this);

        back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), record_view.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        int count=0;
        int Pin_count=0;
        LatLng DEST=new LatLng(37.48354974741023, 127.08220042849793);
        mMap = googleMap;
        try {
            String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";
           //System.out.println(getExternalFilesDir(null)+"/format_example_en.txt");
            File file = new File(foldername+"/result.txt");//파일의 경로, 추후 수정. 임시 데이터 위치에서 진행.
            //파일의 경우 핸드폰에 있는 txt파일을 읽어오도록 함. 경로의 경우 /storage/emulated/0/Android/data/com.example.test1/files/format_example_en.txt
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            LatLng pre_Pin=new LatLng(37.48354974741023, 127.08220042849793);;
            while ((line = bufReader.readLine()) != null) {
                System.out.println(line);
                if(line.contains("time")) {
                    count = Integer.parseInt(line.replaceFirst("time", ""));//System.out.println(line.replaceFirst("time", ""));
                }
                if(count!=text_count) continue;//TODO:0말고 앞 activity에서 넘어온 값을 줘야 함.
                if(!line.contains("time") && line.charAt(0)>='0' && line.charAt(0)<'9'){
                    String[] line_array = line.split("\\s+");//3,4
                    float a, b;
                    String tmp;
                    a=Float.parseFloat(line_array[3]);
                    //tmp=String.format("%.6f", a);
                    //a=Float.parseFloat(tmp);
                    b=Float.parseFloat(line_array[4]);
                    //tmp=String.format("%.6f", b);
                    //b=Float.parseFloat(tmp);
                    LatLng new_Pin=new LatLng(a,b);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new_Pin);
                    int flag=Integer.parseInt(line_array[0]);
                    if(flag==0) {
                        markerOptions.title("급감속");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    }
                    if(flag==1) {
                        markerOptions.title("급가속");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }
                    if(flag==2) {
                        markerOptions.title("과속");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    }
                    if(flag==3) {
                        markerOptions.title("방지턱");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    }
                    markerOptions.snippet(Integer.toString(Pin_count+1)+"번째 위반");
                    if(flag==2) markerOptions.snippet(Integer.toString(Pin_count+1)+"번째 위반, 과속끝");
                    mMap.addMarker(markerOptions);
                    if(flag==2){
                        a=Float.parseFloat(line_array[5]);
                        b=Float.parseFloat(line_array[6]);
                        LatLng tPin=new LatLng(a,b);
                        markerOptions.position(tPin);
                        markerOptions.snippet(Integer.toString(Pin_count+1)+"번째 위반, 과속시작");
                        mMap.addMarker(markerOptions);
                        mMap.addPolyline(new PolylineOptions().add(tPin, new_Pin).width(20).color(Color.rgb(0,0,102)));
                    }
                    if(Pin_count==0) {
                        DEST=new_Pin;
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(DEST));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                    }
                    //else mMap.addPolyline(new PolylineOptions().add(pre_Pin, new_Pin).width(5).color(Color.RED));
                    pre_Pin=new_Pin;
                    Pin_count++;
                }
                if(line.contains("perTime")){
                    String[] line_array = line.split("\\s+");
                    float tf=Float.parseFloat(line_array[2]);
                    int ti=Math.round(tf);
                    if(line_array[0].contains("Dec")) tv3.setText(ti+"회");
                    if(line_array[0].contains("Inc")) tv2.setText(ti+"회");
                    if(line_array[0].contains("Acc")) tv1.setText(ti+"회");
                    if(line_array[0].contains("Bump")) tv4.setText(ti+"회");
                }
            }
            bufReader.close();
        }catch (FileNotFoundException e) {
            ;
        }catch(IOException e){
            System.out.println(e);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(DEST));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
       // Polyline polyline1 = mMap.addPolyline(new PolylineOptions().add(SOGANG, DEST).width(5).color(Color.RED));
    }
    /*private void drawPath(){        //polyline을 그려주는 메소드
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(15).color(Color.BLACK).geodesic(true);
        polylines.add(mMap.addPolyline(options));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 18));
    }*/

}
