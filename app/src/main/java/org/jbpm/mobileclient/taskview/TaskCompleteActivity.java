package org.jbpm.mobileclient.taskView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.WebView;

import org.jbpm.mobileclient.R;

import java.io.UnsupportedEncodingException;

public class TaskCompleteActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_complete);

        //   final HashMap<String, String> map = new HashMap<>();
        String authorization = null;
        try {
            authorization = "Basic " + Base64.encodeToString("krisv:krisv".getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //  i.setData("Authorization", authorization);


        //    GetTaskList getTask = new GetTaskList("krisv", authorization);
        //   getTask.execute((Void) null);



        CookieManager.getInstance().setAcceptCookie(true);

        WebView webView;
        webView = (WebView) findViewById(R.id.webview1);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setSaveFormData(true);
        webView.loadUrl("file:///android_asset/showTaskForm.html");
    }
}
