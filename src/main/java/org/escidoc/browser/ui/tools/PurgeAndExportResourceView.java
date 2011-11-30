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
package org.escidoc.browser.ui.tools;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.escidoc.browser.AppConstants;
import org.escidoc.browser.Utils;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.Style.H2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.adm.AdminStatus;
import de.escidoc.core.resources.adm.MessagesStatus;

@SuppressWarnings("serial")
public class PurgeAndExportResourceView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeAndExportResourceView.class);

    private final class FilterButtonListener implements ClickListener {

        private static final String EXPORT_FILENAME = "escidoc-xml-export.zip";

        private final class PurgeButtonListener implements ClickListener {
            @Override
            public void buttonClick(ClickEvent event) {
                tryPurge(getSelectedResourceIds(getSelectedResources()));
            }

            private Set<String> getSelectedResourceIds(Set<ResourceModel> selectedResources) {
                final Set<String> objectIds = new HashSet<String>(selectedResources.size());
                for (final ResourceModel resource : selectedResources) {
                    objectIds.add(resource.getId());
                }
                return objectIds;
            }

            private void tryPurge(Set<String> objectIds) {
                try {
                    showPurgeStatus(startPurging(objectIds));
                }
                catch (final EscidocClientException e) {
                    LOG.error("Internal Server Error while purging resources. " + e);
                    showErrorMessage(e);
                }
            }

            private void showPurgeStatus(MessagesStatus status) throws EscidocClientException {
                if (status.getStatusCode() == AdminStatus.STATUS_INVALID_RESULT) {
                    showErrorMessage(status);
                }
                if (status.getStatusCode() == AdminStatus.STATUS_FINISHED) {
                    showSucess(status);
                }
                else if (status.getStatusCode() == AdminStatus.STATUS_IN_PROGRESS) {
                    showPurgeStatus(repositories.admin().retrievePurgeStatus());
                }
                else {
                    showErrorMessage(status);
                }
            }

            private void showSucess(MessagesStatus status) {
                router.getMainWindow().showNotification(ViewConstants.INFO, status.getStatusMessage(),
                    Notification.TYPE_TRAY_NOTIFICATION);
            }

            private MessagesStatus startPurging(Set<String> objectIds) throws EscidocClientException {
                return repositories.admin().purge(objectIds);
            }
        }

        @SuppressWarnings("unchecked")
        private Set<ResourceModel> getSelectedResources() {
            Object object = resultTable.getValue();
            if (object instanceof Set) {
                return (Set<ResourceModel>) object;
            }
            return Collections.emptySet();
        }

        private static final boolean IS_EXPORT_PERMITTTED = true;

        private VerticalLayout resultLayout = new VerticalLayout();

        private Table resultTable;

        @Override
        public void buttonClick(ClickEvent event) {
            try {
                showResult(getFilterResult());
                if (isPurgePermitted()) {
                    showPurgeView();
                }
                if (isContentModelSelected() && isExportPermitted()) {
                    showExportView();
                }
            }
            catch (EscidocClientException e) {
                LOG.error(e.getMessage());
                showErrorMessage(e);
            }
            catch (URISyntaxException e) {
                LOG.error(e.getMessage());
                showErrorMessage(e);
            }
        }

        private boolean isContentModelSelected() {
            return getSelectedType().equals(ResourceType.CONTENT_MODEL);
        }

        private void showExportView() {
            Button exportButton = new Button(ViewConstants.EXPORT);
            exportButton.setStyleName(Reindeer.BUTTON_SMALL);
            resultLayout.addComponent(exportButton);
            exportButton.addListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    final Set<ResourceModel> selectedResources = getSelectedResources();
                    router.getMainWindow().open(new StreamResource(new StreamSource() {

                        @Override
                        public InputStream getStream() {
                            try {
                                return zip(selectedResources);
                            }
                            catch (IOException e) {
                                showErrorMessage(e);
                            }
                            catch (EscidocClientException e) {
                                showErrorMessage(e);
                            }
                            return null;
                        }

                    }, EXPORT_FILENAME, router.getApp()), "download");
                }
            });
        }

        private ByteArrayInputStream zip(Set<ResourceModel> set) throws IOException, EscidocClientException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Closeable res = out;
            try {
                ZipOutputStream zout = new ZipOutputStream(out);
                res = zout;
                for (ResourceModel resourceModel : set) {
                    zout.putNextEntry(new ZipEntry(resourceModel.getId()));
                    String asString = repositories.contentModel().getAsXmlString(resourceModel.getId());
                    InputStream is = new ByteArrayInputStream(asString.getBytes("UTF-8"));
                    Utils.copy(is, zout);
                    zout.closeEntry();
                }
            }
            finally {
                res.close();
            }
            return new ByteArrayInputStream(out.toByteArray());

        }

        private boolean isExportPermitted() {
            return IS_EXPORT_PERMITTTED;
        }

        private boolean isPurgePermitted() throws EscidocClientException, URISyntaxException {
            return repositories.pdp().forCurrentUser().isAction(ActionIdConstants.PURGE_RESOURCES).permitted();
        }

        private void showPurgeView() {
            addHintForSelection();
            addPurgeButton();
        }

        private void addHintForSelection() {
            final Label hintText =
                new Label(
                    "<div><em>Hint: </em>"
                        + "To select multiple resources, hold down the CONTROL key while you click on the resource.</br></div>"
                        + "<strong>Warning:</strong> Purging resources can cause inconsitencies in the repository.</div>",
                    Label.CONTENT_XHTML);
            resultLayout.addComponent(hintText);
        }

        private void addPurgeButton() {
            final Button purgeButton = new Button(ViewConstants.PURGE);
            purgeButton.setStyleName(Reindeer.BUTTON_SMALL);
            resultLayout.addComponent(purgeButton);
            purgeButton.addListener(new PurgeButtonListener());
        }

        private void showResult(List<ResourceModel> result) {
            LOG.debug("Found: " + result.size());
            emptyPreviousResult();
            if (isEmpty(result)) {
                showNoResult();
            }
            else {
                showFilterResultView(createFilterResultView(result));
            }
        }

        private void emptyPreviousResult() {
            resultLayout.removeAllComponents();
            resultLayout.addComponent(new Style.Ruler());
        }

        private void showFilterResultView(Table table) {
            resultLayout.addComponent(table);
            addComponent(resultLayout);
        }

        private Table createFilterResultView(List<ResourceModel> result) {
            LOG.debug("found filtered resources: " + result.size());

            resultTable =
                new Table("Found: " + result.size() + " " + result.get(0).getType().getLabel() + "s",
                    new BeanItemContainer<ResourceModel>(ResourceModel.class, result));
            resultTable.setWidth("100%");
            resultTable.setHeight("100%");
            resultTable.setSizeFull();
            resultTable.setColumnWidth("id", -1);
            resultTable.setColumnExpandRatio("name", 1);
            resultTable.setColumnHeader(PropertyId.ID, ViewConstants.ID);
            resultTable.setColumnHeader(PropertyId.NAME, ViewConstants.NAME);
            resultTable.setColumnHeader(PropertyId.TYPE, ViewConstants.TYPE);
            resultTable.setSelectable(true);
            resultTable.setMultiSelect(true);
            return resultTable;
        }

        private void showNoResult() {
            resultLayout.addComponent(new Label(ViewConstants.NO_RESULT));
            addComponent(resultLayout);
        }

        private boolean isEmpty(List<ResourceModel> result) {
            return result.isEmpty();

        }

        private List<ResourceModel> getFilterResult() throws EscidocClientException {
            if (isInputEmpty()) {
                return getRepository().findAll();
            }
            return getRepository().filterUsingInput(getRawFilter());
        }

        private Repository getRepository() {
            switch (getSelectedType()) {
                case ITEM:
                    return repositories.item();
                case CONTAINER:
                    return repositories.container();
                case CONTEXT:
                    return repositories.context();
                case CONTENT_MODEL:
                    return repositories.contentModel();
                default:
                    router.getMainWindow().showNotification(getSelectedType() + " not yet supported",
                        Window.Notification.TYPE_WARNING_MESSAGE);
            }
            throw new UnsupportedOperationException(getSelectedType() + " not yet supported");
        }

        private boolean isInputEmpty() {
            return getRawFilter().isEmpty();

        }

        private ResourceType getSelectedType() {
            final Object value = resourceOption.getValue();
            if (value instanceof ResourceType) {
                return (ResourceType) value;
            }
            return ResourceType.ITEM;
        }

        private String getRawFilter() {
            final Object value = textField.getValue();
            if (value instanceof String) {
                return ((String) value).trim();
            }
            return AppConstants.EMPTY_STRING;
        }
    }

    private final AbstractSelect resourceOption = new NativeSelect();

    private final TextField textField = new TextField();

    private final Router router;

    private final Repositories repositories;

    public PurgeAndExportResourceView(Router router, Repositories repositories) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.repositories = repositories;
    }

    public void init() {
        setMargin(true);
        addImportView();
        addHeader();
        addRuler();
        addDescription();
        addRuler();
        addContent();
    }

    private void addImportView() {
        addComponent(new ImportView(router, repositories.ingest()));
    }

    private void addContent() {
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setMargin(true);
        filterLayout.setSpacing(true);
        addResult();
    }

    private void addResult() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setSpacing(true);

        textField.setWidth("100%");

        createResourceOptions();

        Button filterButton = new Button(ViewConstants.FILTER);
        filterButton.setStyleName(Reindeer.BUTTON_SMALL);
        filterButton.addListener(new FilterButtonListener());

        horizontalLayout.addComponent(resourceOption);
        horizontalLayout.addComponent(createHelpView());
        horizontalLayout.addComponent(textField);
        horizontalLayout.addComponent(filterButton);
        horizontalLayout.setExpandRatio(textField, 1.0f);

        addComponent(horizontalLayout);
    }

    private PopupView createHelpView() {
        final Label popUpContent = new Label(ViewConstants.FILTER_EXAMPLE_TOOLTIP_TEXT, Label.CONTENT_XHTML);
        popUpContent.setWidth(400, UNITS_PIXELS);
        final PopupView popup = new PopupView(ViewConstants.TIP, popUpContent);
        return popup;
    }

    private void createResourceOptions() {
        BeanItemContainer<ResourceType> dataSource =
            new BeanItemContainer<ResourceType>(ResourceType.class, createResourceTypeList());
        dataSource.addNestedContainerProperty("label");
        resourceOption.setContainerDataSource(dataSource);
        resourceOption.setNewItemsAllowed(false);
        resourceOption.setNullSelectionAllowed(false);
        resourceOption.select(ResourceType.ITEM);
        resourceOption.setItemCaptionPropertyId("label");
    }

    private List<ResourceType> createResourceTypeList() {
        final List<ResourceType> list = new ArrayList<ResourceType>();
        list.add(ResourceType.CONTEXT);
        list.add(ResourceType.CONTAINER);
        list.add(ResourceType.ITEM);
        list.add(ResourceType.CONTENT_MODEL);
        return list;
    }

    private void addDescription() {
        addComponent(new Label("<p>" + ViewConstants.FILTER_DESCRIPTION_TEXT + "</p>", Label.CONTENT_XHTML));
    }

    private void addRuler() {
        addComponent(new Style.Ruler());
    }

    private void addHeader() {
        final Label text = new H2(ViewConstants.FILTERING_RESOURCES_TITLE);
        text.setContentMode(Label.CONTENT_XHTML);
        addComponent(text);
    }

    private void showErrorMessage(final Exception e) {
        router
            .getApp().getMainWindow()
            .showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
    }

    private void showErrorMessage(MessagesStatus status) {
        router.getMainWindow().showNotification(
            new Notification("Error", status.getStatusMessage(), Notification.TYPE_ERROR_MESSAGE));
    }
}
