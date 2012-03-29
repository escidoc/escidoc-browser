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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.elabsmodul.model;

import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;

import java.io.Serializable;
import java.util.Date;

public class InstrumentBean implements Serializable, IBeanModel {

    private static final long serialVersionUID = -7950795372352237485L;

    private String objectId;

    private String identifier;

    private String name;

    private String description;

    private Date createdOn;

    private String createdBy;

    private Date modifiedOn;

    private String modifiedBy;

    private boolean configuration;

    private boolean calibration;

    private String eSyncDaemon;

    private String folder;

    private String fileFormat;

    private String context;

    private String contentModel;

    private String deviceSupervisor;

    private String institute;

    private String incarnationType;

    private String costCenter;

    private boolean operator;

    /**
     * @return the objectId
     */
    public final String getObjectId() {

        return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public final void setObjectId(final String objectId) {

        this.objectId = objectId;
    }

    /**
     * @return the id
     */
    public final String getIdentifier() {

        return identifier;
    }

    /**
     * @param id
     *            the id to set
     */
    public final void setIdentifier(final String id) {

        identifier = id;
    }

    /**
     * @return the title
     */

    public final String getName() {

        return name;
    }

    /**
     * @param title
     *            the title to set
     */
    public final void setName(final String title) {

        name = title;
    }

    /**
     * @return the description
     */

    public final String getDescription() {

        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public final void setDescription(final String description) {

        this.description = description;
    }

    /**
     * @return the configuration
     */
    public final boolean getConfiguration() {

        return configuration;
    }

    /**
     * @param configuration
     *            the configuration to set
     */
    public final void setConfiguration(final boolean configuration) {

        this.configuration = configuration;
    }

    /**
     * @return the calibrationProperty
     */
    public final boolean getCalibration() {

        return calibration;
    }

    /**
     * @param calibration
     *            the calibrationProperty to set
     */
    public final void setCalibration(final boolean calibration) {

        this.calibration = calibration;
    }

    /**
     * @return the eSyncDaemon
     */
    public final String getESyncDaemon() {

        return eSyncDaemon;
    }

    /**
     * @param eSyncDaemon
     *            the eSyncDaemon to set
     */
    public final void setESyncDaemon(final String eSyncDaemon) {

        this.eSyncDaemon = eSyncDaemon;
    }

    /**
     * @return the folder
     */
    public final String getFolder() {

        return folder;
    }

    /**
     * @return the createdOn
     */

    public final Date getCreatedOn() {

        return createdOn;
    }

    /**
     * @param createdOn
     *            the createdOn to set
     */

    public final void setCreatedOn(final Date createdOn) {

        this.createdOn = createdOn;
    }

    /**
     * @return the createdBy
     */

    public final String getCreatedBy() {

        return createdBy;
    }

    /**
     * @param createdBy
     *            the createdBy to set
     */

    public final void setCreatedBy(final String createdBy) {

        this.createdBy = createdBy;
    }

    /**
     * @return the modifiedOn
     */

    public final Date getModifiedOn() {

        return modifiedOn;
    }

    /**
     * @param modifiedOn
     *            the modifiedOn to set
     */

    public final void setModifiedOn(final Date modifiedOn) {

        this.modifiedOn = modifiedOn;
    }

    /**
     * @return the modifiedBy
     */

    public final String getModifiedBy() {

        return modifiedBy;
    }

    /**
     * @param modifiedBy
     *            the modifiedBy to set
     */

    public final void setModifiedBy(final String modifiedBy) {

        this.modifiedBy = modifiedBy;
    }

    /**
     * @param folder
     *            the folder to set
     */
    public final void setFolder(final String folder) {

        this.folder = folder;
    }

    /**
     * @return the fileFormat
     */
    public final String getFileFormat() {

        return fileFormat;
    }

    /**
     * @param fileFormat
     *            the fileFormat to set
     */
    public final void setFileFormat(final String fileFormat) {

        this.fileFormat = fileFormat;
    }

    /**
     * @return the contentModel
     */

    public final String getContentModel() {

        return contentModel;
    }

    /**
     * @param contentModel
     *            the contentModel to set
     */

    public final void setContentModel(final String contentModel) {

        this.contentModel = contentModel;
    }

    /**
     * @return the deviceSupervisor
     */
    public final String getDeviceSupervisor() {

        return deviceSupervisor;
    }

    /**
     * @param deviceSupervisor
     *            the deviceSupervisor to set
     */
    public final void setDeviceSupervisor(final String deviceSupervisor) {

        this.deviceSupervisor = deviceSupervisor;
    }

    /**
     * @return the institute
     */
    public final String getInstitute() {

        return institute;
    }

    /**
     * @param institute
     *            the institute to set
     */
    public final void setInstitute(final String institute) {

        this.institute = institute;
    }

    /**
     * @return the incarnationType
     */
    public final String getIncarnationType() {

        return incarnationType;
    }

    /**
     * @param incarnationType
     *            the incarnationType to set
     */
    public final void setIncarnationType(final String incarnationType) {

        this.incarnationType = incarnationType;
    }

    /**
     * @return the costCenter
     */
    public final String getCostCenter() {

        return costCenter;
    }

    /**
     * @return the context
     */

    public final String getContext() {

        return context;
    }

    /**
     * @param context
     *            the context to set
     */

    public final void setContext(final String context) {

        this.context = context;
    }

    /**
     * @param costCenter
     *            the costCenter to set
     */
    public final void setCostCenter(final String costCenter) {

        this.costCenter = costCenter;
    }

    @Override
    public String toString() {

        return name;
    }

    /**
     * @return the operator
     */
    public final boolean isOperator() {
        return operator;
    }

    /**
     * @param operator
     *            the operator to set
     */
    public final void setOperator(final boolean operator) {
        this.operator = operator;
    }
}
