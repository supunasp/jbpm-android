package org.jbpm.mobileclient.processView;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Supun Prabhath on 6/25/2015.
 * Project MobileClient
 */
public class ProcessObject implements Serializable{

    private String processId;
    private String name;
    private String deploymentId;
    private String version;

    private String[] processVariables;
    private String processSummery;


    /**
     * process details object
     **/
    public ProcessObject() {

    }


    public ProcessObject(String processDefId, String name, String deploymentId, String[] processVariables ,String Version) {

        this.processId = processDefId;
        this.name = name;
        this.version =Version;
        this.deploymentId = deploymentId;
        this.processVariables = processVariables;

    }

    public ProcessObject(String processDefId, String name, String ExternalId ,String Version) {

        this.processId = processDefId;
        this.name = name;
        this.version =Version;
        this.deploymentId=ExternalId;
    }
    public String toString(){
        return processId +" "+name+" "+ deploymentId +" "+ Arrays.toString(processVariables);
    }

    public String getProcessId() {
        return processId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public String[] getProcessVariables() {
        return processVariables;
    }

    public void setProcessSummery(String processSummery) {

        this.processSummery = processSummery;
    }
}