package com.royarijit998.vlearnit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView profileImg;
    private EditText unameEditText, fnameEditText, countryEditText;
    private Button saveInfoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        profileImg = findViewById(R.id.profileImg);
        unameEditText = findViewById(R.id.unameEditText);
        fnameEditText = findViewById(R.id.fnameEditText);
        countryEditText = findViewById(R.id.countryEditText);
        saveInfoBtn = findViewById(R.id.saveInfoBtn);

    }
}
