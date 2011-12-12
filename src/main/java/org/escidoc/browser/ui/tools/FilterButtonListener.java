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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.BulkRepository.DeleteResult;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class FilterButtonListener implements ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(FilterButtonListener.class);

    final PurgeAndExportResourceView purgeAndExportResourceView;

    private Window mainWindow;

    public FilterButtonListener(PurgeAndExportResourceView purgeAndExportResourceView, Window mainWindow) {
        Preconditions.checkNotNull(purgeAndExportResourceView, "purgeAndExportResourceView is null: %s",
            purgeAndExportResourceView);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        this.purgeAndExportResourceView = purgeAndExportResourceView;
        this.mainWindow = mainWindow;
    }

    private static final String EXPORT_FILENAME = "escidoc-xml-export.zip";

    @SuppressWarnings("unchecked")
    Set<ResourceModel> getSelectedResources() {
        Object object = resultTable.getValue();
        if (object instanceof Set) {
            return (Set<ResourceModel>) object;
        }
        return Collections.emptySet();
    }

    private static final boolean IS_EXPORT_PERMITTTED = true;

    private VerticalLayout resultLayout = new VerticalLayout();

    private HorizontalLayout buttonLayout = new HorizontalLayout();

    private Table resultTable;

    private BeanItemContainer<ResourceModel> resultDataSource;

    @Override
    public void buttonClick(ClickEvent event) {
        try {
            showResult();
            if (isPurgePermitted()) {
                showPurgeView();
            }
            if (isContentModelSelected() && isExportPermitted()) {
                showExportView();
            }
            showDeleteView();
        }
        catch (EscidocClientException e) {
            LOG.error(e.getMessage());
            this.purgeAndExportResourceView.showErrorMessage(e);
        }
        catch (URISyntaxException e) {
            LOG.error(e.getMessage());
            this.purgeAndExportResourceView.showErrorMessage(e);
        }
    }

    private void showDeleteView() {
        buttonLayout.setSpacing(true);
        Button deleteButton = new Button(ViewConstants.DELETE);
        deleteButton.setStyleName(Reindeer.BUTTON_SMALL);
        buttonLayout.addComponent(deleteButton);
        deleteButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                DeleteResult result =
                    FilterButtonListener.this.purgeAndExportResourceView.repositories.bulkTasks().delete(
                        getSelectedResources());

                for (ResourceModel rM : result.getSuccess()) {
                    LOG.debug("Succesfully delete " + rM);
                    resultDataSource.removeItem(rM);
                }

                Map<ResourceModel, String> map = result.getFail();
                for (ResourceModel rM : map.keySet()) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("fail to delete ");
                    builder.append(rM.getType().getLabel());
                    builder.append(" ");
                    builder.append(rM.getId());
                    builder.append(". Reason: ");
                    builder.append(map.get(rM));
                    String msg = builder.toString();
                    LOG.debug(msg);
                    FilterButtonListener.this.purgeAndExportResourceView.showWarningMessage(msg);
                }
            }
        });
    }

    private boolean isContentModelSelected() {
        return getSelectedType().equals(ResourceType.CONTENT_MODEL);
    }

    private void showExportView() {
        buttonLayout.setSpacing(true);

        Button exportButton = new Button(ViewConstants.EXPORT);
        exportButton.setStyleName(Reindeer.BUTTON_SMALL);
        buttonLayout.addComponent(exportButton);
        exportButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                final Set<ResourceModel> selectedResources = getSelectedResources();
                FilterButtonListener.this.purgeAndExportResourceView.router.getMainWindow().open(
                    new StreamResource(new StreamSource() {

                        @Override
                        public InputStream getStream() {
                            try {
                                return zip(selectedResources);
                            }
                            catch (IOException e) {
                                FilterButtonListener.this.purgeAndExportResourceView.showErrorMessage(e);
                            }
                            catch (EscidocClientException e) {
                                FilterButtonListener.this.purgeAndExportResourceView.showErrorMessage(e);
                            }
                            return null;
                        }

                    }, EXPORT_FILENAME, FilterButtonListener.this.purgeAndExportResourceView.router.getApp()), "_new");
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
                String asString =
                    this.purgeAndExportResourceView.repositories.contentModel().getAsXmlString(resourceModel.getId());
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
        return this.purgeAndExportResourceView.repositories
            .pdp().forCurrentUser().isAction(ActionIdConstants.PURGE_RESOURCES).permitted();
    }

    private void showPurgeView() {
        addHintForSelection();
        addPurgeButton();
    }

    private void addHintForSelection() {
        final Label hintText =
            new Label(
                "<div><em>Hint: </em>"
                    + "To select multiple resources, hold down the CONTROL key while you click on the resource.</br></div>",
                Label.CONTENT_XHTML);
        resultLayout.addComponent(hintText);
    }

    private void addPurgeButton() {
        final Button purgeButton = new Button(ViewConstants.PURGE);
        purgeButton.setStyleName(Reindeer.BUTTON_SMALL);
        buttonLayout.addComponent(purgeButton);
        purgeButton.addListener(new PurgeButtonListener(this, mainWindow, resultDataSource));
    }

    private void showResult() throws EscidocClientException {
        List<ResourceModel> result = getFilterResult();
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
        buttonLayout.removeAllComponents();
        resultLayout.addComponent(buttonLayout);

        this.purgeAndExportResourceView.addComponent(resultLayout);
    }

    private Table createFilterResultView(List<ResourceModel> result) {
        resultDataSource = new BeanItemContainer<ResourceModel>(ResourceModel.class, result);
        resultTable =
            new Table("Found: " + result.size() + " " + result.get(0).getType().getLabel() + "s", resultDataSource);
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
        this.purgeAndExportResourceView.addComponent(resultLayout);
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
                return this.purgeAndExportResourceView.repositories.item();
            case CONTAINER:
                return this.purgeAndExportResourceView.repositories.container();
            case CONTEXT:
                return this.purgeAndExportResourceView.repositories.context();
            case CONTENT_MODEL:
                return this.purgeAndExportResourceView.repositories.contentModel();
            default:
                this.purgeAndExportResourceView.router.getMainWindow().showNotification(
                    getSelectedType() + " not yet supported", Window.Notification.TYPE_WARNING_MESSAGE);
        }
        throw new UnsupportedOperationException(getSelectedType() + " not yet supported");
    }

    private boolean isInputEmpty() {
        return getRawFilter().isEmpty();

    }

    private ResourceType getSelectedType() {
        final Object value = this.purgeAndExportResourceView.resourceOption.getValue();
        if (value instanceof ResourceType) {
            return (ResourceType) value;
        }
        return ResourceType.ITEM;
    }

    private String getRawFilter() {
        final Object value = this.purgeAndExportResourceView.textField.getValue();
        if (value instanceof String) {
            return ((String) value).trim();
        }
        return AppConstants.EMPTY_STRING;
    }
}