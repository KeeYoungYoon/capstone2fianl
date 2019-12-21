package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class record_view extends AppCompatActivity {

    Button  back_to_main;
    Button  record_detail;
    EditText textInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_view);

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
