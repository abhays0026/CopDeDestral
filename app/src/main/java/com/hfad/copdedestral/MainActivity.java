package com.hfad.copdedestral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button student;
    Button mess;
    Button hostel;

    public void onBackPressed() {
        //do nothing
    }


    public void studentActivity(View view){

        Intent intent = new Intent(this,StudentActivity.class);
        startActivity(intent);

    }
    public  void messActivity(View view){
        Intent intent = new Intent(this, MessActivity.class);
        startActivity(intent);
    }

    public void hostelActivity(View view){
        Intent intent = new Intent(this,HostelActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        student = (Button)findViewById(R.id.studentAccount);
        mess = (Button)findViewById(R.id.messAccount);
        hostel = (Button)findViewById(R.id.hostelAccount);




    }
}
