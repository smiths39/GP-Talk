package com.app.gptalk.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.gptalk.R;

public class Splash extends Activity {

    private MediaPlayer introAudioTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        introAudioTheme = MediaPlayer.create(Splash.this, R.raw.gptalk_intro);

        // If theme has been enabled in audio settings within application
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean music = getPrefs.getBoolean("checkbox", true);

        if (music == true) {
            introAudioTheme.start();
        }

        // Wait 4 seconds until Login page is launched (i.e. length of audio theme)
        Thread timer = new Thread() {

            public void run() {

                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent openLoginPage = new Intent("com.app.gptalk.main.LOGIN");
                    startActivity(openLoginPage);
                }
            }
        };

        timer.start();
        startAnimation();
    }

    // Sets a gradient effect in center of background
    public void onAttachedToWindow() {

        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    // Launch introduction animation
    private void startAnimation() {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animation.reset();

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutSplash);
        layout.clearAnimation();
        layout.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        animation.reset();

        ImageView image = (ImageView) findViewById(R.id.ImageLogo);
        image.clearAnimation();
        image.startAnimation(animation);
    }

    @Override
    protected void onPause() {

        super.onPause();
        introAudioTheme.release();
        finish();
    }
}