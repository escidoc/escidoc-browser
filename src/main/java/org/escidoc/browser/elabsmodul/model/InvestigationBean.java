package org.escidoc.browser.elabsmodul.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;

public class InvestigationBean implements Serializable, IBeanModel {

    private static final long serialVersionUID = 493568797506896729L;

    private String objid;

    private String name;

    private String description;

    private long maxRuntime;

    private String depositEndpoint;

    private String investigator;

    private String rig;

    private Map<String, String> instrumentFolder = new HashMap<String, String>();

    public String getObjid() {
        return objid;
    }

    public void setObjid(String objid) {
        this.objid = objid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getMaxRuntime() {
        return maxRuntime;
    }

    public void setMaxRuntime(long maxRuntime) {
        this.maxRuntime = maxRuntime;
    }

    public String getDepositEndpoint() {
        return depositEndpoint;
    }

    public void setDepositEndpoint(String depositEndpoint) {
        this.depositEndpoint = depositEndpoint;
    }

    public String getInvestigator() {
        return investigator;
    }

    public void setInvestigator(String investigator) {
        this.investigator = investigator;
    }

    public String getRig() {
        return rig;
    }

    public void setRig(String rig) {
        this.rig = rig;
    }

    public Map<String, String> getInstrumentFolder() {
        return instrumentFolder;
    }

    public void setInstrumentFolder(Map<String, String> instrumentFolder) {
        this.instrumentFolder = instrumentFolder;
    }

}
