package com.hfad.copdedestral;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

import static com.hfad.copdedestral.StudentSignUpActivity.LOG_TAG;

public class StudentsApproval extends AppCompatActivity {

    TextView studentsAppralTextView ;
    String hostelid;
    String roll;
    String api;

    Intent intent;
    //String hostelId;


    public  void studentsList(View view){

        intent = new Intent(getApplicationContext(),StudentsList.class);
        intent.putExtra("hostelId",hostelid);
        startActivity(intent);
    }


    boolean isConfirmed = false;

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.approve:
                if (checked) {

                    isConfirmed = true;

                    api = "https://us-central1-hacknitj-bae7f.cloudfunctions.net/app/hostelVerifyStudent";

                    if (checkNetworkConnection()) {
                        HTTPAsyncTask task = new HTTPAsyncTask();
                        task.execute(api);

                    } else
                        Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

                    finish();




                }
                    break;
            case R.id.disApprove:
                if (checked) {

                    isConfirmed = false;

                    api = "https://us-central1-hacknitj-bae7f.cloudfunctions.net/app/hostelRejectStudent";

                    if (checkNetworkConnection()) {
                        HTTPAsyncTask task = new HTTPAsyncTask();
                        task.execute(api);

                    } else
                        Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

                    finish();


                }
                    break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_approval);

        studentsAppralTextView = (TextView)findViewById( R.id.studentsApprovalTextView);

        String display = "";

        Intent intent = getIntent();
         hostelid = intent.getStringExtra("hostelid");
         roll = intent.getStringExtra("rollno");

        display += ("Hostel : " + hostelid + "\n");
        display += ("Roll Number : " + roll + "\n");

        studentsAppralTextView.setText(display);
    }

    // check network connection
    protected boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {

        } else {
            // show "Not Connected"
            ///tvIsConnected.setText("Not Connected");
            // change background color to green
            ///tvIsConnected.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }


    private String httpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();

        // Toast.makeText(getApplicationContext(),"this object sent : " + jsonObject.toString(),Toast.LENGTH_SHORT).show();

        // 3. add JSON content to POST request body
        result = setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();


        if (conn.getResponseCode() == 200) {
            return result;
        }
        return null;
        //return jsonObject.toString();
        // 5. return response message
        // return conn.getResponseMessage()+"";

    }


    private String setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();

        String jsonResponse = "";

        //if the url is null , then return
        if (conn == null) {
            return jsonResponse;
        }

        //HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            //urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setReadTimeout(10000 /* milliseconds */);
            //urlConnection.setConnectTimeout(15000 /* milliseconds */);
            //urlConnection.connect();

            //if the request was successful (response code 200)
            // then read the input stream and parse the response.
            if (conn.getResponseCode() == 200) {
                inputStream = conn.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + conn.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            //   if (conn != null) {
            //urlConnection.disconnect();
            // }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;

    }


    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return httpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            JSONObject object = null;

            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String status = null;
            try {
                status = object.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status.equals("ok") && isConfirmed == true){
                Toast.makeText(getApplicationContext(),"APPLICATION APPROVED !",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"APPLICATION NOT APPROVED !",Toast.LENGTH_LONG).show();
            }

        }
    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.accumulate("hostelid",hostelid);
         jsonObject.accumulate("rollno",roll);
        //jsonObject.accumulate("country",  etCountry.getText().toString());
        //jsonObject.accumulate("twitter",  etTwitter.getText().toString());
        return jsonObject;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }





}
