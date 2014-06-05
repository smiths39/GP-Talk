package com.app.gptalk.base;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JSONParser {

    private static InputStream inputStream = null;
    private static JSONObject jsonObject = null;
    private static String json = "";

    // Encoding defined for reading input
    private static final String characterEncoding = "iso-8859-1";

    public JSONObject makeHttpRequest(String url, String method, ArrayList<NameValuePair> parameters) {

        establishConnection(url, method, parameters);
        readJSONInput();

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void establishConnection(String newUrl, String newMethod, ArrayList<NameValuePair> parameters) {

        try {

            if (newMethod == "POST") {

                // Establish connection with remote server and creating the retrieval of data
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(newUrl);
                httpPost.setEntity(new UrlEncodedFormEntity(parameters));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            } else if(newMethod == "GET") {

                // Establish connection with remote server and the submission of data
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String parameterString = URLEncodedUtils.format(parameters, "utf-8");
                newUrl += "?" + parameterString;
                HttpGet httpGet = new HttpGet(newUrl);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readJSONInput() {

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, characterEncoding), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String nextLine = null;

            // Read each line of data retrieved
            while ((nextLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(nextLine + "\n");
            }

            inputStream.close();
            json = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
