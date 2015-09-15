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

import java.io.Serializable;

/**
 * Created by Supun Athukorala on 6/10/2015.
 * MobileClient
 *
 */
public class TaskObject implements Serializable {

    private String taskId;
    private String name;
    private String details;
    private String status;
    private String taskSummery;

    /**
     * Task object
     **/
    public TaskObject() {

    }


    public TaskObject(String taskId, String name, String details, String status) {

        this.taskId = taskId;
        this.name = name;
        this.details = details;
        this.status = status;

    }
    public String toString(){
        return taskId+" "+name+" "+details+" "+status;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTaskSummery(String taskSummery) {

        this.taskSummery = taskSummery;
    }


}