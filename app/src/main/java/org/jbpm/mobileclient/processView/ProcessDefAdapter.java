package org.jbpm.mobileclient.processView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jbpm.mobileclient.R;

import java.util.ArrayList;

/**
 * Created by Supun Athukorala on 6/10/2015.
 */
public class ProcessDefAdapter extends ArrayAdapter<ProcessObject> {


    private ArrayList<ProcessObject> processList;

    public ProcessDefAdapter(Context context, int textViewResourceId, ArrayList<ProcessObject> processList) {
        super(context, textViewResourceId, processList);
        this.processList = processList;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_process, null);
        }

        ProcessObject i = processList.get(position);

        if (i != null) {

            TextView nameId = (TextView) convertView.findViewById(R.id.nametext);
            TextView nameData = (TextView) convertView.findViewById(R.id.namedata);

            TextView detailsId = (TextView) convertView.findViewById(R.id.detailstext);
            TextView detailsData = (TextView) convertView.findViewById(R.id.detailsdata);

            if (nameId != null) {
                nameId.setText("Definition : ");
            }
            if (nameData != null) {
                nameData.setText(i.getProcessId() + " - " + i.getName());
            }
            if (detailsId != null) {
                detailsId.setText("Project: ");
            }
            if (detailsData != null) {
                detailsData.setText(i.getDeploymentId());
            }
        }


        return convertView;
    }
}
