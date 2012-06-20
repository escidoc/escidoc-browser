package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.controller.WikiPageController;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceProxy;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;

public class SaveWikiItemContent implements ClickListener {
    private TextArea txtWikiContent;

    private WikiPageController controller;

    private ItemProxy itemProxy;

    public SaveWikiItemContent(final ResourceProxy resourceProxy, final WikiPageController controller,
        TextArea txtWikiContent) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);
        this.txtWikiContent = txtWikiContent;
        this.controller = controller;
        this.itemProxy = (ItemProxy) resourceProxy;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (txtWikiContent.getValue().toString() != "") {
            controller.createWikiContent("Title", txtWikiContent.getValue().toString());

        }
    }
}
