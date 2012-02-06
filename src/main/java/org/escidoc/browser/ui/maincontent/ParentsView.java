package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ParentsView {

    private final OrgUnitProxy orgUnitProxy;

    private Window mainWindow;

    private Router router;

    private OrgUnitController orgUnitController;

    public ParentsView(ResourceProxy resourceProxy, Window mainWindow, Router router,
        OrgUnitController orgUnitController) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(orgUnitController, "orgUnitController is null: %s", orgUnitController);
        this.orgUnitProxy = (OrgUnitProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.router = router;
        this.orgUnitController = orgUnitController;
    }

    public Component asAccord() {
        final Accordion accordion = new Accordion();
        accordion.setSizeFull();
        accordion.addTab(buildParentsList(), ViewConstants.PARENTS, null);
        return accordion;
    }

    @SuppressWarnings("serial")
    private Component buildParentsList() {
        final Panel panel = new Panel();
        panel.setWidth("100%");
        panel.setHeight("100%");

        List<ResourceModel> l = orgUnitProxy.getParentList();
        for (ResourceModel rm : l) {
            final Button button = new Button(rm.getName());
            button.setStyleName(BaseTheme.BUTTON_LINK);
            panel.addComponent(button);
        }

        Button btnAdd = new Button("+/-");
        btnAdd.setStyleName(BaseTheme.BUTTON_LINK);
        panel.addComponent(btnAdd);
        btnAdd.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {

                final Window subwindow = new Window("Manage Organizational Unit Parents");
                subwindow.setModal(true);
                subwindow.setWidth("650px");
                VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                layout.setMargin(true);
                layout.setSpacing(true);

                try {
                    subwindow.addComponent(new OrgUnitParentEditView(orgUnitProxy, orgUnitProxy.getParentList(),
                        router, orgUnitController));
                }
                catch (EscidocClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Button close = new Button("Close", new Button.ClickListener() {

                    @Override
                    public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                        (subwindow.getParent()).removeWindow(subwindow);
                    }
                });
                layout.addComponent(close);
                layout.setComponentAlignment(close, Alignment.TOP_RIGHT);

                mainWindow.addWindow(subwindow);
            }
        });

        return panel;
    }
}