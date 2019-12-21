package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class driving_record extends AppCompatActivity {

    ToggleButton driving;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driving_record);

        driving = findViewById(R.id.driving);

        driving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == false) {
                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    startActivity(intent);
                }
            }
        });
    }
}