package com.example.xiaoxiazheng.socialnetworkapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends Activity {

    EditText username, password;
    Button login, register;
    private String l_username, l_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        //Get text from the UI xml file.
        username = (EditText) findViewById(R.id.editTextUsername);
        password = (EditText) findViewById(R.id.editTextPassword);

        //When click the login button, it begin to run the asynctask to communicate with the Mysql server.
        login = (Button) findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_ = username.getText().toString();
                String password_ = password.getText().toString();

                BackgroundTask backgroundTask = new BackgroundTask();
                backgroundTask.execute(username_, password_);
                finish();
            }
        });

        //When click the register button, it will open the register activity
        register = (Button) findViewById(R.id.buttonRegister);
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }


    //Background task to communicate with the Mysql server
    public class BackgroundTask extends AsyncTask<String, Void, String> {

        public BackgroundTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://mpss.csce.uark.edu/~xiaoxia1/login.php";

            l_username = params[0];
            l_password = params[1];

            try {
                URL url = new URL(login_url);
                HttpURLConnection httpsURLConnection = (HttpURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setDoInput(true);

                //Use the Stream to write data into server.
                OutputStream outputStream = httpsURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String message = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(l_username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(l_password, "UTF-8");

                bufferedWriter.write(message);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpsURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String response = "";
                String line = "";

                Log.d("Sasha", "user" + l_username);
                Log.d("Sasha", "pass" + l_password);


                //Getting result from the server.
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                Log.d("Sasha", "response" + response);

                bufferedReader.close();
                inputStream.close();
                httpsURLConnection.disconnect();
                return response;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        //Check the data from server. If success then open the map activity.
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("  Login Success!")) {
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.putExtra("username", l_username);
                intent.putExtra("password", l_password);
                startActivity(intent);

            } else {
                Toast.makeText(LoginActivity.this, "Invalid User Name or Password", Toast.LENGTH_LONG).show();
            }
        }
    }




}
