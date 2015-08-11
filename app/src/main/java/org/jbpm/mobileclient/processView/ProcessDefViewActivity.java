package org.jbpm.mobileclient.processView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jbpm.mobileclient.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class ProcessDefViewActivity extends Activity implements View.OnClickListener {

    TextView ids;
    private Intent intent;
    private String authHeader,serverAddress="";
    private ProcessObject processObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_view);

        intent = getIntent();
        String usrname = intent.getExtras().getString("username");
        authHeader = intent.getExtras().getString("AuthHeader");
        processObject = (ProcessObject) intent.getSerializableExtra("processObject");
        serverAddress+=intent.getExtras().getString("ServerAddress");

        TextView userName = (TextView) findViewById(R.id.username);
        userName.setText(usrname);

        ids = (TextView) findViewById(R.id.getId);
        ids.setText(processObject.getProcessId());

        TextView processDefName = (TextView) findViewById(R.id.definitionName);
        processDefName.setText(processObject.getName());

        TextView deploymentOd = (TextView) findViewById(R.id.getDepolyment);
        deploymentOd.setText("" + processObject.getDeploymentId());

        ListView listView = (ListView) findViewById(R.id.processVariables);

        String[] processVariables = processObject.getProcessVariables();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, processVariables);
        listView.setAdapter(adapter);

        Button newIns = (Button) findViewById(R.id.ButtonInsStart);
        newIns.setOnClickListener(this);

        Button closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);


        intent = new Intent(this, ProcessDefActivity.class);
        intent.putExtra("username", usrname);
        intent.putExtra("AuthHeader", authHeader);
        intent.putExtra("ServerAddress",serverAddress);

    }
    @Override
    public void onClick(View v) {

        String output = " Not Done";
        switch (v.getId()) {
            case R.id.ButtonInsStart:
                //New Instance button
                try {
                    switch (output =
                            new getProcessDone()
                                    .execute(serverAddress+"/rest/runtime/" + processObject.getDeploymentId() + "/process/" + processObject.getProcessId() + "/start")
                                    .get()) {
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                String responseMsg;
                if (output.length() > 10) {
                    responseMsg = output.substring(output.indexOf("<status>") + 8, output.indexOf("</status>"));
                    Toast.makeText(getApplicationContext(),
                            "response is " + responseMsg, Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case R.id.closeButton:
                //Close button
                finish();
                startActivity(intent);
                break;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public class getProcessDone extends AsyncTask<String, Integer, String> {
        HttpURLConnection conn;
        URL url;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;

        getProcessDone() {

        }

        @Override
        protected String doInBackground(String... params) {
            String line = "";
            if (isNetworkAvailable()) {

                try {

                    url = new URL(params[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", authHeader);

                    inputStreamReader = new InputStreamReader((conn.getInputStream()));
                    bufferedReader = new BufferedReader(inputStreamReader);
                    line = bufferedReader.readLine();

                    conn.disconnect();
                    inputStreamReader.close();
                    bufferedReader.close();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            } else

                Toast.makeText(getApplicationContext(),"Network Connection is not Available",Toast.LENGTH_SHORT).show();
            return line;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
