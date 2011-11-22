/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
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

    private String rigComplexId;

    private RigBean rigBean;

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

    public Map<String, String> getInstrumentFolder() {
        return instrumentFolder;
    }

    public void setInstrumentFolder(Map<String, String> instrumentFolder) {
        this.instrumentFolder = instrumentFolder;
    }

    public RigBean getRigBean() {
        return rigBean;
    }

    public void setRigBean(RigBean rigBean) {
        this.rigBean = rigBean;
    }

    public String getRigComplexId() {
        return rigComplexId;
    }

    public void setRigComplexId(String rigComplexId) {
        this.rigComplexId = rigComplexId;
    }
}
