package com.example.xiaoxiazheng.socialnetworkapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;



public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_username, et_password, et_confPsw;
    String s_username, s_password, s_confPsw;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = (EditText) findViewById(R.id.editTextR_Username);
        et_password = (EditText) findViewById(R.id.editTextR_Password);

        register = (Button) findViewById(R.id.buttonR_Register);
        register.setOnClickListener(this);
    }


    //When click the button, the asynctask begins.
    @Override
    public void onClick(View v)
    {
        s_username = et_username.getText().toString();
        s_password = et_password.getText().toString();
        s_confPsw = et_confPsw.getText().toString();

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(s_password, s_password);
    }


    //Background task to communicate with the server.
    public class BackgroundTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);

        public BackgroundTask() {
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Login Information.");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String reg_url = "http://mpss.csce.uark.edu/~xiaoxia1/register.php";

            String username = params[0];
            String password = params[1];

            try {
                    URL url = new URL(reg_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);


                //Using stream to write data into server.
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                    String message = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                            URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                    bufferedWriter.write(message);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream IS = httpURLConnection.getInputStream();
                    IS.close();

                    httpURLConnection.disconnect();
                    return "Registration Success!";

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


        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Registration Success!")) {
                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(RegisterActivity.this, "Registration Fail", Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();

        }
    }

}
