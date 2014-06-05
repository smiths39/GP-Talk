package com.app.gptalk.listview_getter;

import android.graphics.Bitmap;

public class SupportItem {

    private String socialWebsite;
    private int socialLogo;

    public SupportItem(String socialWebsite, int socialLogo) {

        this.socialWebsite = socialWebsite;
        this.socialLogo = socialLogo;
    }

    // Getter methods
    public String getWebsite() { return socialWebsite; }
    public int getWebsiteLogo() { return socialLogo; }
}
