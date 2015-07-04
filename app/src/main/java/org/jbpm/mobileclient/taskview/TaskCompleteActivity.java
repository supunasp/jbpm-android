package org.jbpm.mobileclient.taskView;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.jbpm.mobileclient.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TaskCompleteActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_complete);




        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().setAcceptCookie(true);

        WebView webView = (WebView) findViewById(R.id.webview1);
        webView.getSettings().setJavaScriptEnabled(true);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(true);





     //   final HashMap<String, String> map = new HashMap<>();
        String authorization = null;
        try {
            authorization = "Basic " + Base64.encodeToString("krisv:krisv".getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        GetTaskList getTask = new GetTaskList("krisv", authorization);
        getTask.execute((Void) null);
/*
        String url ="http://10.0.2.2:8080/jbpm-console/kie-wb.html#Process%20Definition%20List";
        map.put("Authorization", authorization);
        webView.loadUrl(url, map);
        String rawCookieHeader = CookieManager.getInstance().getCookie(url);
        System.out.println(rawCookieHeader+" - Cookie");

        CustomWebViewClient webViewClient = new CustomWebViewClient();

        webView.setWebViewClient(webViewClient);
        webView.setHttpAuthUsernamePassword("http://10.0.2.2:8080/jbpm-console", "Authorization", "krisv", "krisv");
        String customHtml = "<html><body><iframe src='http://10.0.2.2:8080/jbpm-console/kie-wb.html?perspective=" +
                "FormDisplayPerspective&amp;standalone=true&amp;opener=localhost:8080&amp;taskId=169'" +
                " frameborder='0' style='width:100%; height:100%'>\n" +
                "  <p>Your browser does not support iframes.</p>\n" +
                "</iframe></body></html>";

       // webView.loadData(customHtml, "text/html", "utf-8");


*/
    }
     //   setContentView(webView);
    //public void onStart



/*
    public class CustomWebViewClient extends WebViewClient {

        private String currentUrl="http://10.0.2.2:8080/jbpm-console/kie-wb.html#org.kie.workbench.common.screens.home.client.HomePresenter";



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.equals(currentUrl)){
                String urlOver ="http://10.0.2.2:8080/jbpm-console/kie-wb.html#Process%20Definition%20List";
                view.loadUrl(urlOver);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Make a note about the failed load.
            System.out.println("Error - "+failingUrl);
        }
        @Override
        public void onPageFinished(WebView view, String url) {

            System.out.println("pageFinished - "+url);

            super.onPageFinished(view, url);
        }

    }

    public static String getCookieFromAppCookieManager(String url) throws MalformedURLException {
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager == null)
            return null;
        String rawCookieHeader = null;
        URL parsedURL = new URL(url);

        // Extract Set-Cookie header value from Android app CookieManager for this URL
        rawCookieHeader = cookieManager.getCookie(parsedURL.getHost());
        if (rawCookieHeader == null)
            return null;
        return rawCookieHeader;
    }
*/

    private class GetTaskList extends AsyncTask<Void, Void, Boolean> {
        private final String userName;
        private final String authHeader;

        HttpURLConnection conn;

        GetTaskList(String userNme, String authHeader) {
            userName = userNme;
            this.authHeader = authHeader;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (isNetworkAvailable()) {
                String response;
                URL url;
                try {
                    url = new URL("http://10.0.2.2:8080/jbpm-console/kie-wb.html?perspective=FormDisplayPerspective&amp;standalone=true&amp;opener=localhost:8080&amp;taskId=169");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", authHeader);
                    InputStreamReader inputStreamReader = new InputStreamReader((conn.getInputStream()));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    response = bufferedReader.readLine();
                    System.out.println(response);
                    conn.disconnect();
                    inputStreamReader.close();
                    bufferedReader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        public boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

    }

}
