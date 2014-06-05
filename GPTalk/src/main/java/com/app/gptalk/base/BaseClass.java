package com.app.gptalk.base;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

public class BaseClass extends Activity {

    public String unconfirmedStatus = "unconfirmed";
    public String confirmedStatus = "confirmed";
    public String rejectedStatus = "rejected";

    public void errorHandler(Exception exception) {

        String errorMessage = exception.toString();

        Dialog dialog = new Dialog(this);
        dialog.setTitle("Error Occurred");

        TextView text = new TextView(this);
        text.setText(errorMessage);

        dialog.setContentView(text);
        dialog.show();
    }

    public void initialiseProgressDialog(ProgressDialog newProgressDialog, String message) {

        newProgressDialog.setMessage(message);
        newProgressDialog.setIndeterminate(false);
        newProgressDialog.setCancelable(true);
        newProgressDialog.show();
    }

    public String retrieveUsername(Activity activity, FragmentActivity fragmentActivity) {

        Intent intent = null;

        if (activity == null) {
            intent = fragmentActivity.getIntent();

        } else if (fragmentActivity == null) {
            intent = activity.getIntent();
        }

        return intent.getExtras().getString("username");
    }
}
