/**
*   Copyright 2015 A.S.P. Athukorala
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*
*/
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
import android.widget.Toast;

import org.jbpm.mobileclient.MenuActivity;
import org.jbpm.mobileclient.R;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class TaskActivity extends ListActivity implements View.OnClickListener {

    TaskAdapter task_adapter;
    TextView t;
    String usrname = "";
    String serverAddress = "";
    String authHeader;
    Button coseButton;
    Intent mIntent;

    // storage where task list stored
    private ArrayList<TaskObject> tasks_list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        /**
         *   get user details, authentication header and server address from previous intent.
         * */
        Intent intent = getIntent();
        usrname = intent.getExtras().getString("username");
        authHeader = intent.getExtras().getString("AuthHeader");
        serverAddress += intent.getExtras().getString("ServerAddress");

        /**
         *   set user name.
         * */
        t = (TextView) findViewById(R.id.username);
        t.setText(usrname);

        /**
         *   get task list
         * */
        GetTaskList getTask = new GetTaskList(usrname, authHeader);
        getTask.execute((Void) null);

        /**
         *  set task list on custom array adaptor
         * */
        task_adapter = new TaskAdapter(this, R.layout.custom_list_view, tasks_list);
        setListAdapter(task_adapter);

        /**
         *      close button
         * */
        coseButton = (Button) findViewById(R.id.menubutton);
        coseButton.setOnClickListener(this);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {


        /**
         *   get the touched task
         * */
        TaskObject taskObject = tasks_list.get(position);

        /**
         *   send the touched task data via intent
         * */
        mIntent = new Intent(this, TaskViewActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("taskObject", taskObject);
        mIntent.putExtra("username", usrname);
        mIntent.putExtra("AuthHeader", authHeader);
        mIntent.putExtra("ServerAddress", serverAddress);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    @Override
    public void onClick(View v) {

        /**
         *   close button send user details,authentication header and server address to menu screen
         * */
        mIntent = new Intent(this, MenuActivity.class);
        mIntent.putExtra("username", usrname);
        mIntent.putExtra("AuthHeader", authHeader);
        mIntent.putExtra("ServerAddress", serverAddress);
        finish();
        startActivity(mIntent);
    }

    /**
     * get task list
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
                    /**
                     *   send a request and get data by url
                     * */
                    url = new URL(serverAddress + "/rest/task/query?potentialOwner=" + userName);
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
            } else runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**
                     *   If the network is not available
                     * */
                    Toast.makeText(getApplicationContext(), "Network Connection is not available ", Toast.LENGTH_LONG).show();
                }
            });

            if (tasks_list.isEmpty()) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get the tasks list! Check ur Connection ", Toast.LENGTH_LONG).show();
                    }
                });

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**
                     *   set task list in custom array adaptor
                     * */
                    task_adapter = new TaskAdapter(TaskActivity.this, R.layout.custom_list_view, tasks_list);
                    // display the list.
                    setListAdapter(task_adapter);
                }
            });
            return true;
        }

        /**
         * check whether network is available
         */
        public boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

        /**
         * create task list by response
         */
        private ArrayList<TaskObject> getTaskList(String response) {


            ArrayList<TaskObject> tasks_list = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            String taskId = "";
            String name = "";
            String description = "";
            String status = "";

            try {
                builder = factory.newDocumentBuilder();

                Document document = builder.parse(new InputSource(new StringReader(
                        response)));

                NodeList flowList = document.getElementsByTagName("task-summary");

                for (int i = 0; i < flowList.getLength(); i++) {

                    NodeList childList = flowList.item(i).getChildNodes();
                    for (int j = 0; j < childList.getLength(); j++) {
                        Node childNode = childList.item(j);
                        if ("id".equals(childNode.getNodeName())) {
                            taskId = childList.item(j).getTextContent()
                                    .trim();
                        }
                        if ("name".equals(childNode.getNodeName())) {
                            name = childList.item(j).getTextContent()
                                    .trim();
                        }
                        if ("description".equals(childNode.getNodeName())) {
                            description = childList.item(j).getTextContent()
                                    .trim();
                        }
                        if ("status".equals(childNode.getNodeName())) {
                            status = childList.item(j).getTextContent()
                                    .trim();
                        }
                    }
                    if (!status.equals("Completed") && !status.equals("Failed") && !status.equals("Exited")) {
                        TaskObject taskObj = new TaskObject(taskId, name, description, status);
                        taskObj.setTaskSummery(flowList.item(i).toString());
                        tasks_list.add(taskObj);
                    }
                }

            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
            return tasks_list;
        }
    }


}