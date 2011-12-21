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
package org.escidoc.browser.elabsmodul.views.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.model.StudyBean;
import org.escidoc.browser.elabsmodul.views.AddNewStudyPublicationWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public final class LabsStudyTableHelper {

    private static final String motPubProperty1 = "path-mot", resPubProperty1 = "path-res";

    private static final String HTTP = "http://", HTTPS = "https://";

    private static final String ADD_BUTTON = "Add document";

    private static final String DELETE_BUTTON_TEXT = "Delete document";

    private static final String DELETES_BUTTON_TEXT = "Delete selected documents";

    private Button motPubDeleteButton = null, motPubAddButton = null;

    private Button resPubDeleteButton = null, resPubAddButton = null;

    private Table motPubTable = null, resPubTable = null;

    private IndexedContainer motPubContainer = null, resPubContainer = null;

    private StudyBean studyBean = null;

    private ILabsAction labsAction;

    private final boolean hasUpdateAccess;

    private static final Logger LOG = LoggerFactory.getLogger(LabsStudyTableHelper.class);

    public LabsStudyTableHelper(StudyBean bean, final ILabsAction action, final boolean hasUpdateAccess) {
        Preconditions.checkNotNull(bean, "Bean is null");
        Preconditions.checkNotNull(action, "Action is null");
        this.studyBean = bean;
        this.labsAction = action;
        this.hasUpdateAccess = hasUpdateAccess;
    }

    public VerticalLayout createTableLayoutForMotPublications() {
        final int RIG_TABLE_SIZE = 4;
        final Label selectedLabel = new Label("No selection");
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        motPubTable = new Table();
        motPubTable.setSelectable(true);
        motPubTable.setMultiSelect(true);
        motPubTable.setMultiSelectMode(MultiSelectMode.DEFAULT);
        motPubTable.setImmediate(true);
        motPubTable.setPageLength(RIG_TABLE_SIZE);
        motPubTable.setWidth("450px");
        motPubTable.setColumnReorderingAllowed(false);
        motPubTable.setColumnCollapsingAllowed(false);
        motPubTable.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        motPubTable.setContainerDataSource(fillMotPubTableData());
        motPubTable.setColumnHeaders(new String[] { "Url" });
        motPubTable.addListener(new Table.ValueChangeListener() {
            private static final long serialVersionUID = 2000562132182698589L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                int selSize = 0;
                Set<?> values = null;
                try {
                    values = (Set<?>) event.getProperty().getValue();
                }
                catch (ClassCastException e) {
                    LOG.warn("Table should be multiselectable!", e.getMessage());
                }
                if (values == null || values.size() == 0) {
                    selectedLabel.setValue("No selection");
                }
                else {
                    selSize = values.size();
                    selectedLabel.setValue("Selected: " + selSize + " document" + ((selSize > 1) ? "s" : ""));
                }

                if (selSize == 0) {
                    motPubDeleteButton.setEnabled(false);
                }
                else if (selSize == 1) {
                    motPubDeleteButton.setEnabled(true);
                    motPubDeleteButton.setCaption(DELETE_BUTTON_TEXT);
                }
                else {
                    motPubDeleteButton.setEnabled(true);
                    motPubDeleteButton.setCaption(DELETES_BUTTON_TEXT);
                }
            }
        });
        layout.addComponent(motPubTable);
        layout.addComponent(selectedLabel);
        addMotPubButtonsToLayout(layout);
        return layout;
    }

    private void addMotPubButtonsToLayout(final VerticalLayout layout) {
        Button.ClickListener motPubButtonsListener = new Button.ClickListener() {
            private static final long serialVersionUID = 1586321256611542129L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (event.getButton().getCaption().equals(DELETE_BUTTON_TEXT)
                    || event.getButton().getCaption().equals(DELETES_BUTTON_TEXT)) {
                    LabsStudyTableHelper.this.labsAction.showButtonLayout();
                    @SuppressWarnings("unchecked")
                    Set<String> selectedIdSet = (Set<String>) motPubTable.getValue();
                    // delete motivationg publication relations from the model
                    LabsStudyTableHelper.this.synchronizeStudyModel(selectedIdSet, true);
                    // instant delete from table containerdatasource
                    for (String idToDelete : selectedIdSet) {
                        motPubTable.getContainerDataSource().removeItem(idToDelete);
                    }
                    motPubTable.requestRepaint();
                }
                else if (event.getButton().getCaption().equals(ADD_BUTTON)) {
                    LabsStudyTableHelper.this.labsAction.showButtonLayout();
                    motPubTable
                        .getApplication().getMainWindow()
                        .addWindow(new AddNewStudyPublicationWindow(new AddNewStudyPublicationWindow.Callback() {
                            @Override
                            public void onAcceptAction(String inputURLText) {
                                // add to table
                                final String newURL =
                                    LabsStudyTableHelper.this.addnewItemToPublicationsTable(inputURLText, true);
                                // add to model
                                LabsStudyTableHelper.this.studyBean.getMotivatingPublication().add(newURL);
                            }
                        }, true));
                }
            }
        };
        motPubAddButton = new Button(ADD_BUTTON);
        motPubDeleteButton = new Button(DELETE_BUTTON_TEXT);
        if (hasUpdateAccess) {
            motPubAddButton.setEnabled(true);
            motPubAddButton.setVisible(true);
            motPubAddButton.setIcon(ELabsViewContants.ICON_16_OK);
            motPubDeleteButton.setEnabled(false);
            motPubDeleteButton.setVisible(true);
            motPubDeleteButton.setIcon(ELabsViewContants.ICON_16_CANCEL);
            motPubDeleteButton.addListener(motPubButtonsListener);
            motPubAddButton.addListener(motPubButtonsListener);
        }
        else {
            motPubAddButton.setVisible(false);
            motPubAddButton.setEnabled(false);
            motPubDeleteButton.setVisible(false);
            motPubDeleteButton.setEnabled(false);
        }
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(motPubAddButton);
        horizontalLayout.addComponent(motPubDeleteButton);
        layout.addComponent(horizontalLayout);
    }

    public VerticalLayout createTableLayoutForResPublications() {
        final int RIG_TABLE_SIZE = 4;
        final Label selectedLabel = new Label("No selection");
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        resPubTable = new Table();
        resPubTable.setSelectable(true);
        resPubTable.setMultiSelect(true);
        resPubTable.setMultiSelectMode(MultiSelectMode.DEFAULT);
        resPubTable.setImmediate(true);
        resPubTable.setPageLength(RIG_TABLE_SIZE);
        resPubTable.setWidth("450px");
        resPubTable.setColumnReorderingAllowed(false);
        resPubTable.setColumnCollapsingAllowed(false);
        resPubTable.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        resPubTable.setContainerDataSource(fillResPubTableData());
        resPubTable.setColumnHeaders(new String[] { "Url" });
        resPubTable.addListener(new Table.ValueChangeListener() {
            private static final long serialVersionUID = 2000562132182698589L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                int selSize = 0;
                Set<?> values = null;
                try {
                    values = (Set<?>) event.getProperty().getValue();
                }
                catch (ClassCastException e) {
                    LOG.warn("Table should be multiselectable!", e.getMessage());
                }
                if (values == null || values.size() == 0) {
                    selectedLabel.setValue("No selection");
                }
                else {
                    selSize = values.size();
                    selectedLabel.setValue("Selected: " + selSize + " document" + ((selSize > 1) ? "s" : ""));
                }
                if (selSize == 0) {
                    resPubDeleteButton.setEnabled(false);
                }
                else if (selSize == 1) {
                    resPubDeleteButton.setEnabled(true);
                    resPubDeleteButton.setCaption(DELETE_BUTTON_TEXT);
                }
                else {
                    resPubDeleteButton.setEnabled(true);
                    resPubDeleteButton.setCaption(DELETES_BUTTON_TEXT);
                }
            }
        });
        layout.addComponent(resPubTable);
        layout.addComponent(selectedLabel);
        addResPubButtonsToLayout(layout);
        return layout;
    }

    private void addResPubButtonsToLayout(final VerticalLayout layout) {
        Button.ClickListener resPubButtonsListener = new Button.ClickListener() {
            private static final long serialVersionUID = 1586321256611542129L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (event.getButton().getCaption().equals(DELETE_BUTTON_TEXT)
                    || event.getButton().getCaption().equals(DELETES_BUTTON_TEXT)) {
                    LabsStudyTableHelper.this.labsAction.showButtonLayout();
                    @SuppressWarnings("unchecked")
                    Set<String> selectedIdSet = (Set<String>) resPubTable.getValue();
                    // delete motivationg publication relations from the model
                    LabsStudyTableHelper.this.synchronizeStudyModel(selectedIdSet, false);
                    // instant delete from table containerdatasource
                    for (String idToDelete : selectedIdSet) {
                        resPubTable.getContainerDataSource().removeItem(idToDelete);
                    }
                    resPubTable.requestRepaint();
                }
                else if (event.getButton().getCaption().equals(ADD_BUTTON)) {
                    LabsStudyTableHelper.this.labsAction.showButtonLayout();
                    resPubTable
                        .getApplication().getMainWindow()
                        .addWindow(new AddNewStudyPublicationWindow(new AddNewStudyPublicationWindow.Callback() {
                            @Override
                            public void onAcceptAction(String inputURLText) {
                                // add to table
                                final String newURL =
                                    LabsStudyTableHelper.this.addnewItemToPublicationsTable(inputURLText, false);
                                // add to model
                                LabsStudyTableHelper.this.studyBean.getResultingPublication().add(newURL);
                            }
                        }, false));
                }
            }
        };

        resPubAddButton = new Button(ADD_BUTTON);
        resPubDeleteButton = new Button(DELETE_BUTTON_TEXT);
        if (hasUpdateAccess) {
            resPubAddButton.setEnabled(true);
            resPubAddButton.setVisible(true);
            resPubAddButton.setIcon(ELabsViewContants.ICON_16_OK);
            resPubDeleteButton.setEnabled(false);
            resPubDeleteButton.setVisible(true);
            resPubDeleteButton.setIcon(ELabsViewContants.ICON_16_CANCEL);
            resPubDeleteButton.addListener(resPubButtonsListener);
            resPubAddButton.addListener(resPubButtonsListener);
        }
        else {
            resPubAddButton.setVisible(false);
            resPubAddButton.setEnabled(false);
            resPubDeleteButton.setVisible(false);
            resPubDeleteButton.setEnabled(false);
        }

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(resPubAddButton);
        horizontalLayout.addComponent(resPubDeleteButton);
        layout.addComponent(horizontalLayout);
    }

    private void synchronizeStudyModel(final Set<String> selectedElements, boolean isMotNotResPublication) {
        List<String> idsToDelete = new ArrayList<String>();
        if (isMotNotResPublication) {
            for (String publicationString : this.studyBean.getMotivatingPublication()) {
                if (selectedElements.contains(publicationString)) {
                    idsToDelete.add(publicationString);
                }
            }
            this.studyBean.getMotivatingPublication().removeAll(idsToDelete);
        }
        else {
            for (String publicationString : this.studyBean.getResultingPublication()) {
                if (selectedElements.contains(publicationString)) {
                    idsToDelete.add(publicationString);
                }
            }
            this.studyBean.getResultingPublication().removeAll(idsToDelete);
        }
    }

    private IndexedContainer fillMotPubTableData() {
        motPubContainer = new IndexedContainer();
        motPubContainer.addContainerProperty(motPubProperty1, Link.class, null);
        for (String publicationURL : this.studyBean.getMotivatingPublication()) {
            Link link = createLinkByResourcePath(publicationURL);
            Item item = motPubContainer.addItem(publicationURL);
            if (item != null) {
                item.getItemProperty(motPubProperty1).setValue(link);
            }
        }
        motPubContainer.sort(new Object[] { motPubProperty1 }, new boolean[] { true });
        return motPubContainer;
    }

    private IndexedContainer fillResPubTableData() {
        resPubContainer = new IndexedContainer();
        resPubContainer.addContainerProperty(resPubProperty1, Link.class, null);
        for (String publicationURL : this.studyBean.getResultingPublication()) {
            Link link = createLinkByResourcePath(publicationURL);
            Item item = resPubContainer.addItem(publicationURL);
            if (item != null) {
                item.getItemProperty(resPubProperty1).setValue(link);
            }
        }
        resPubContainer.sort(new Object[] { resPubProperty1 }, new boolean[] { true });
        return resPubContainer;
    }

    private String addnewItemToPublicationsTable(final String newDocumentURL, boolean isMotNotResPublication) {
        Preconditions.checkNotNull(motPubContainer, "motPubContainer is null");
        Preconditions.checkNotNull(resPubContainer, "motPubContainer is null");
        Preconditions.checkNotNull(newDocumentURL, "document URL is null");

        final Link link = createLinkByResourcePath(newDocumentURL);
        if (isMotNotResPublication) {
            Item newItem = motPubContainer.addItem(newDocumentURL);
            if (newItem != null) {
                newItem.getItemProperty(motPubProperty1).setValue(link);
            }
            this.motPubTable.requestRepaint();
        }
        else {
            Item newItem = resPubContainer.addItem(newDocumentURL);
            if (newItem != null) {
                newItem.getItemProperty(resPubProperty1).setValue(link);
            }
            this.resPubTable.requestRepaint();
        }
        return link.getCaption();
    }

    private static Link createLinkByResourcePath(String inputUrl) {
        Preconditions.checkNotNull(inputUrl, "URL is null");
        String urlString = inputUrl.trim();

        if (!urlString.toLowerCase().startsWith(HTTP) && !urlString.toLowerCase().startsWith(HTTPS)) {
            urlString = HTTP + urlString;
        }

        String inputString = urlString;
        String fileFormat;

        while (inputString.endsWith("/")) {
            inputString = inputString.substring(0, inputString.length() - 1);
        }

        fileFormat = inputString.substring(inputString.lastIndexOf(".") + 1).toLowerCase();
        final Link link = new Link(urlString, new ExternalResource(urlString));
        link.setTargetName("_blank");

        if (fileFormat.startsWith("doc")) {
            link.setIcon(ELabsViewContants.ICON_16_DOC_DOC);
        }
        else if (fileFormat.startsWith("pdf")) {
            link.setIcon(ELabsViewContants.ICON_16_DOC_PDF);
        }
        else if (fileFormat.startsWith("ppt")) {
            link.setIcon(ELabsViewContants.ICON_16_DOC_PPT);
        }
        else if (fileFormat.startsWith("txt")) {
            link.setIcon(ELabsViewContants.ICON_16_DOC_TXT);
        }
        else if (fileFormat.startsWith("jpg") || fileFormat.startsWith("jpeg") || fileFormat.startsWith("png")) {
            link.setIcon(ELabsViewContants.ICON_16_DOC_IMG);
        }
        else {
            link.setIcon(ELabsViewContants.ICON_16_DOC_WEB);
        }
        return link;
    }
}
