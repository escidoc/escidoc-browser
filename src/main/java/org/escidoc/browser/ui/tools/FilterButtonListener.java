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

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class FilterButtonListener implements ClickListener {

    final BulkTasksView bulkTasksView;

    private static final Logger LOG = LoggerFactory.getLogger(FilterButtonListener.class);

    private static final boolean IS_EXPORT_PERMITTTED = true;

    private final VerticalLayout resultLayout = new VerticalLayout();

    private final HorizontalLayout buttonLayout = new HorizontalLayout();

    private final Window mainWindow;

    private final Repositories repositories;

    private BeanItemContainer<ResourceModel> resultDataSource;

    private Table resultTable;

    public FilterButtonListener(final BulkTasksView bulkTasksView, final Window mainWindow,
        final Repositories repositories) {
        Preconditions.checkNotNull(bulkTasksView, "purgeAndExportResourceView is null: %s", bulkTasksView);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.bulkTasksView = bulkTasksView;
        this.mainWindow = mainWindow;
        this.repositories = repositories;
    }

    private static final String EXPORT_FILENAME = "escidoc-xml-export.zip";

    @SuppressWarnings("unchecked")
    Set<ResourceModel> getSelectedResources() {
        final Object object = resultTable.getValue();
        if (object instanceof Set) {
            return (Set<ResourceModel>) object;
        }
        return Collections.emptySet();
    }

    @Override
    public void buttonClick(final ClickEvent event) {
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
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            this.bulkTasksView.showErrorMessage(e);
        }
        catch (final URISyntaxException e) {
            LOG.error(e.getMessage());
            this.bulkTasksView.showErrorMessage(e);
        }
    }

    private void showDeleteView() {
        buttonLayout.setSpacing(true);
        final Button deleteButton = new Button(ViewConstants.DELETE);
        deleteButton.setStyleName(Reindeer.BUTTON_SMALL);
        buttonLayout.addComponent(deleteButton);

        deleteButton.addListener(new BulkDeleteListener(this, repositories, mainWindow, resultDataSource));
    }

    private boolean isContentModelSelected() {
        return getSelectedType().equals(ResourceType.CONTENT_MODEL);
    }

    private void showExportView() {
        buttonLayout.setSpacing(true);

        final Button exportButton = new Button(ViewConstants.EXPORT);
        exportButton.setStyleName(Reindeer.BUTTON_SMALL);
        buttonLayout.addComponent(exportButton);
        exportButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                final Set<ResourceModel> selectedResources = getSelectedResources();
                mainWindow.open(new StreamResource(new StreamSource() {

                    @Override
                    public InputStream getStream() {
                        try {
                            return zip(selectedResources);
                        }
                        catch (final IOException e) {
                            FilterButtonListener.this.bulkTasksView.showErrorMessage(e);
                        }
                        catch (final EscidocClientException e) {
                            FilterButtonListener.this.bulkTasksView.showErrorMessage(e);
                        }
                        return null;
                    }

                }, EXPORT_FILENAME, FilterButtonListener.this.bulkTasksView.router.getApp()), "_blank");
            }
        });
    }

    private ByteArrayInputStream zip(final Set<ResourceModel> set) throws IOException, EscidocClientException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Closeable res = out;
        try {
            final ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            for (final ResourceModel resourceModel : set) {
                zout.putNextEntry(new ZipEntry(resourceModel.getId()));
                final String asString =
                    this.bulkTasksView.repositories.contentModel().getAsXmlString(resourceModel.getId());
                final InputStream is = new ByteArrayInputStream(asString.getBytes("UTF-8"));
                Utils.copy(is, zout);
                zout.closeEntry();
            }
        }
        finally {
            res.close();
        }
        return new ByteArrayInputStream(out.toByteArray());

    }

    private static boolean isExportPermitted() {
        return IS_EXPORT_PERMITTTED;
    }

    private boolean isPurgePermitted() throws EscidocClientException, URISyntaxException {
        return this.bulkTasksView.repositories
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
        final List<ResourceModel> result = getFilterResult();
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

    private void showFilterResultView(final Table table) {
        resultLayout.addComponent(table);
        buttonLayout.removeAllComponents();
        resultLayout.addComponent(buttonLayout);

        this.bulkTasksView.addComponent(resultLayout);
    }

    private Table createFilterResultView(final List<ResourceModel> result) {
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
        this.bulkTasksView.addComponent(resultLayout);
    }

    private static boolean isEmpty(final List<ResourceModel> result) {
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
            case ORG_UNIT:
                return repositories.organization();
            default:
                this.bulkTasksView.router.getMainWindow().showNotification(getSelectedType() + " not yet supported",
                    Window.Notification.TYPE_WARNING_MESSAGE);
        }
        throw new UnsupportedOperationException(getSelectedType() + " not yet supported");
    }

    private boolean isInputEmpty() {
        return getRawFilter().isEmpty();

    }

    private ResourceType getSelectedType() {
        return bulkTasksView.getSelectedTask();
    }

    private String getRawFilter() {
        final Object value = this.bulkTasksView.textField.getValue();
        if (value instanceof String) {
            return ((String) value).trim();
        }
        return AppConstants.EMPTY_STRING;
    }
}