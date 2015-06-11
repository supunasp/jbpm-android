package org.jbpm.mobileclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static org.jbpm.mobileclient.R.layout.activity_menu;


public class MenuActivity extends Activity {

    String usrname = "";
    String authHeader = "";
    Button btn_Tasks;
    Button btn_process;
    Button btn_dashboard;
    Button btn_logout;
    TextView t;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_menu);

        /**
         * Creating all buttons instances
         * */
        Intent intent = getIntent();
        usrname += intent.getExtras().getString("username");
        authHeader += intent.getExtras().getString("AuthHeader");
        // Dashboard Tasks button
        btn_Tasks = (Button) findViewById(R.id.tasksbutton);

        // Dashboard Friends button
        btn_process = (Button) findViewById(R.id.processbutton);

        // Dashboard process Dashboard button
        btn_dashboard = (Button) findViewById(R.id.dashboardbutton);

        // Dashboard logout button
        btn_logout = (Button) findViewById(R.id.logoutbutton);


        t = (TextView) findViewById(R.id.Username);

        t.setText(usrname);


        /**
         * Handling all button click events
         * */

        // Listening to Tasks button click
        btn_Tasks.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                // Launching Tasks Screen
                Intent i = new Intent(MenuActivity.this, TaskActivity.class);
                i.putExtra("username", usrname);
                i.putExtra("AuthHeader", authHeader);
                startActivity(i);

            }
        });

        // Listening Process button click
        btn_process.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(MenuActivity.this, ProcessActivity.class);
                startActivity(i);
            }
        });

        // Listening Dashboard button click
        btn_dashboard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(MenuActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        // Listening Dashboard button click
        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(MenuActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        });


    }

}


