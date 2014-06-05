package com.app.gptalk.homepage.patient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.app.gptalk.R;

public class PatientFragmentGPWebsitesBrowser extends Fragment implements View.OnClickListener {

    private View view;
    private WebView webView;
    private String launchURL;
    private Button bGoBack, bGoForward, bRefresh, bClearHistory, bDisplayBookmarks;

    public PatientFragmentGPWebsitesBrowser(String website) {
        this.launchURL = website;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view != null) {

            ViewGroup parent = (ViewGroup) view.getParent();

            if (parent != null) {
                parent.removeView(view);
            }
        }

        try {
            view = inflater.inflate(R.layout.website_browser, container, false);
        } catch (InflateException e) {}

        initialiseWidgets(view);
        setHasOptionsMenu(true);

        // Initialise a browser within the fragment
        enableWebViewSettings(webView);
        webView.setWebViewClient(new WebClient());
        webView.loadUrl(launchURL);
        return view;
    }

    private void initialiseWidgets(View view) {

        webView = (WebView) view.findViewById(R.id.gpWebView);

        bGoBack = (Button) view.findViewById(R.id.bBack);
        bGoForward = (Button) view.findViewById(R.id.bForward);
        bRefresh = (Button) view.findViewById(R.id.bRefresh);
        bClearHistory = (Button) view.findViewById(R.id.bHistory);
        bDisplayBookmarks = (Button) view.findViewById(R.id.bBookmarkList);

        setClickListeners();
    }

    private void setClickListeners() {

        bGoBack.setOnClickListener(PatientFragmentGPWebsitesBrowser.this);
        bGoForward.setOnClickListener(PatientFragmentGPWebsitesBrowser.this);
        bRefresh.setOnClickListener(PatientFragmentGPWebsitesBrowser.this);
        bClearHistory.setOnClickListener(PatientFragmentGPWebsitesBrowser.this);
        bDisplayBookmarks.setOnClickListener(PatientFragmentGPWebsitesBrowser.this);
    }

    // Enable specified settings to be provided to user whilst interacting with the browser
    private void enableWebViewSettings(WebView newWebView) {

        newWebView.getSettings().setJavaScriptEnabled(true);
        newWebView.getSettings().setLoadWithOverviewMode(true);
        newWebView.getSettings().setUseWideViewPort(true);
        newWebView.getSettings().setBuiltInZoomControls(true);
        newWebView.getSettings().setDisplayZoomControls(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    this.getActivity().finish();
                } else {
                    getFragmentManager().popBackStack();
                }
                break;
        }

        return false;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bBack:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    Toast.makeText(PatientFragmentGPWebsitesBrowser.this.getActivity(), "Cannot go back", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bForward:
                if (webView.canGoForward()) {
                    webView.goForward();
                } else {
                    Toast.makeText(PatientFragmentGPWebsitesBrowser.this.getActivity(), "Cannot go forward", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bRefresh:
                // Refresh page if poor wifi connection affects displayed website
                webView.reload();
                Toast.makeText(PatientFragmentGPWebsitesBrowser.this.getActivity(), "Page Refreshed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bHistory:
                // Clear cache history to increase speed performance
                webView.clearHistory();
                Toast.makeText(PatientFragmentGPWebsitesBrowser.this.getActivity(), "History Cleared", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bBookmarkList:
                activateBookmarkFragment();
                break;
        }
    }

    private void activateBookmarkFragment() {

        PatientFragmentGPWebsites gpWebsites = new PatientFragmentGPWebsites();
        FragmentManager fragmentManager = getFragmentManager();

        // Revert back to available GP website list
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .addToBackStack(null)
                .replace(((ViewGroup)getView().getParent()).getId(), gpWebsites);

        fragmentTransaction.commit();
    }

    public class WebClient extends WebViewClient {

        // Disable application taking control when URL is successfully loaded
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }
    }
}
