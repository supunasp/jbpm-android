package org.jbpm.mobileclient;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.jbpm.mobileclient.taskview.TaskAdapter;
import org.jbpm.mobileclient.taskview.TaskObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class TaskActivity extends ListActivity {

    TaskAdapter task_adapter;
    TextView t;
    // declare class variables
    private ArrayList<TaskObject> tasks_list = new ArrayList<>();


    /**
     * Called when the activity is first created.
     */


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        String usrname = intent.getExtras().getString("username");
        String authHeader = intent.getExtras().getString("AuthHeader");

        t = (TextView) findViewById(R.id.userName);
        t.setText(usrname);

        GetTaskList getTask = new GetTaskList(usrname, authHeader);
        getTask.execute((Void) null);

        task_adapter = new TaskAdapter(this, R.layout.list_task, tasks_list);
        setListAdapter(task_adapter);


    }

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
                    url = new URL("http://10.0.2.2:8080/jbpm-console/rest/task/query?potentialOwner=" + userName);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", authHeader);
                    response = new BufferedReader(new InputStreamReader((conn.getInputStream()))).readLine();
                    tasks_list = getTaskList(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else t.setText("Network Connection is not available");

            tasks_list.add(new TaskObject("34", "MyTaskName", "This is task #1", "Reserved"));
            tasks_list.add(new TaskObject("35", "MyTaskName #2", "This is task #2", "Ready"));
            tasks_list.add(new TaskObject("36", "MyTaskName #3", "This is task #3", "In progress"));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    task_adapter = new TaskAdapter(TaskActivity.this, R.layout.list_task, tasks_list);
                    // display the list.
                    setListAdapter(task_adapter);
                }
            });
            return true;
        }


        public boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            // if no network is available networkInfo will be null
            // otherwise check if we are connected
            return networkInfo != null && networkInfo.isConnected();
        }

        private ArrayList<TaskObject> getTaskList(String response) {

            String[] parts = response.split("</task-summary>");
            ArrayList<TaskObject> tasks_list = new ArrayList<>();
            String taskId;
            String name;
            String description;
            String status;
            String part;
            for (int i = 0; i < parts.length - 1; i++) {
                part = parts[i];
                try {
                    taskId = part.substring(part.indexOf("<id>") + 4, part.indexOf("</id>"));
                    name = part.substring(part.indexOf("<name>") + 6, part.indexOf("</name>"));
                    description = part.substring(part.indexOf("<description>") + 13, part.indexOf("</description>"));
                    status = part.substring(part.indexOf("<status>") + 8, part.indexOf("</status>"));
                    if (!status.equals("Completed")) {
                        tasks_list.add(new TaskObject(taskId, name, description, status));
                        System.out.println(taskId + " task " + name + ": " + description + " : " + status);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("list size :" + tasks_list.size());
            return tasks_list;
        }
    }
}

