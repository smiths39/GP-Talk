package com.app.gptalk.option;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.custom_adapters.CustomSocialListViewAdapter;
import com.app.gptalk.listview_getter.SupportItem;

import java.util.ArrayList;

public class OptionSupport extends Activity {

    private TextView tvSupportTitle, tvSupportMessage;
    private ListView listView;
    private Typeface font;
    private CustomSocialListViewAdapter adapter;

    private String username, userType;

    private BaseClass baseClass = new BaseClass();

    private ArrayList<SupportItem> socialWebsiteList;

    private String [] websiteListContent = { "Email", "Facebook", "Twitter", "LinkedIn", "Google+" };
    private int [] websiteIconContent = { R.drawable.gmail_logo, R.drawable.facebook_logo, R.drawable.twitter_logo, R.drawable.linkedin_logo, R.drawable.google_plus_logo};

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_support);

        getIntentDetails();
        initialiseVariables();

        font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        setFontStyles(font);

        createCustomListView(listView);
    }

    private void getIntentDetails() {

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userType = intent.getStringExtra("user_type");
    }

    private void createCustomListView(ListView newListView) {

        retrieveSocialWebsiteList();

        adapter = new CustomSocialListViewAdapter(OptionSupport.this, R.layout.social_website_listview_item, socialWebsiteList);
        newListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        newListView.setAdapter(adapter);

        enforceListViewClickListener(newListView);
    }

    private void retrieveSocialWebsiteList() {

        socialWebsiteList = new ArrayList<SupportItem>();

        for (int index = 0; index < websiteListContent.length; index++) {

            String socialItem = websiteListContent[index];
            int socialLogo = websiteIconContent[index];

            SupportItem item = new SupportItem(socialItem, socialLogo);
            socialWebsiteList.add(item);
        }
    }

    private void enforceListViewClickListener(ListView newListView) {

        newListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int websiteID = position;

                if (websiteID == 0) {

                    Intent launchEmail = new Intent(android.content.Intent.ACTION_SEND);
                    launchEmail.setType("plain/text");

                    launchEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"gptalk2014@email.com"});
                    launchEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, "GPTalk Application Assistance");
                    launchEmail.putExtra(android.content.Intent.EXTRA_TEXT, "");

                    startActivity(Intent.createChooser(launchEmail, ""));
                } else if (websiteID == 1) {
                    launchSocialMediaIntent("facebook.com/gptalk2014");
                } else if (websiteID == 2) {
                    launchSocialMediaIntent("twitter.com/GPTalk2014");
                } else if (websiteID == 3) {
                    launchSocialMediaIntent("linkedin.com/company/gp-talk");
                } else if (websiteID == 4) {
                    launchSocialMediaIntent("google.com/+GptalkIe2014");
                }
            }

        });
    }

    private void launchSocialMediaIntent(String url) {

        Uri uriUrl = Uri.parse("http://www." + url);
        Intent launchIntent = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchIntent);
    }

    private void initialiseVariables() {

        listView = (ListView) findViewById(R.id.lvSupport);

        tvSupportTitle = (TextView) findViewById(R.id.supportTitle);
        tvSupportMessage = (TextView) findViewById(R.id.supportMessage);
    }

    public void setFontStyles(Typeface fontStyle) {

        tvSupportTitle.setTypeface(fontStyle);
        tvSupportMessage.setTypeface(fontStyle);
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
}
