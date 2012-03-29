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
package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.OrgUnitBuilder;
import org.escidoc.browser.model.internal.OrgUnitModel;
import org.escidoc.browser.repository.internal.OrganizationUnitRepository;
import org.escidoc.browser.ui.ViewConstants;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.oum.OrganizationalUnit;

public class CreateOrgUnitView {

    private final Form form = new Form();

    private final Window mainWindow;

    private final OrganizationUnitRepository repo;

    private final ResourceModel parent;

    private final Window subwindow = new Window();

    private final TreeDataSource dataSource;

    public CreateOrgUnitView(final Window mainWindow, final OrganizationUnitRepository repo,
        final ResourceModel parent, final TreeDataSource oUDS) {
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(repo, "repo is null: %s", repo);
        Preconditions.checkNotNull(parent, "selectedOrgUnit is null: %s", parent);
        Preconditions.checkNotNull(oUDS, "oUDS is null: %s", oUDS);
        this.repo = repo;
        this.mainWindow = mainWindow;
        this.parent = parent;
        this.dataSource = oUDS;
    }

    public void show() {
        buildForm();
        buildSubWindow();
        mainWindow.addWindow(subwindow);
    }

    private void buildSubWindow() {
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(form);
    }

    @SuppressWarnings("serial")
    private void buildForm() {
        form.setImmediate(true);

        // Name
        final TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Name");
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        form.addField("txtNameContext", txtNameContext);

        // Description
        final TextField txtDescContext = new TextField("Description");
        txtDescContext.setImmediate(false);
        txtDescContext.setWidth("-1px");
        txtDescContext.setHeight("-1px");
        form.addField("txtDescContext", txtDescContext);

        // btnAddContext
        final Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    final OrgUnitModel child = storeInRepository(txtNameContext, txtDescContext);
                    updateTree(child);
                    form.commit();
                    resetFields();
                    showSuccesfullMessage(txtNameContext);
                }
                catch (final EmptyValueException e) {
                    mainWindow.showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (final Exception e) {
                    mainWindow.showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
                finally {
                    closeSubWindow();
                }
            }

            private void updateTree(final OrgUnitModel child) {
                dataSource.addChild(parent, child);
            }

            private void showSuccesfullMessage(final TextField txtNameContext) {
                mainWindow.showNotification("Organizational Unit " + txtNameContext.getValue().toString()
                    + " created successfully ", Window.Notification.TYPE_TRAY_NOTIFICATION);
            }

            private void resetFields() {
                form.getField("txtNameContext").setValue("");
                form.getField("txtDescContext").setValue("");
            }

            private void closeSubWindow() {
                mainWindow.removeWindow(subwindow);
            }

            private OrgUnitModel storeInRepository(final TextField txtNameContext, final TextField txtDescContext)
                throws EscidocClientException, ParserConfigurationException, SAXException, IOException {
                final OrgUnitBuilder orgBuilder = new OrgUnitBuilder();
                OrganizationalUnit orgUnit =
                    orgBuilder.with(txtNameContext.getValue().toString(), txtDescContext.getValue().toString()).build();

                if (parent != null) {
                    Set set = new HashSet();
                    set.add(parent.getId());
                    orgBuilder.parents(set);
                }
                return repo.create(orgUnit);

            }
        });

        btnAddContext.setWidth("-1px");
        btnAddContext.setHeight("-1px");
        form.getLayout().addComponent(btnAddContext);

        form.getField("txtNameContext").setRequired(true);
        form.getField("txtNameContext").setRequiredError("Name is missing");

        form.getField("txtDescContext").setRequired(true);
        form.getField("txtDescContext").setRequiredError("Description is missing");
    }
}
