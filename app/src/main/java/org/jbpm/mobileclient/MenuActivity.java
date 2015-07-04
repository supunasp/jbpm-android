package org.jbpm.mobileclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jbpm.mobileclient.processView.ProcessDefActivity;
import org.jbpm.mobileclient.processView.ProcessInsActivity;
import org.jbpm.mobileclient.taskView.TaskActivity;

import static org.jbpm.mobileclient.R.layout.activity_menu;


public class MenuActivity extends Activity implements View.OnClickListener {

    String usrname = "";
    String authHeader = "";
    Button btn_Tasks;
    Button btn_process;
    Button btn_process_Instances;
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
        btn_Tasks.setShadowLayer(1, 0, 0, Color.BLACK);
        btn_Tasks.setOnClickListener(this);

        // Dashboard processDefinitions button
        btn_process = (Button) findViewById(R.id.processbutton);
        btn_process.setShadowLayer(1, 0, 0, Color.BLACK);
        btn_process.setOnClickListener(this);

        // Dashboard process Instances button
        btn_process_Instances = (Button) findViewById(R.id.processInstances);
        btn_process_Instances.setShadowLayer(1, 0, 0, Color.BLACK);
        btn_process_Instances.setOnClickListener(this);



        // Dashboard process Dashboard button
        btn_dashboard = (Button) findViewById(R.id.dashboardbutton);
        btn_dashboard.setShadowLayer(1, 0, 0, Color.BLACK);
        btn_dashboard.setOnClickListener(this);


        // Dashboard logout button
        btn_logout = (Button) findViewById(R.id.logoutbutton);
        btn_logout.setShadowLayer(1, 0, 0, Color.BLACK);
        btn_logout.setOnClickListener(this);

        t = (TextView) findViewById(R.id.username);
        t.setShadowLayer(1,0,0, Color.BLACK);

        t.setText(usrname);

    }

    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {
            case R.id.tasksbutton:
                // Tasks button

                intent = new Intent(MenuActivity.this, TaskActivity.class);
                intent.putExtra("username", usrname);
                intent.putExtra("AuthHeader", authHeader);
                startActivity(intent);

                break;
            case R.id.processbutton:
                // Process Management button

                intent = new Intent(MenuActivity.this, ProcessDefActivity.class);
                intent.putExtra("username", usrname);
                intent.putExtra("AuthHeader", authHeader);
                startActivity(intent);

                break;

            case R.id.processInstances:
                // Process process Instances button

                intent = new Intent(MenuActivity.this, ProcessInsActivity.class);
                intent.putExtra("username", usrname);
                intent.putExtra("AuthHeader", authHeader);
                startActivity(intent);

                break;
            case R.id.dashboardbutton:
                // Dashboard button

                intent = new Intent(MenuActivity.this, DashboardActivity.class);
                intent.putExtra("username", usrname);
                intent.putExtra("AuthHeader", authHeader);
                startActivity(intent);


                break;
            case R.id.logoutbutton:
                // Logout button

                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);

                break;
        }

    }
}


