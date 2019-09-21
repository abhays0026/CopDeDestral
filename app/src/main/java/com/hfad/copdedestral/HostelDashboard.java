package com.hfad.copdedestral;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.TestLooperManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;

import static com.hfad.copdedestral.StudentSignUpActivity.LOG_TAG;

public class HostelDashboard extends AppCompatActivity {

    Intent intent;

    TextView hostelDetailsTextView;
    RelativeLayout changePasswordRelativeLayout;
    RelativeLayout hostelRalativeLayout;
    String oldPassword;

    String help;

    Button hostelStudentsList;
    Button hostelComplaints;
    Button verifyStudentsButton;

    EditText newPassword ;
    Button changedPassword ;
    String hostelId;


    private static final int MY_PASSWORD_DIALOG_ID = 4;


    public  void studentsToBeVerified(View view){

        intent = new Intent(getApplicationContext(),StudentsToBeVerified.class);
        startActivity(intent);

    }

    public void hostelComplaints(View view){

        intent = new Intent(getApplicationContext(),HostelComplaints.class);
        startActivity(intent);

    }

    public  void studentsList(View view){

        intent = new Intent(getApplicationContext(),StudentsList.class);
        intent.putExtra("hostelId",hostelId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hostelmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.changePassword:

                changePassword();

                return true;

            case R.id.hostelActivityLogout:
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                return true;


        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_dashboard);

       // changePasswordRelativeLayout.setVisibility(View.INVISIBLE);

        hostelStudentsList = (Button)findViewById(R.id.hostelStudentList);
        hostelComplaints = (Button) findViewById(R.id.hostelComplaints);
        verifyStudentsButton = (Button)findViewById(R.id.verifyStudentsButton);
        hostelDetailsTextView = (TextView)findViewById(R.id.hostelDetailsTextView);
        //newPassword = (EditText)findViewById(R.id.newHostelPassword);
        //changedPassword = (Button)findViewById(R.id.changedPassword);
        hostelRalativeLayout = (RelativeLayout)findViewById(R.id.hostelRelativeLayout);


        intent = getIntent();

        String result = intent.getStringExtra("result");
        oldPassword = intent.getStringExtra("oldPassword");

        // Toast.makeText(getApplicationContext(),"text : " + result,Toast.LENGTH_LONG).show();
        //int name = intent.getIntExtra("name",1);

        help = result;

        JSONObject object  = null;

        try {

            object = new JSONObject(result);

        } catch (JSONException e) {
            // Toast.makeText(this, "heello !", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //String hostelId = null;

        try {
            hostelId = object.getString("hostelid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String  ans = "HOSTEL :" + hostelId + "\n";

        hostelDetailsTextView.setText(ans);

    }


    public void changePassword() {


        new AlertDialog.Builder(HostelDashboard.this)
                .setIcon(android.R.drawable.alert_dark_frame)
                .setTitle("Are you sure ?")
                .setMessage("Do you want to change the password?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        JSONObject jsonObject = new JSONObject();

                        JSONObject object  = null;

                        try {

                            object = new JSONObject(help);

                        } catch (JSONException e) {
                            // Toast.makeText(this, "heello !", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        String hostelId = null;

                        try {
                            hostelId = object.getString("hostelid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(),ChangePasswordActivity.class);
                        try {
                            intent.putExtra("hostelId" +
                                    "d",object.getString("hostelid"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("oldPassword",oldPassword);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No",null).show();

    }

}
