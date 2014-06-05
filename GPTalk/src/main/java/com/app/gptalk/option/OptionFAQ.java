package com.app.gptalk.option;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

public class OptionFAQ extends Activity {

    private String username, userType;
    private Typeface fontTitle;
    private WebView webViewBookingQuestion1, webViewBookingQuestion2, webViewCancelQuestion, webViewModifyQuestion;
    private TextView tvFAQTitle, tvFAQBookingTitle, tvFAQBookingQuestion1, tvFAQBookingQuestion2, tvFAQCancelTitle,
                     tvFAQCancelQuestion, tvFAQModifyTitle, tvFAQModifyQuestion;
    private BaseClass baseClass = new BaseClass();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_faq);

        getIntentDetails();
        initialiseWidgets();

        fontTitle = Typeface.createFromAsset(getAssets(), "Sanseriffic.otf");
        setFontStyles(fontTitle);

        String question1Answer = "To make a booking with a selected GP, ensure that the GP is available at the desired time by " +
                                 "viewing their schedule located on the 'New Booking' page. If a time has been selected, enter the " +
                                 "prompted details and press the 'Make Booking' button. A request will have been sent to the GP " +
                                 "and your booking request will either be confirmed or rejected by the GP. You can view the progress " +
                                 "of your booking on the 'Consultations' page.";

        String question2Answer = "When you have successfully sent a consultation request to a GP, the consultation in which you have sent " +
                                 "is automatically inserted into your schedule listings under the title of 'unconfirmed'. When a GP has " +
                                 "made a decision regarding your booking question, their answer will be sent back to your via a notification. Please " +
                                 "view the contents of the notification and the reply will automatically update your schedule planner. ";

        String question3Answer = "To cancel a confirmed booking with a GP or a patient, you must go to your schedule planner and select the consultation " +
                                 "that you wish to cancel. Upon pressing the cancellation button, you are prompted for an explanation for the reason of the " +
                                 "cancellation. When your have sent the cancellation notice, the notification is sent to the corresponding client and they are " +
                                 "informed of the cancellation. Your schedule planner will be updated to accommodate these changes.";

        String question4Answer = "If you wish to change your account details via the app, you may do so by clicking the profile icon located in the top right bar " +
                                 "of the application. Select 'Account' and you are displayed with your current account details. Click the 'Settings' button below to be " +
                                 "displayed prompted input boxes that when entered, the submitted details will override your profile details successfully.";

        createAnswer(question1Answer, webViewBookingQuestion1);
        createAnswer(question2Answer, webViewBookingQuestion2);
        createAnswer(question3Answer, webViewCancelQuestion);
        createAnswer(question4Answer, webViewModifyQuestion);
    }

    private void getIntentDetails() {

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userType = intent.getStringExtra("user_type");
    }

    // Set html style text for defined answer
    private void createAnswer(String questionAnswer, WebView webViewQuestion) {

        String textFormat = "<html><body><p align=\"justify\"><font color=\"#ffffff\">" + questionAnswer +"</font></p></body></html>";
        webViewQuestion.setBackgroundColor(Color.parseColor("#ff191919"));
        webViewQuestion.loadData(textFormat, "text/html", "utf-8");
    }

    private void setFontStyles(Typeface font) {

        tvFAQTitle.setTypeface(font);
        tvFAQBookingTitle.setTypeface(font);
        tvFAQCancelTitle.setTypeface(font);
        tvFAQModifyTitle.setTypeface(font);

        tvFAQBookingQuestion1.setTypeface(font);
        tvFAQBookingQuestion2.setTypeface(font);
        tvFAQCancelQuestion.setTypeface(font);
        tvFAQModifyQuestion.setTypeface(font);
    }

    private void initialiseWidgets() {

        tvFAQTitle = (TextView) findViewById(R.id.tvFAQTitle);
        tvFAQBookingTitle = (TextView) findViewById(R.id.tvFAQBookingTitle);
        tvFAQCancelTitle = (TextView) findViewById(R.id.tvFAQCancelTitle);
        tvFAQModifyTitle = (TextView) findViewById(R.id.tvFAQAccountDetailsTitle);

        tvFAQBookingQuestion1 = (TextView) findViewById(R.id.tvFAQBookingQuestion1);
        tvFAQBookingQuestion2 = (TextView) findViewById(R.id.tvFAQBookingQuestion2);
        tvFAQCancelQuestion = (TextView) findViewById(R.id.tvFAQCancelQuestion);
        tvFAQModifyQuestion = (TextView) findViewById(R.id.tvFAQAccountDetailsQuestion);

        webViewBookingQuestion1 = (WebView) findViewById(R.id.tvFAQBookingAnswer1);
        webViewBookingQuestion2 = (WebView) findViewById(R.id.tvFAQBookingAnswer2);
        webViewCancelQuestion = (WebView) findViewById(R.id.tvFAQCancelBookingAnswer1);
        webViewModifyQuestion = (WebView) findViewById(R.id.tvFAQAccountDetailsAnswer1);

        disableScrolling(webViewBookingQuestion1);
        disableScrolling(webViewBookingQuestion2);
        disableScrolling(webViewCancelQuestion);
        disableScrolling(webViewModifyQuestion);
    }

    private void disableScrolling(WebView appView) {

        appView.setVerticalScrollBarEnabled(false);
        appView.setHorizontalScrollBarEnabled(false);
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