package com.example.ming.simpleotpgenerator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VerifyOtpActivity extends AppCompatActivity {

    EditText otpEnter;
    Button verifyOTP;
    TextView statusText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        final String SessionId = getIntent().getExtras().getString("sessionID");

        statusText = (TextView) findViewById(R.id.statusText);

        otpEnter = (EditText) findViewById(R.id.otpET);

        verifyOTP = (Button) findViewById(R.id.verifyB);
        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getOTP = otpEnter.getText().toString();
                verifyingOTP(getOTP, SessionId);
            }
        });
    }

    private void verifyingOTP(String getOTP, String SessionID) {

        JsonTask(getOTP, SessionID);

    }


    String json_string = "";


    public String JsonTask(final String getOtp, final String SessionI) {

        new AsyncTask<String, String, String>() {


            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            @Override
            protected String doInBackground(String... params) {
                try {
                    //creating client object
                    URL url = new URL("https://2factor.in/API/V1/32d8c4b1-abbc-11e6-a40f-00163ef91450/SMS/VERIFY/" + SessionI + "/" + getOtp);
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
                //statusText.setText(result);
                getStatusResult(json_string);
            }
        }.execute();
        return json_string;
    }

    private void getStatusResult(String json_string) {

        try {
            JSONArray jsonArray = new JSONArray("["+json_string+"]");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String status = jsonObject.getString("Status");
                String message = jsonObject.getString("Details");

                statusText.setText(status + "\n" + message);

                Intent intent = new Intent();
                intent.putExtra("message",message);
                setResult(RESULT_OK,intent);
                finish();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}