package com.app.gptalk.registration;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.app.gptalk.base.BaseClass;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class RegisterApplicationID extends AsyncTask<Void, Void, String> {

    private Context context;
    private GoogleCloudMessaging googleCloudMessaging;

    private int appVersion;
    private String registrationID = null, username, userType;

    private BaseClass baseClass = new BaseClass();

    // ID acquired from Google API console
    private final String SENDER_ID = "216858060430";

    public RegisterApplicationID(Context context, GoogleCloudMessaging googleCloudMessaging, int appVersion, String username, String userType) {

        this.context = context;
        this.googleCloudMessaging = googleCloudMessaging;
        this.appVersion = appVersion;
        this.username = username;
        this.userType = userType;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        String message = "";

        try {
            if (googleCloudMessaging == null) {
                googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
            }

            registrationID = googleCloudMessaging.register(SENDER_ID);
            message = "Device registered";

            sendRegistrationIDToBackend();
            storeRegistrationID(context, registrationID, username, userType);
        } catch (IOException e) {
            baseClass.errorHandler(e);
        }

        return message;
    }

    private void storeRegistrationID(Context context, String registrationID, String username, String userType) {

        final SharedPreferences sharedPreferences = context.getSharedPreferences(RegisterApplicationID.class.getSimpleName(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("user_type", userType);
        editor.putString("registration_id", registrationID);
        editor.putInt("appVersion", appVersion);
        editor.commit();
    }

    private void sendRegistrationIDToBackend() {

        URI url = null;

        try {
            // Upload the registration ID to the remote database
            url = new URI("http://www.gptalk.ie/WAMP/gcm_server_php/register.php?username=" + username + "&user_type=" + userType + "&registration_id=" + registrationID);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(url);

        try {
            httpClient.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        super.onPostExecute(result);
    }
}
