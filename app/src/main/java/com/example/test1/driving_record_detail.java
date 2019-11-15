package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class driving_record_detail extends AppCompatActivity {

    Button back_to_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driving_record_detail);
        back_to_main = findViewById(R.id.back_to_main);

        back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), record_view.class);
                startActivity(intent);
            }
        });
    }
}
