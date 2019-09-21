package com.hfad.copdedestral;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import static com.hfad.copdedestral.StudentSignUpActivity.LOG_TAG;

public class FeedbackActiivity extends AppCompatActivity {

    Button feedbackSubmit;
    EditText feedbackTypeEditText;
    String hostelid;
    EditText feedbackTitle;
    String rollno;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feedback_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.checkAllFeedbacks:
                Intent intent = new Intent(getApplicationContext(),FeedbackList.class);
                intent.putExtra("hostelid",hostelid);
                intent.putExtra("title",feedbackTitle.getText().toString());
                intent.putExtra("rollno",rollno);
                startActivity(intent);
                return true;

        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_actiivity);

        Intent intent = getIntent();
        hostelid = intent.getStringExtra("hostelid");
        rollno = intent.getStringExtra("rollno");

        Toast.makeText(getApplicationContext(),"id " + hostelid + "roll  " + rollno,Toast.LENGTH_LONG).show();

        feedbackSubmit = (Button)findViewById(R.id.sacFeedbackSubmit);
        feedbackTitle = (EditText) findViewById(R.id.Title);

        feedbackTypeEditText = (EditText)findViewById(R.id.feedbackTypeEditText);

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

        // 3. add JSON content to POST request body
        result = setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();


        if(conn.getResponseCode() == 200){
            return result;
        }
        return null;
        //return jsonObject.toString();
        // 5. return response message
        // return conn.getResponseMessage()+"";

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

          Toast.makeText(getApplicationContext(),"FEEDBACK SUCCESSFULLY SUBMITTED !" ,Toast.LENGTH_SHORT).show();
          finish();


        }
    }


    public void submitFeedback(View view) {

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(feedbackSubmit.getWindowToken(), 0);

        //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        // perform HTTP POST request
        if(checkNetworkConnection()) {
            HTTPAsyncTask task = new HTTPAsyncTask();
            task.execute("https://us-central1-hacknitj-bae7f.cloudfunctions.net/app/studentAddFeedback");

        }
        else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.accumulate("hostelid",hostelid);
        jsonObject.accumulate("rollno",rollno);
        //jsonObject.accumulate("title",feedbackTitle.getText().toString());
        jsonObject.accumulate("content",feedbackTypeEditText.getText().toString());
        jsonObject.accumulate("title",feedbackTitle.getText().toString());


        // jsonObject.accumulate("name", etName.getText().toString());
        //jsonObject.accumulate("country",  etCountry.getText().toString());
        //jsonObject.accumulate("twitter",  etTwitter.getText().toString());

        return jsonObject;
    }

    private  String setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();

        String jsonResponse = "";

        //if the url is null , then return
        if(conn==null){
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
            if(conn.getResponseCode()==200){
                inputStream = conn.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG,"Error response code: "+conn.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem retrieving the earthquake JSON results.",e);
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
