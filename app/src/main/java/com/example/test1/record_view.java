package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class record_view extends AppCompatActivity {

    Button  back_to_main;
    Button  record_detail;
    EditText textInputEditText;
    TextView a_inc, a_dec, a_acc, a_bump, dn_inc, dn_dec, dn_acc, dn_bump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_view);
        a_inc = (TextView)findViewById(R.id.textView6);
        a_dec = (TextView)findViewById(R.id.textView7);
        a_acc = (TextView)findViewById(R.id.textView10);
        a_bump = (TextView)findViewById(R.id.textView8);
        dn_inc = (TextView)findViewById(R.id.textView14);
        dn_dec = (TextView)findViewById(R.id.textView16);
        dn_acc = (TextView)findViewById(R.id.textView12);
        dn_bump = (TextView)findViewById(R.id.textView18);
        try{
            String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";
            int cnt = 0;
            String line = "";
            String tmline[][] = new String[8][];
            String tmpline[][] = new String[8][];
            File inp = new File(foldername+"/result.txt");
            FileReader filereader = new FileReader(inp);
            BufferedReader bufReader = new BufferedReader(filereader);
            while((line = bufReader.readLine()) != null)
            {
                tmline[cnt] = line.split(" ");
                tmpline[cnt] = tmline[cnt][1].split("\t");
                if(cnt == 0)
                    dn_dec.setText(tmpline[cnt][1]);
                else if(cnt == 1)
                    dn_inc.setText(tmpline[cnt][1]);
                else if(cnt == 2)
                    dn_acc.setText(tmpline[cnt][1]);
                else if(cnt == 3)
                    dn_bump.setText(tmpline[cnt][1]);
                else if(cnt == 4)
                    a_dec.setText(tmpline[cnt][1]+"회/시간");
                else if(cnt == 5)
                    a_inc.setText(tmpline[cnt][1]+"회/시간");
                else if(cnt == 6)
                   a_acc.setText(tmpline[cnt][1]+"회/시간");
                else if(cnt == 7)
                    a_bump.setText(tmpline[cnt][1]+"회/시간");
                cnt++;
                if(cnt >= 8)
                    break;
            }
            bufReader.close();
        }catch (FileNotFoundException e) {
        }catch(IOException e){
            //System.out.println(e);
        }

        textInputEditText = (EditText)findViewById(R.id.textInputEditText);
        back_to_main = findViewById(R.id.back_to_main);

        back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intent);
            }
        });

        record_detail = findViewById(R.id.record_detail);

        record_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), driving_record_detail.class);
                intent.putExtra("count",textInputEditText.getText().toString());
                startActivity(intent);
            }
        });
    }
}
