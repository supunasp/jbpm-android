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
package org.jbpm.mobileclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.jbpm.mobileclient.R.layout.activity_dashboard;


public class DashboardActivity extends Activity implements View.OnClickListener {


    TextView t;
    String usrName;
    String authHeader;
    String serverAddress="";
    Intent mIntent;

    TextView totalInstances;
    TextView activeInstances;
    TextView completedInstances;
    TextView abortedInstances;
    TextView pendingInstances;
    TextView suspendedInstances;

    TextView totalTasks;
    TextView completedTasks;

    String totalInstancesNo = "";
    String activeInstancesNo = "";
    String completedInstancesNo = "";
    String abortedInstancesNo = "";
    String pendingInstancesNo = "";
    String suspendedInstanceNo = "";
    String totalTasksNo = "";
    String completedTasksNo = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(activity_dashboard);

        /**
         *   get data from previous activity via intent.
         * */
        Intent intent = getIntent();
        usrName = intent.getExtras().getString("username");
        authHeader = intent.getExtras().getString("AuthHeader");
        serverAddress+=intent.getExtras().getString("ServerAddress");

        /**
         *   set UI elements
         * */
        t = (TextView) findViewById(R.id.dashUsername);
        t.setText(usrName);

        totalInstances = (TextView) findViewById(R.id.totalInstances);
        activeInstances = (TextView) findViewById(R.id.instancesActive);
        completedInstances = (TextView) findViewById(R.id.instancesCompleted);
        abortedInstances = (TextView) findViewById(R.id.instancesAborted);
        pendingInstances = (TextView) findViewById(R.id.instancesPending);
        suspendedInstances = (TextView) findViewById(R.id.instancesSuspended);
        totalTasks = (TextView) findViewById(R.id.toatlTasks);
        completedTasks = (TextView) findViewById(R.id.tasksCompleted);

        totalInstances.setText(totalInstancesNo);
        activeInstances.setText(activeInstancesNo);
        completedInstances.setText(completedInstancesNo);
        abortedInstances.setText(abortedInstancesNo);
        pendingInstances.setText(pendingInstancesNo);
        suspendedInstances.setText(suspendedInstanceNo);

        totalTasks.setText(totalTasksNo);
        completedTasks.setText(completedTasksNo);

        GetTaskList getTask = new GetTaskList(usrName, authHeader);
        getTask.execute((Void) null);

        Button close = (Button) findViewById(R.id.closeButton);
        close.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        /**
         *   send data to Menu activity via intent.
         * */
        mIntent = new Intent(this, MenuActivity.class);
        mIntent.putExtra("username", usrName);
        mIntent.putExtra("AuthHeader", authHeader);
        mIntent.putExtra("ServerAddress",serverAddress);
        finish();
        startActivity(mIntent);
    }

    private class GetTaskList extends AsyncTask<Void, Void, Boolean> {
        private final String authHeader;

        HttpURLConnection conn;

        GetTaskList(String userNme, String authHeader) {
            this.authHeader = authHeader;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (isNetworkAvailable()) {
                String response;
                URL url;
                try {
                    /**
                     *   get instances data from server.
                     * */
                    url = new URL(serverAddress+"/rest/history/instances");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", authHeader);
                    InputStreamReader inputStreamReader = new InputStreamReader((conn.getInputStream()));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    response = bufferedReader.readLine();


                    calculateSummeryInstances(response);
                    conn.disconnect();
                    inputStreamReader.close();
                    bufferedReader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    /**
                     *   get task history data from server.
                     * */
                    url = new URL(serverAddress+"/rest/task/query");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", authHeader);
                    InputStreamReader inputStreamReader = new InputStreamReader((conn.getInputStream()));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    response = bufferedReader.readLine();

                    calculateSummeryTasks(response);
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
                     *   If connection is not available
                     * */
                    Toast.makeText(getApplicationContext(),"Network Connection is not available ", Toast.LENGTH_LONG).show();

                    totalInstances.setText(totalInstancesNo);
                    activeInstances.setText(activeInstancesNo);
                    completedInstances.setText(completedInstancesNo);
                    abortedInstances.setText(abortedInstancesNo);
                    pendingInstances.setText(pendingInstancesNo);
                    suspendedInstances.setText(suspendedInstanceNo);

                    totalTasks.setText(totalTasksNo);
                    completedTasks.setText(completedTasksNo);
                }
            });
            return true;
        }

        /**
         *   calculate Task summery.
         * */
        private void calculateSummeryTasks(String response) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(
                        response)));
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();

                XPathExpression expr = xpath.compile("/task-summary-list-response/task-summary");
                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                totalTasksNo = "" + nl.getLength();

                expr = xpath.compile("/task-summary-list-response/task-summary[status='Completed']");
                nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                completedTasksNo = "" + nl.getLength();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        totalTasks.setText(totalTasksNo);
                        completedTasks.setText(completedTasksNo);
                    }
                });
            } catch (ParserConfigurationException | XPathExpressionException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }

        /**
         *   calculate Instances summery.
         * */
        private void calculateSummeryInstances(String response) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;


            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(
                        response)));
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();

                XPathExpression expr = xpath.compile("/log-instance-list/process-instance-log");
                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                totalInstancesNo = "" + nl.getLength();

                expr = xpath.compile("/log-instance-list/process-instance-log[status=1]");
                nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                activeInstancesNo = "" + nl.getLength();

                expr = xpath.compile("/log-instance-list/process-instance-log[status=2]");
                nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                completedInstancesNo = "" + nl.getLength();

                expr = xpath.compile("/log-instance-list/process-instance-log[status=3]");
                nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                abortedInstancesNo = "" + nl.getLength();

                expr = xpath.compile("/log-instance-list/process-instance-log[status=4]");
                nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                pendingInstancesNo = "" + nl.getLength();

                expr = xpath.compile("/log-instance-list/process-instance-log[status=5]");
                nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                suspendedInstanceNo = "" + nl.getLength();

            } catch (ParserConfigurationException | XPathExpressionException | IOException | SAXException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    totalInstances.setText(totalInstancesNo);
                    activeInstances.setText(activeInstancesNo);
                    completedInstances.setText(completedInstancesNo);
                    abortedInstances.setText(abortedInstancesNo);
                    pendingInstances.setText(pendingInstancesNo);
                    suspendedInstances.setText(suspendedInstanceNo);

                    totalTasks.setText(totalTasksNo);
                    completedTasks.setText(completedTasksNo);
                }
            });

        }

        /**
         *   Check whether network is available .
         * */
        public boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}

