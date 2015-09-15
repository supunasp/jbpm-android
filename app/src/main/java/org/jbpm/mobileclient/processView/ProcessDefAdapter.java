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
 * Created by Supun Prabhath on 6/10/2015.
 * Project MobileClient
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

            /**
             * show prcess details one by one in list View
             **/
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
