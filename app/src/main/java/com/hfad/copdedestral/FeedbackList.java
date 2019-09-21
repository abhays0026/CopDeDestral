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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;

import static com.hfad.copdedestral.StudentSignUpActivity.LOG_TAG;

public class FeedbackList extends AppCompatActivity {

    String hostelid;
    ListView feedbackList ;
    String rn;
    ArrayList<String> rollNumbers = new ArrayList<>();
    ArrayList<String> feedbaclId = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;

    Intent intent;

    @Override
    protected void onResume() {
        super.onResume();

        if (checkNetworkConnection()) {
            HTTPAsyncTask task = new HTTPAsyncTask();
            task.execute("https://us-central1-hacknitj-bae7f.cloudfunctions.net/app/GetAllFeedback");

        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

        feedbackList.setAdapter(itemsAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        intent = getIntent();
        hostelid = intent.getStringExtra("hostelid");

        rn = intent.getStringExtra("rollno");

        feedbackList = (ListView)findViewById(R.id.feedbackList);

        if (checkNetworkConnection()) {
            HTTPAsyncTask task = new HTTPAsyncTask();
            task.execute("https://us-central1-hacknitj-bae7f.cloudfunctions.net/app/GetAllFeedback");

        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();



        itemsAdapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);

        //feedbackList.setAdapter(itemsAdapter);

        feedbackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),HostelVote.class);
                intent.putExtra("id", feedbaclId.get(position));
                intent.putExtra("hostelid",hostelid);
                intent.putExtra("rollno", rn);
                intent.putExtra("content",content.get(position));
                Toast.makeText(getApplicationContext(),"send h id " + hostelid,Toast.LENGTH_SHORT).show();
                startActivity(intent);
                //startActivityForResult(intent);

            }
        });


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


            rollNumbers.clear();
            arrayList.clear();
            feedbaclId.clear();

            JSONArray arr = null;


            try {
                arr = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0 ;i<arr.length();++i){

                JSONObject obj = null;
                try {
                    obj = arr.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String id=null;

                String cont = null;
                String rollno=null;
                String tit = null;
                int upvotecount=0;
                int downvotecount=0;

                try {
                    cont = obj.getString("content");
                    content.add(cont);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    id = obj.getString("id");
                    feedbaclId.add(id);
                    //feedbaclId.add(hos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    rollno = obj.getString("rollno");
                    rollNumbers.add(rollno);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    tit = obj.getString("title");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    upvotecount = obj.getInt("upVoteCount");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    downvotecount = obj.getInt("downVoteCount");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String display = "";
                display +=("Roll No. : " + rollno + "\n");
                display += ("Title : " + tit + "\n");
                display +=( "UPVOTES :" + upvotecount+"\t\t\t\t");
                display += ("DOWNVOTES :" + downvotecount + "\n");
               // display += ("Room no. : " + room + "\n");

                arrayList.add(display);
            }
            feedbackList.setAdapter(itemsAdapter);

        }
    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.accumulate("hostelid",hostelid);
        // jsonObject.accumulate("name", etName.getText().toString());
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
