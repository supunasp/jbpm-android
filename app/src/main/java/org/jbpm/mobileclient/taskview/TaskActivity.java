package org.jbpm.mobileclient.taskView;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jbpm.mobileclient.MenuActivity;
import org.jbpm.mobileclient.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class TaskActivity extends ListActivity implements View.OnClickListener {

    TaskAdapter task_adapter;
    TextView t;
    String usrname;
    String authHeader;
    Button menuButton;
    Intent mIntent;
    // declare class variables
    private ArrayList<TaskObject> tasks_list = new ArrayList<>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        Intent intent = getIntent();
        usrname = intent.getExtras().getString("username");
        authHeader = intent.getExtras().getString("AuthHeader");

        t = (TextView) findViewById(R.id.username);
        t.setText(usrname);

        GetTaskList getTask = new GetTaskList(usrname, authHeader);
        getTask.execute((Void) null);

        task_adapter = new TaskAdapter(this, R.layout.list_task, tasks_list);
        setListAdapter(task_adapter);

        menuButton = (Button) findViewById(R.id.menubutton);
        menuButton.setOnClickListener(this);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        TaskObject taskObject = tasks_list.get(position);

        mIntent = new Intent(this, TaskViewActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("taskObject", taskObject);
        mIntent.putExtra("username", usrname);
        mIntent.putExtra("AuthHeader", authHeader);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    @Override
    public void onClick(View v) {
        mIntent = new Intent(this, MenuActivity.class);
        mIntent.putExtra("username", usrname);
        mIntent.putExtra("AuthHeader", authHeader);
        finish();
        startActivity(mIntent);
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
                    InputStreamReader inputStreamReader = new InputStreamReader((conn.getInputStream()));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    response = bufferedReader.readLine();
                    tasks_list = getTaskList(response);
                    conn.disconnect();
                    inputStreamReader.close();
                    bufferedReader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else t.setText("Network Connection is not available");

            if (tasks_list.isEmpty()) {

                t.setText("Couldn't get the tasks list! Check ur Connection");
            }
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
                    if (!status.equals("Completed") && !status.equals("Failed")) {
                        TaskObject taskObj = new TaskObject(taskId, name, description, status);
                        taskObj.setTaskSummery((part + "</task-summary>"));
                        tasks_list.add(taskObj);

                        System.out.println(taskId + " task " + name + ": " + description + " : " + status);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return tasks_list;
        }
    }


}




