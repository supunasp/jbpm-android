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
 * Project MobileClient
 */
public class TaskAdapter extends ArrayAdapter<TaskObject> {


    private ArrayList<TaskObject> tasksList;
    /**
     *   set task Adaptor
     * */
    public TaskAdapter(Context context, int textViewResourceId, ArrayList<TaskObject> tasksList) {
        super(context, textViewResourceId, tasksList);
        this.tasksList = tasksList;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_list_view, null);
        }
        if(!tasksList.isEmpty()) {
            TaskObject i = tasksList.get(position);

            if (i != null) {

                /**
                 *   set task data
                 * */

                TextView nameId = (TextView) convertView.findViewById(R.id.nametext);
                TextView nameData = (TextView) convertView.findViewById(R.id.namedata);

                TextView detailsId = (TextView) convertView.findViewById(R.id.detailstext);
                TextView detailsData = (TextView) convertView.findViewById(R.id.detailsdata);

                if (nameId != null) {
                    nameId.setText("Name: ");
                }
                if (nameData != null) {
                    nameData.setText(i.getTaskId() + " - " + i.getName());
                }
                if (detailsId != null) {
                    detailsId.setText("Details: ");
                }
                if (detailsData != null) {
                    detailsData.setText(i.getDetails());
                }
            }
        }

        return convertView;
    }
}
