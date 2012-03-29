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

import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class StudyBean implements Serializable, IBeanModel {

    private static final long serialVersionUID = -8047692815915730494L;

    private String objectId;

    private String identifier;

    private String name;

    private String description;

    private boolean configuration;

    private String context;

    private String contentModel;

    private List<String> motivatingPublication = new ArrayList<String>();

    private List<String> resultingPublication = new ArrayList<String>();

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

    public List<String> getMotivatingPublication() {
        return motivatingPublication;
    }

    public List<String> getResultingPublication() {
        return resultingPublication;
    }

    public void setMotivatingPublication(List<String> motivatingPublication) {
        this.motivatingPublication = motivatingPublication;
    }

    public void setResultingPublication(List<String> resultingPublication) {
        this.resultingPublication = resultingPublication;
    }

    @Override
    public String toString() {

        return name;
    }
}
