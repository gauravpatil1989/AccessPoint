package com.example.apple.accesspoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuSelectionActivity extends Activity {

    Button exhibitor_btn;
    Button visitorBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_selection);

        exhibitor_btn = (Button) findViewById(R.id.exhibitor_btn);
        visitorBtn = (Button) findViewById(R.id.visitor_btn);

        visitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), StaffLoginActivity.class);
                intent.putExtra("Choice", "V");
                startActivity(intent);
                finish();
            }
        });

        exhibitor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), StaffLoginActivity.class);
                intent.putExtra("Choice", "E");
                startActivity(intent);
                finish();
            }
        });

    }
}
