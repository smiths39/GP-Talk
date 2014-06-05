package com.app.gptalk.option;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

public class OptionWebsite extends Activity {

    private String username, userType, url;
    private BaseClass baseClass = new BaseClass();

    private final static String GPTALK_URL = "http://www.gptalk.ie";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_website);

        getIntentDetails();

        WebView webView = (WebView) findViewById(R.id.webView1);
        enableWebViewSettings(webView);

        webView.setWebViewClient(new webClient());

        if (url.isEmpty() || url.length() == 0) {
            webView.loadUrl(GPTALK_URL);
        } else {
            webView.loadUrl(url);
        }
    }

    private void getIntentDetails() {

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userType = intent.getStringExtra("user_type");

        if(getIntent().getExtras().getString("url") != null) {
            url = intent.getStringExtra("url");
        } else {
            url = "";
        }
    }

    private void enableWebViewSettings(WebView newWebView) {

        newWebView.getSettings().setJavaScriptEnabled(true);
        newWebView.getSettings().setLoadWithOverviewMode(true);
        newWebView.getSettings().setUseWideViewPort(true);
        newWebView.getSettings().setBuiltInZoomControls(true);
        newWebView.getSettings().setDisplayZoomControls(true);
    }

    // Launch preference menu
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionalmenu, menu);
        return true;
    }

    public void launchOptionActivity(String destination) {

        try {
            Intent launchIntent = new Intent("com.app.gptalk.option." + destination);
            launchIntent.putExtra("username", username);
            launchIntent.putExtra("user_type", userType);
            startActivity(launchIntent);
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    // Launch preference menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.optionLaunch:
                return true;

            case R.id.optionHome:

                if (userType.equals("patient")) {
                    Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTHOMEPAGE");
                    userHomepageIntent.putExtra("username", username);
                    startActivity(userHomepageIntent);
                } else {
                    Intent gpHomepageIntent = new Intent("com.app.gptalk.homepage.gp.GPHOMEPAGE");
                    gpHomepageIntent.putExtra("username", username);
                    startActivity(gpHomepageIntent);
                }
                return true;

            case R.id.optionAccount:

                if (userType.equals("patient")) {
                    launchOptionActivity("OPTIONPATIENTACCOUNT");
                } else {
                    launchOptionActivity("OPTIONGPACCOUNT");
                }
                return true;

            case R.id.optionFAQ:
                launchOptionActivity("OPTIONFAQ");
                return true;

            case R.id.optionSupport:
                launchOptionActivity("OPTIONSUPPORT");
                return true;

            case R.id.optionSettings:
                launchOptionActivity("OPTIONSETTINGS");
                return true;

            case R.id.optionWebsite:
                launchOptionActivity("OPTIONWEBSITE");
                return true;

            case R.id.exit:
                Intent loginIntent = new Intent("com.app.gptalk.main.LOGIN");
                startActivity(loginIntent);
                return true;
        }

        return false;
    }

    private class webClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }
    }
}