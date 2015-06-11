package org.jbpm.mobileclient.taskview;

/**
 * Created by Supun Athukorala on 6/10/2015.
 */
public class TaskObject {

    private String taskId;
    private String name;
    private String details;
    private String status;


    public TaskObject() {

    }

    public TaskObject(String taskId, String name, String details, String status) {

        this.taskId = taskId;
        this.name = name;
        this.details = details;
        this.status = status;

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}