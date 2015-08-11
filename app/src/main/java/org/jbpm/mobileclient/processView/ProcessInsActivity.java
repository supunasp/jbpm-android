package org.jbpm.mobileclient.processView;

import android.app.ListActivity;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class ProcessInsActivity extends ListActivity implements View.OnClickListener {

    String usrname,authHeader,serverAddress="";
    Intent intent;
    ArrayList<ProcessObject> process_list=new ArrayList<>();
    ProcessInsAdaptor process_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_instances);

         intent = getIntent();
        usrname = intent.getExtras().getString("username");
        authHeader = intent.getExtras().getString("AuthHeader");
        serverAddress+=intent.getExtras().getString("ServerAddress");

        TextView username = (TextView) findViewById(R.id.usernameIns);
        username.setText(usrname);
        GetTaskList getTask = new GetTaskList(usrname, authHeader);
        getTask.execute((Void) null);


       process_adapter = new ProcessInsAdaptor(this, R.layout.list_task, process_list);
        setListAdapter(process_adapter);

        Button close = (Button) findViewById(R.id.closeButton);
        close.setOnClickListener(this);


        intent = new Intent(this, MenuActivity.class);
        intent.putExtra("username", usrname);
        intent.putExtra("AuthHeader", authHeader);
        intent.putExtra("ServerAddress",serverAddress);
    }

    @Override
    public void onClick(View v) {


        startActivity(intent);
        finish();
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
                    url = new URL(serverAddress+"/rest/history/instances");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", authHeader);
                    InputStreamReader inputStreamReader = new InputStreamReader((conn.getInputStream()));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    response = bufferedReader.readLine();
                    process_list = getProcessList(response);
                    conn.disconnect();
                    inputStreamReader.close();
                    bufferedReader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Network Connection is not available ", Toast.LENGTH_LONG)
                            .show();
                }});

            if (process_list.isEmpty()) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get the process Instances list! Check ur Connection ", Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    process_adapter = new ProcessInsAdaptor(ProcessInsActivity.this, R.layout.list_process, process_list);
                    // display the list.
                    setListAdapter(process_adapter);
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

        private ArrayList<ProcessObject> getProcessList(String response) {


            ArrayList<ProcessObject> process_list = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            String processInsId="";
            String processName="";
            String processExId="";
            String processVersion="";
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(
                        response)));
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();
                XPathExpression expr = xpath.compile("/log-instance-list/process-instance-log[status=1]");
                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);



                for (int i = 0; i < nl.getLength(); i++) {
                    NodeList processInsNode= nl.item(i).getChildNodes();

                    for(int j=0;j<processInsNode.getLength();j++){

                        Node childNode = processInsNode.item(j);
                        if ("process-instance-id".equals(childNode.getNodeName())) {
                            processInsId=processInsNode.item(j).getTextContent()
                                    .trim();
                        }
                        if ("process-name".equals(childNode.getNodeName())) {
                            processName=processInsNode.item(j).getTextContent()
                                    .trim();
                        }
                        if ("external-id".equals(childNode.getNodeName())) {
                            processExId=processInsNode.item(j).getTextContent()
                                    .trim();
                        }
                        if ("process-version".equals(childNode.getNodeName())) {
                            processVersion=processInsNode.item(j).getTextContent()
                                    .trim();
                        }

                    }
                    ProcessObject processObject = new ProcessObject(processInsId, processName, processExId ,processVersion);
                    processObject.setProcessSummery(processInsNode.toString());
                    process_list.add(processObject);
                }
            } catch (ParserConfigurationException | XPathExpressionException | IOException | SAXException e) {
                e.printStackTrace();
            }
            return process_list;
        }
    }
}
