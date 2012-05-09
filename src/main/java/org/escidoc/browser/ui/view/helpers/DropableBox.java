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
package org.escidoc.browser.ui.view.helpers;

import com.google.common.base.Preconditions;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ResourceDisplay;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.Resource;

public class DropableBox extends DragAndDropWrapper implements DropHandler {

    static final String SEVERAL_ITEMS = "Several Items";

    static final String ONE_ITEM = "One Item";

    private final static Logger LOG = LoggerFactory.getLogger(DropableBox.class);

    private static final long FILE_SIZE_LIMIT = 200 * 1024 * 1024; // 200MB

    private final CssLayout pane;

    private final Repositories repositories;

    private Window mainWindow;

    public DropableBox(Window mainWindow, CssLayout dropPane, Repositories repositories) {
        super(dropPane);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(dropPane, "dropPane is null: %s", dropPane);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.mainWindow = mainWindow;
        this.pane = dropPane;
        this.repositories = repositories;
        setDropHandler(this);
    }

    @Override
    public void drop(DragAndDropEvent dropEvent) {
        Html5File[] files = getDropFiles(dropEvent);
        if (files == null) {
            onTextDrop(dropEvent);
        }
        else {
            try {
                onFilesDrop(files);
            }
            catch (EscidocException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (InternalClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (TransportException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static Html5File[] getDropFiles(DragAndDropEvent dropEvent) {
        return ((WrapperTransferable) dropEvent.getTransferable()).getFiles();
    }

    private static void onTextDrop(DragAndDropEvent dropEvent) {
        String text = getText(dropEvent);
        if (text == null) {
            return;
        }
        LOG.debug("Text dropped: " + text);
    }

    private static String getText(DragAndDropEvent dropEvent) {
        String text = ((WrapperTransferable) dropEvent.getTransferable()).getText();
        return text;
    }

    private void onFilesDrop(Html5File[] files) throws EscidocException, InternalClientException, TransportException {
        LOG.debug("#files: " + files.length);

        Window modalWindow = buildModalWindow();
        showModalWindow(modalWindow);

        // TODO refactor to a method
        VerticalLayout layout = (VerticalLayout) modalWindow.getContent();
        FormLayout formLayout = new FormLayout();
        layout.addComponent(formLayout);

        addNameField(formLayout);
        addContentModelSelect(formLayout);

        for (final Html5File dropFile : files) {
            if (dropFile.getFileSize() > FILE_SIZE_LIMIT) {
                getWindow().showNotification("File rejected. Max 200Mb files are accepted by Sampler",
                    Window.Notification.TYPE_WARNING_MESSAGE);
            }
            else {
                // TODO make upload async
                dropFile.setStreamVariable(new StreamHandlerImpl(new ByteArrayOutputStream(), dropFile,
                    getApplication(), getWindow(), layout, repositories));
            }
        }

        // TODO refactor to a method
        // if (numberOfFiles > 1) {
        // OptionGroup og = buildOptionGroup(numberOfFiles);
        // layout.addComponent(og);
        // layout.addComponent(buildCreateButton(og));
        // }
    }

    private static void addNameField(FormLayout formLayout) {
        TextField nameField = new TextField(ViewConstants.ITEM_NAME);
        nameField.setRequired(true);
        nameField.setWidth("400px");
        nameField.setRequiredError(ViewConstants.PLEASE_ENTER_AN_ITEM_NAME);
        nameField
            .addValidator(new StringLengthValidator(ViewConstants.ITEM_NAME_MUST_BE_3_25_CHARACTERS, 3, 25, false));
        nameField.setImmediate(true);
        formLayout.addComponent(nameField);
    }

    private void addContentModelSelect(FormLayout formLayout) throws EscidocException, InternalClientException,
        TransportException {
        NativeSelect contentModelSelect = new NativeSelect(ViewConstants.PLEASE_SELECT_CONTENT_MODEL);
        contentModelSelect.setRequired(true);
        bindData(contentModelSelect);
        formLayout.addComponent(contentModelSelect);
    }

    private void bindData(AbstractSelect contentModelSelect) throws EscidocException, InternalClientException,
        TransportException {
        final Collection<? extends Resource> contentModelList =
            repositories.contentModel().findPublicOrReleasedResources();
        final List<ResourceDisplay> resourceDisplayList = new ArrayList<ResourceDisplay>(contentModelList.size());
        for (final Resource resource : contentModelList) {
            resourceDisplayList.add(new ResourceDisplay(resource.getObjid(), resource.getXLinkTitle() + " ("
                + resource.getObjid() + ")"));
        }
        final BeanItemContainer<ResourceDisplay> resourceDisplayContainer =
            new BeanItemContainer<ResourceDisplay>(ResourceDisplay.class, resourceDisplayList);
        resourceDisplayContainer.addNestedContainerProperty("objectId");
        resourceDisplayContainer.addNestedContainerProperty("title");
        contentModelSelect.setContainerDataSource(resourceDisplayContainer);
        contentModelSelect.setItemCaptionPropertyId("title");
    }

    private void showModalWindow(Window modalWindow) {
        mainWindow.addWindow(modalWindow);
    }

    private static Window buildModalWindow() {
        Window modalWindow = new Window("Creating item(s");
        modalWindow.setModal(true);

        VerticalLayout layout = (VerticalLayout) modalWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("600px");

        return modalWindow;
    }

    private Button buildCreateButton(final OptionGroup og) {
        Button createButton = new Button("OK");
        createButton.setStyleName("small");

        String name = null;
        String contextId = null;
        String contentModelId = null;
        ResourceModel parent = null;
        ItemBuilderHelper itemHelper = new ItemBuilderHelper(name, contextId, contentModelId, parent);
        createButton.addListener(new OnCreateItemWithComponents(og, repositories, itemHelper));
        return createButton;
    }

    @SuppressWarnings("serial")
    private static OptionGroup buildOptionGroup(int numberOfFiles) {
        final OptionGroup og =
            new OptionGroup("Creating... " + numberOfFiles + " items.", Arrays.asList(new String[] { ONE_ITEM,
                SEVERAL_ITEMS }));
        og.setImmediate(true);
        og.select(ONE_ITEM);
        og.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.debug("Selected: " + og.getValue());
            }
        });
        return og;
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }
}