package org.escidoc.browser.elabsmodul.views.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.model.StudyBean;
import org.escidoc.browser.elabsmodul.views.AddNewStudyPublicationWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public final class LabsStudyTableHelper {

    // properties for the tables def
    private final String motPubProperty1 = "path-mot", resPubProperty1 = "path-res";

    // buttons related to the tables
    private Button motPubDeleteButton = null, motPubAddButton = null;

    private Button resPubDeleteButton = null, resPubAddButton = null;

    // table and container references
    private Table motPubTable = null, resPubTable = null;

    private IndexedContainer motPubContainer = null, resPubContainer = null;

    // model references
    private StudyBean studyBean = null;

    // common texts
    private final String ADD_BUTTON = "Add document";

    private final String DELETE_BUTTON_TEXT = "Delete document";

    private final String DELETES_BUTTON_TEXT = "Delete selected documents";

    private static LabsStudyTableHelper singleton = null;

    private static Object syncObject = new Object();

    private static final Logger LOG = LoggerFactory.getLogger(LabsStudyTableHelper.class);

    private LabsStudyTableHelper() {
    }

    // TODO refactor , do not use Singleton DP
    public static LabsStudyTableHelper singleton() {
        if (singleton == null) {
            synchronized (syncObject) {
                if (singleton == null) {
                    singleton = new LabsStudyTableHelper();
                }
            }
        }
        return singleton;
    }

    public synchronized void setModel(final StudyBean studyBean) {
        Preconditions.checkNotNull(studyBean, "studyBean is null");
        this.studyBean = studyBean;
    }

    public synchronized VerticalLayout createTableLayoutForMotPublications() {

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

        motPubTable.setColumnReorderingAllowed(false);
        motPubTable.setColumnCollapsingAllowed(false);
        motPubTable.setRowHeaderMode(Table.ROW_HEADER_MODE_HIDDEN);

        motPubTable.setContainerDataSource(fillMotPubTableData(studyBean.getMotivatingPublication()));
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

        motPubAddButton = new Button(ADD_BUTTON);
        motPubAddButton.setEnabled(true);
        motPubAddButton.setVisible(true);
        motPubAddButton.setIcon(ELabsViewContants.ICON_16_OK);

        motPubDeleteButton = new Button(DELETE_BUTTON_TEXT);
        motPubDeleteButton.setEnabled(false);
        motPubDeleteButton.setVisible(true);
        motPubDeleteButton.setIcon(ELabsViewContants.ICON_16_CANCEL);

        Button.ClickListener motPubButtonsListener = new Button.ClickListener() {
            private static final long serialVersionUID = 1586321256611542129L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (event.getButton().getCaption().equals(DELETE_BUTTON_TEXT)
                    || event.getButton().getCaption().equals(DELETES_BUTTON_TEXT)) {
                    @SuppressWarnings("unchecked")
                    Set<String> selectedIdSet = (Set<String>) motPubTable.getValue();

                    // delete motivationg publication relations from the model
                    LabsStudyTableHelper.this.synchronizeStudyModel(selectedIdSet, true);

                    // instant delete from table containerdatasource
                    for (Iterator<String> iterator = selectedIdSet.iterator(); iterator.hasNext();) {
                        String idToDelete = iterator.next();
                        motPubTable.getContainerDataSource().removeItem(idToDelete);
                    }
                    motPubTable.requestRepaint();
                }
                else if (event.getButton().getCaption().equals(ADD_BUTTON)) {
                    motPubTable
                        .getApplication().getMainWindow()
                        .addWindow(new AddNewStudyPublicationWindow(new AddNewStudyPublicationWindow.Callback() {
                            @Override
                            public void onAcceptAction(String inputURLText) {
                                // add to model
                                LabsStudyTableHelper.this.studyBean.getMotivatingPublication().add(inputURLText);
                                // add to table
                                LabsStudyTableHelper.this.addnewItemToPublicationsTable(inputURLText, true);
                            }
                        }));
                }
            }
        };

        motPubDeleteButton.addListener(motPubButtonsListener);
        motPubAddButton.addListener(motPubButtonsListener);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(motPubAddButton);
        horizontalLayout.addComponent(motPubDeleteButton);
        layout.addComponent(horizontalLayout);
    }

    public synchronized VerticalLayout createTableLayoutForResPublications() {

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

        resPubTable.setColumnReorderingAllowed(false);
        resPubTable.setColumnCollapsingAllowed(false);
        resPubTable.setRowHeaderMode(Table.ROW_HEADER_MODE_HIDDEN);

        resPubTable.setContainerDataSource(fillResPubTableData(studyBean.getResultingPublication()));
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

        resPubAddButton = new Button(ADD_BUTTON);
        resPubAddButton.setEnabled(true);
        resPubAddButton.setVisible(true);
        resPubAddButton.setIcon(ELabsViewContants.ICON_16_OK);

        resPubDeleteButton = new Button(DELETE_BUTTON_TEXT);
        resPubDeleteButton.setEnabled(false);
        resPubDeleteButton.setVisible(true);
        resPubDeleteButton.setIcon(ELabsViewContants.ICON_16_CANCEL);

        Button.ClickListener resPubButtonsListener = new Button.ClickListener() {
            private static final long serialVersionUID = 1586321256611542129L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (event.getButton().getCaption().equals(DELETE_BUTTON_TEXT)
                    || event.getButton().getCaption().equals(DELETES_BUTTON_TEXT)) {
                    @SuppressWarnings("unchecked")
                    Set<String> selectedIdSet = (Set<String>) resPubTable.getValue();

                    // delete motivationg publication relations from the model
                    LabsStudyTableHelper.this.synchronizeStudyModel(selectedIdSet, false);

                    // instant delete from table containerdatasource
                    for (Iterator<String> iterator = selectedIdSet.iterator(); iterator.hasNext();) {
                        String idToDelete = iterator.next();
                        resPubTable.getContainerDataSource().removeItem(idToDelete);
                    }
                    resPubTable.requestRepaint();
                }
                else if (event.getButton().getCaption().equals(ADD_BUTTON)) {
                    resPubTable
                        .getApplication().getMainWindow()
                        .addWindow(new AddNewStudyPublicationWindow(new AddNewStudyPublicationWindow.Callback() {
                            @Override
                            public void onAcceptAction(String inputURLText) {
                                // add to model
                                LabsStudyTableHelper.this.studyBean.getResultingPublication().add(inputURLText);
                                // add to table
                                LabsStudyTableHelper.this.addnewItemToPublicationsTable(inputURLText, false);
                            }
                        }));
                }
            }
        };

        resPubDeleteButton.addListener(resPubButtonsListener);
        resPubAddButton.addListener(resPubButtonsListener);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(resPubAddButton);
        horizontalLayout.addComponent(resPubDeleteButton);
        layout.addComponent(horizontalLayout);
    }

    private void synchronizeStudyModel(final Set<String> selectedElements, boolean isMotNotResPublication) {
        List<String> idsToDelete = new ArrayList<String>();
        if (isMotNotResPublication) {
            for (Iterator<String> iterator = this.studyBean.getMotivatingPublication().iterator(); iterator.hasNext();) {
                String publicationString = iterator.next();
                if (selectedElements.contains(publicationString)) {
                    idsToDelete.add(publicationString);
                }
            }
            this.studyBean.getMotivatingPublication().removeAll(idsToDelete);
        }
        else {
            for (Iterator<String> iterator = this.studyBean.getResultingPublication().iterator(); iterator.hasNext();) {
                String publicationString = iterator.next();
                List<String> idsToDelet = new ArrayList<String>();
                if (selectedElements.contains(publicationString)) {
                    idsToDelete.add(publicationString);
                }
            }
            this.studyBean.getResultingPublication().removeAll(idsToDelete);
        }
    }

    private IndexedContainer fillMotPubTableData(final List<String> motivationPublications) {
        motPubContainer = new IndexedContainer();
        motPubContainer.addContainerProperty(motPubProperty1, String.class, null);
        for (Iterator<String> iterator = this.studyBean.getMotivatingPublication().iterator(); iterator.hasNext();) {
            String prublicationURLs = iterator.next();
            Item item = motPubContainer.addItem(prublicationURLs);
            if (item != null) {
                item.getItemProperty(motPubProperty1).setValue(prublicationURLs);
            }
        }
        motPubContainer.sort(new Object[] { motPubProperty1 }, new boolean[] { true });
        return motPubContainer;
    }

    private IndexedContainer fillResPubTableData(final List<String> motivationPublications) {
        resPubContainer = new IndexedContainer();
        resPubContainer.addContainerProperty(resPubProperty1, String.class, null);
        for (Iterator<String> iterator = this.studyBean.getResultingPublication().iterator(); iterator.hasNext();) {
            String prublicationURLs = iterator.next();
            Item item = resPubContainer.addItem(prublicationURLs);
            if (item != null) {
                item.getItemProperty(resPubProperty1).setValue(prublicationURLs);
            }
        }
        resPubContainer.sort(new Object[] { resPubProperty1 }, new boolean[] { true });
        return resPubContainer;
    }

    private void addnewItemToPublicationsTable(final String newDocumentURL, boolean isMotNotResPublication) {
        Preconditions.checkNotNull(motPubContainer, "motPubContainer is null");
        Preconditions.checkNotNull(resPubContainer, "motPubContainer is null");
        Preconditions.checkNotNull(newDocumentURL, "document URL is null");

        if (isMotNotResPublication) {
            Item newItem = motPubContainer.addItem(newDocumentURL);
            if (newItem != null) {
                newItem.getItemProperty(motPubProperty1).setValue(newDocumentURL);
            }
            this.motPubTable.requestRepaint();
        }
        else {
            Item newItem = resPubContainer.addItem(newDocumentURL);
            if (newItem != null) {
                newItem.getItemProperty(resPubProperty1).setValue(newDocumentURL);
            }
            this.resPubTable.requestRepaint();
        }
    }
}
