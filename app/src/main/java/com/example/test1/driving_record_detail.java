package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.graphics.Color;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driving_record_detail);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        back_to_main = findViewById(R.id.back_to_main);

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
           //System.out.println(getExternalFilesDir(null)+"/format_example_en.txt");
            File file = new File(getExternalFilesDir(null)+"/format_example_en.txt");//파일의 경로, 추후 수정. 임시 데이터 위치에서 진행.
            //파일의 경우 핸드폰에 있는 txt파일을 읽어오도록 함. 경로의 경우 /storage/emulated/0/Android/data/com.example.test1/files/format_example_en.txt
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            LatLng pre_Pin=new LatLng(37.48354974741023, 127.08220042849793);;
            while ((line = bufReader.readLine()) != null) {
                System.out.println(line);
                if(line.contains("time")) {
                    count = Integer.parseInt(line.replaceFirst("time", ""));//System.out.println(line.replaceFirst("time", ""));
                    if(count!=0) continue;//TODO:0말고 앞 activity에서 넘어온 값을 줘야 함.
                }
                else if(line.charAt(0)>='0' && line.charAt(0)<'9'){
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
                    if(flag==0) markerOptions.title("급감속");
                    if(flag==1) markerOptions.title("급가속");
                    if(flag==2) markerOptions.title("가속");
                    if(flag==3) markerOptions.title("방지턱");
                    markerOptions.snippet(Integer.toString(Pin_count)+"번째 위반");
                    if(flag==2) markerOptions.snippet(Integer.toString(Pin_count)+"번째 위반, 가속시작");
                    mMap.addMarker(markerOptions);
                    if(flag==2){
                        a=Float.parseFloat(line_array[5]);
                        b=Float.parseFloat(line_array[6]);
                        LatLng tPin=new LatLng(a,b);
                        markerOptions.position(tPin);
                        markerOptions.snippet(Integer.toString(Pin_count)+"번째 위반, 가속끝");
                        mMap.addMarker(markerOptions);
                        mMap.addPolyline(new PolylineOptions().add(tPin, new_Pin).width(5).color(Color.RED));
                    }
                    if(Pin_count==0) DEST=new_Pin;
                    //else mMap.addPolyline(new PolylineOptions().add(pre_Pin, new_Pin).width(5).color(Color.RED));
                    pre_Pin=new_Pin;
                    Pin_count++;
                }
            }
            bufReader.close();
        }catch (FileNotFoundException e) {
            ;
        }catch(IOException e){
            System.out.println(e);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(DEST));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

       // Polyline polyline1 = mMap.addPolyline(new PolylineOptions().add(SOGANG, DEST).width(5).color(Color.RED));
    }
    /*private void drawPath(){        //polyline을 그려주는 메소드
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(15).color(Color.BLACK).geodesic(true);
        polylines.add(mMap.addPolyline(options));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 18));
    }*/

}
