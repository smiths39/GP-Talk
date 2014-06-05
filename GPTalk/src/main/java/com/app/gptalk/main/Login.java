package com.app.gptalk.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.base.JSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Login extends Activity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private TextView tvSignUp;
    private Button bLogin;
    private ProgressDialog progressDialog;

    private BaseClass baseClass = new BaseClass();
    private JSONParser jsonParser = new JSONParser();

    // PHP script located on remote server to handle login verification
    private static final String LOGIN_URL = "http://www.gptalk.ie/gptalkie_registered_users/login.php";
    private static final String MESSAGE = "message";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initialiseWidgets();
        launchAlertDialog();

        // Hide characters of password input
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
    }

    public boolean isConnectingToInternet() {

        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {

                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void initialiseWidgets() {

        etUsername = (EditText) findViewById(R.id.etLoginUsername);
        etPassword = (EditText) findViewById(R.id.etLoginPassword);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        bLogin = (Button) findViewById(R.id.bLogin);

        bLogin.setOnClickListener(Login.this);
    }

    private void launchAlertDialog() {

        // Launch appropriate registration response based on option chosen
        tvSignUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Registration");
                builder.setMessage("Are you a General Practitioner?");

                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Intent launchIntent = new Intent("com.app.gptalk.registration.REGISTRATIONPATIENT");
                        startActivity(launchIntent);
                    }
                });

                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Intent launchIntent = new Intent("com.app.gptalk.registration.REGISTRATIONGP");
                        startActivity(launchIntent);
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public void onClick(View selection) {

        switch (selection.getId()) {

            case R.id.bLogin:

                if(isConnectingToInternet()) {

                    try {
                        new AttemptLogin().execute();
                    } catch (Exception e) {
                        baseClass.errorHandler(e);
                    }
                } else {
                    Toast.makeText(this, "Please enable Wi-Fi", Toast.LENGTH_LONG).show();
                }


                break;
        }
    }

    private class AttemptLogin extends AsyncTask<String, String, String> {

        private int loginSuccess;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(Login.this, R.style.Theme_MyDialog);
            baseClass.initialiseProgressDialog(progressDialog, "Logging in...");
        }

        @Override
        protected String doInBackground(String... args) {

            String loginUsername = etUsername.getText().toString();
            String loginPassword = etPassword.getText().toString();

            try {
                ArrayList<NameValuePair> loginCredentials = new ArrayList<NameValuePair>();
                loginCredentials.add(new BasicNameValuePair("username", loginUsername));
                loginCredentials.add(new BasicNameValuePair("password", loginPassword));

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", loginCredentials);
                loginSuccess = json.getInt("success");

                // Login is successful if a record matching a patient (1) or a GP (2) is found
                // Send username to next page for the successful operation of features
                if (loginSuccess == 1) {

                    Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTHOMEPAGE");
                    userHomepageIntent.putExtra("username", loginUsername);
                    startActivity(userHomepageIntent);
                    return json.getString(MESSAGE);
                } else if (loginSuccess == 2) {

                    Intent gpHomepageIntent = new Intent("com.app.gptalk.homepage.gp.GPHOMEPAGE");
                    gpHomepageIntent.putExtra("username", loginUsername);
                    startActivity(gpHomepageIntent);
                    return json.getString(MESSAGE);
                } else {

                    // No matching records were found
                    return json.getString(MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String fileURL) {

            progressDialog.dismiss();

            // Display message indicating why login was unsuccessful
            if (fileURL != null) {
                Toast.makeText(Login.this, fileURL, Toast.LENGTH_LONG).show();
            }
        }
    }
}