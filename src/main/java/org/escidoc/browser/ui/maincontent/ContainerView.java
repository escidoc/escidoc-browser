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
package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

/**
 * @author ARB
 * 
 */
public class ContainerView extends VerticalLayout {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final int appHeight;

    private final MainSite mainSite;

    private final ContainerProxy resourceProxy;

    private final CssLayout cssLayout = new CssLayout();

    private static final String DESCRIPTION = "Description: ";

    private static final String CREATED_BY = "Created by";

    private static final String NAME = "Name: ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by";

    private static final String DIRECT_MEMBERS = "Direct Members";

    private static final String RESOURCE_NAME = "Container: ";

    private static final String STATUS = "Status is ";

    private int accordionHeight;

    private int innerelementsHeight;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    private boolean isEditing = false;

    Button btnEdit = null;

    private Component oldComponent = null;

    private Component swapComponent = null;

    public ContainerView(final EscidocServiceLocation serviceLocation, final MainSite mainSite,
        final ResourceProxy resourceProxy, final Window mainWindow, final CurrentUser currentUser,
        final Repositories repositories) throws EscidocClientException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, resourceProxy.getClass()
            + " is not an instance of ContainerProxy.class");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, resourceProxy.getClass()
            + " is not an instance of ContainerProxy.class");
        this.serviceLocation = serviceLocation;
        this.mainSite = mainSite;
        appHeight = mainSite.getApplicationHeight();
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;
        this.repositories = repositories;
        init();
    }

    private void init() throws EscidocClientException {
        configureLayout();
        handleLayoutListeners();
        createBreadCrumb();
        bindNameToHeader();
        createEditBtn();
        bindDescription();
        addHorizontalRuler();
        bindProperties();
        addDirectMembers();
        addMetadataRecords();
        addComponent(cssLayout);
    }

    private void addMetadataRecords() {
        final MetadataRecs metaData =
            new MetadataRecs(resourceProxy, accordionHeight, mainWindow, serviceLocation, repositories);
        rightCell(metaData.asAccord());
    }

    private void addDirectMembers() throws EscidocClientException {
        final DirectMember directMembers =
            new DirectMember(serviceLocation, mainSite, resourceProxy.getId(), mainWindow, currentUser, repositories);
        leftCell(DIRECT_MEMBERS, directMembers.containerAsTree());
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound
     * 
     * @param comptoBind
     */
    private void rightCell(final Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("82%");
        rightpnl.getLayout().setMargin(false);
        rightpnl.addComponent(comptoBind);
        cssLayout.addComponent(rightpnl);
    }

    @SuppressWarnings("deprecation")
    private void leftCell(final String string, final Component comptoBind) {
        final Panel leftpnl = new Panel();

        leftpnl.setStyleName("directmembers floatleft paddingtop10 ");
        leftpnl.setScrollable(false);
        leftpnl.getLayout().setMargin(false);

        leftpnl.setWidth("30%");
        leftpnl.setHeight("82%");

        final Label nameofPanel = new Label("<strong>" + DIRECT_MEMBERS + "</string>", Label.CONTENT_RAW);
        leftpnl.addComponent(nameofPanel);
        leftpnl.addComponent(comptoBind);

        // Adding some buttons
        final AbsoluteLayout absL = new AbsoluteLayout();
        absL.setWidth("100%");
        absL.setHeight(innerelementsHeight + "px");
        final HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.addComponent(new Button("Add"));
        horizontal.addComponent(new Button("Delete"));
        horizontal.addComponent(new Button("Edit"));
        leftpnl.addComponent(horizontal);

        absL.addComponent(horizontal, "left: 0px; top: 380px;");
        cssLayout.addComponent(leftpnl);
    }

    /**
     * Bindind Context Properties 2 sets of labels in 2 rows
     */
    private void bindProperties() {
        // LEFT SIde
        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());
        final Label lblStatus = new Label(STATUS + resourceProxy.getStatus());
        descMetadata1.setStyleName("floatleft");
        lblStatus.setStyleName("floatleft");
        lblStatus.setDescription("status");
        descMetadata1.setWidth("35%");
        lblStatus.setWidth("35%");
        cssLayout.addComponent(descMetadata1);

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(CREATED_BY + "<a href='#'> " + resourceProxy.getCreator() + "</a> "
                + resourceProxy.getCreatedOn() + "<br>" + LAST_MODIFIED_BY + " <a href='#user/"
                + resourceProxy.getModifier() + "'>" + resourceProxy.getModifier() + "</a>", Label.CONTENT_RAW);
        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("65%");
        cssLayout.addComponent(descMetadata2);
        cssLayout.addComponent(lblStatus);

    }

    private void addHorizontalRuler() {
        final Label descRuler = new Label("<hr />", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindDescription() {
        final Label description = new Label(resourceProxy.getDescription());
        description.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(description);
    }

    private void createBreadCrumb() {
        new BreadCrumbMenu(cssLayout, resourceProxy, mainWindow, serviceLocation, repositories);
    }

    /**
     * Building the Header Element that shows the title of the Container
     */
    private void bindNameToHeader() {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth floatleft");
        headerContext.setWidth("80%");
        headerContext.setDescription("header");
        cssLayout.addComponent(headerContext);
    }

    private void createEditBtn() {
        btnEdit = new Button("Edit Container", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                btnEdit.setCaption("Do not push this button again");

            }
        });
        btnEdit.setStyleName("floatright");
        cssLayout.addComponent(btnEdit);

    }

    private void configureLayout() {
        setMargin(true, true, false, true);
        this.setHeight("100%");
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        // this is an assumption of the height that should be left for the
        // accordion or elements of the DirectMember in the same level
        // I remove 420px that are taken by elements on the de.escidoc.esdc.page
        // and 40px for the accordion elements?
        final int innerelementsHeight = appHeight - 420;
        accordionHeight = innerelementsHeight - 20;

    }

    private void handleLayoutListeners() {

        cssLayout.addListener(new LayoutClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void layoutClick(LayoutClickEvent event) {
                // Get the child component which was clicked
                if (event.getChildComponent() != null) {
                    // Is Label?
                    if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                        Label child = (Label) event.getChildComponent();

                        if ((child).getDescription() == "header") {
                            oldComponent = event.getClickedComponent();
                            swapComponent = editHeader(child.getValue().toString());
                            cssLayout.replaceComponent(oldComponent, swapComponent);
                            isEditing = true;
                        }
                        else if (child.getDescription() == "status") {
                            oldComponent = event.getClickedComponent();
                            swapComponent = editStatus(child.getValue().toString());
                            cssLayout.replaceComponent(oldComponent, swapComponent);
                            isEditing = true;
                        }
                    }
                    else {
                        getWindow().showNotification(
                            "The click was over a " + event.getChildComponent().getClass().getCanonicalName());
                    }
                }
                else {
                    if (swapComponent != null) {
                        if (swapComponent instanceof Label) {
                            System.out.println("oldComponent is label");
                            ((Label) oldComponent).setValue(((TextField) swapComponent).getValue());
                        }
                        else if ((swapComponent instanceof ComboBox)) {
                            ((Label) oldComponent).setValue(STATUS + ((ComboBox) swapComponent).getValue());
                        }
                        cssLayout.replaceComponent(swapComponent, oldComponent);
                        swapComponent = null;
                    }
                }
            }

            private Component editStatus(String string) {
                ComboBox cmbStatus = new ComboBox();
                cmbStatus.addItem("pending");
                return cmbStatus;
            }

            private Component editHeader(String lblHeaderValue) {
                final TextField txtHeader = new TextField();
                txtHeader.setValue(lblHeaderValue.replaceAll(RESOURCE_NAME, ""));
                return txtHeader;
            }

        });
    }

    /**
     * Checks if a resource has previous history and returns a string TODO in the future it should be a Link (Button
     * Link) that holds a reference to the history of the resource
     * 
     * @return String
     */
    private String getHistory() {
        String strHistory;
        if (resourceProxy.getPreviousVersion()) {
            strHistory = " previous version";
        }
        else {
            strHistory = " has no previous history";
        }
        return strHistory;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceProxy == null) ? 0 : resourceProxy.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContainerView other = (ContainerView) obj;
        if (resourceProxy == null) {
            if (other.resourceProxy != null) {
                return false;
            }
        }
        else if (!resourceProxy.equals(other.resourceProxy)) {
            return false;
        }
        return true;
    }
}
