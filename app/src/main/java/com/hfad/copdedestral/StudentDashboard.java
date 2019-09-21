package com.hfad.copdedestral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentDashboard extends AppCompatActivity {

    TextView detailsTextView;
    String hostelid;
    String roll;


    public void giveFeedback(View view){
        Intent intent = new Intent(getApplicationContext(),FeedbackActiivity.class);
        intent.putExtra("hostelid",hostelid);
        intent.putExtra("rollno",roll);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logoutmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                return true;

        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

         detailsTextView = (TextView)findViewById(R.id.detailsTextView);

        Intent intent = getIntent();

        String result = intent.getStringExtra("result");
       //Toast.makeText(getApplicationContext(),"text : " + result,Toast.LENGTH_LONG).show();
        //int name = intent.getIntExtra("name",1);

        JSONObject object  = null;
        try {

            object = new JSONObject(result);
            Toast.makeText(getApplicationContext(),"text : " + object.toString(),Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
           // Toast.makeText(this, "heello !", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //JSONObject object ;

     /*   try {
            object= obj.getJSONObject("studentdetails");
            Toast.makeText(getApplicationContext(),"text : " + object,Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }  */

       String name = null;
        String email = null;
        String phone = null;
        hostelid = null;
        String accountBalance = null;
        roll  = null;
      //  try {
        try {
            name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            roll = object.getString("rollno");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            email = object.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            phone = object.getString("phoneno");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            hostelid = object.getString("hostelid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            accountBalance = object.getString("accountBal");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String  ans = "STUDENT DETAILS \n";

        ans += ("Name : " + name + "\n");
        ans += ("Roll No. : " + roll + "\n");
        ans += ("Hostel : " + hostelid + "\n");
        ans += ("Phone : " + phone + "\n");
        ans += ("Email : " + email + "\n\n");

        ans += ("ACCOUNT BALANCE : " + accountBalance + "\n");

        Toast.makeText(this, "here : " + ans, Toast.LENGTH_SHORT).show();


        detailsTextView.setText(ans);

    }
    
    
}
