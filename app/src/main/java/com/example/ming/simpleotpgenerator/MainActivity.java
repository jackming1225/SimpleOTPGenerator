package com.example.ming.simpleotpgenerator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    EditText number;
    TextView returnText;
    Button genOTP;
    private String status;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = (EditText) findViewById(R.id.numberET);
        genOTP = (Button) findViewById(R.id.generateB);
        returnText = (TextView) findViewById(R.id.returnText);

        genOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = number.getText().toString();
                generateOTP(phoneNumber);
            }
        });
    }

    private void generateOTP(String phoneNumber) {

        JsonTask(phoneNumber);


    }

    private void startVerifyActivity(String getSessionId) {

        Intent intent = new Intent(this, VerifyOtpActivity.class);
        intent.putExtra("sessionID", getSessionId);
        startActivity(intent);
    }


    String json_string = "";


    public String JsonTask(final String number) {

        new AsyncTask<String, String, String>() {


            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            @Override
            protected String doInBackground(String... params) {
                try {
                    //creating client object
                    URL url = new URL("https://2factor.in/API/V1/32d8c4b1-abbc-11e6-a40f-00163ef91450/SMS/" + number + "/AUTOGEN");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();


                    //using httppost to use parameter url
                    InputStream inputStream = urlConnection.getInputStream();

                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    //read contect and display
                    StringBuilder stringBuilder = new StringBuilder();
                    String newLine = "";

                    while ((newLine = bufferedReader.readLine()) != null) {
                        stringBuilder.append(newLine);
                        //stringBuffer.append("\n");
                    }
                    return stringBuilder.toString().trim();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //Close content
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    try {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                json_string = result;

                returnText.setText(json_string);

                getSessionId(json_string);


            }
        }.execute();
        return json_string;
    }

    private void getSessionId(String json_string) {

        try {
            JSONArray jsonArray = new JSONArray("["+json_string+"]");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                status = jsonObject.getString("Status");
                sessionId = jsonObject.getString("Details");


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        startVerifyActivity(sessionId);


    }
}
