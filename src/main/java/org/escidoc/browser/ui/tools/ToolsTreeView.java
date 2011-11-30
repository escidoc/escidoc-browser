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

import java.net.URISyntaxException;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;

import com.google.common.base.Preconditions;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class ToolsTreeView extends VerticalLayout {

    private static class Node {

        private NODE_TYPE type;

        Node(NODE_TYPE type) {
            this.type = type;
        }

        public NODE_TYPE getType() {
            return type;
        }

        @Override
        public String toString() {
            return type.getLabel();
        }
    }

    enum NODE_TYPE {

        LOAD_EXAMPLE(ViewConstants.LOAD_EXAMPLE), REPO_INFO(ViewConstants.REPOSITORY_INFORMATION), REINDEX(
            ViewConstants.REINDEX), BULK_TASKS(ViewConstants.BULK_TASKS);

        private String label;

        private NODE_TYPE(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final Tree tree = new Tree();

    private Router router;

    private Repositories repositories;

    public ToolsTreeView(Router router, Repositories repositories) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.repositories = repositories;
    }

    public void init() throws UnsupportedOperationException, EscidocClientException, URISyntaxException {
        fillTree();
        addListener();
        addComponent(tree);
    }

    private void addListener() {
        tree.addListener(new ItemClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void itemClick(ItemClickEvent event) {
                switch (getType(event)) {
                    case LOAD_EXAMPLE:
                        router.openTab(new LoadExampleView(router, repositories.admin()), getType(event).getLabel());
                        break;
                    case REPO_INFO:
                        RepositoryInfoView infoView = new RepositoryInfoView(repositories.admin());
                        try {
                            infoView.init();
                            router.openTab(infoView, getType(event).getLabel());
                        }
                        catch (EscidocClientException e) {
                            router.getMainWindow().showNotification(ViewConstants.ERROR, e.getMessage(),
                                Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                        break;
                    case REINDEX:
                        ReindexView view = new ReindexView(router, repositories.admin());
                        view.init();
                        router.openTab(view, getType(event).getLabel());
                        break;
                    case BULK_TASKS:
                        PurgeAndExportResourceView purgeView = new PurgeAndExportResourceView(router, repositories);
                        purgeView.init();
                        router.openTab(purgeView, getType(event).getLabel());
                        break;
                    default:
                        break;
                }
            }

            private NODE_TYPE getType(ItemClickEvent event) {
                return ((Node) event.getItemId()).getType();
            }
        });
    }

    private void fillTree() throws UnsupportedOperationException, EscidocClientException, URISyntaxException {
        if (hasGrantTo(ActionIdConstants.CREATE_ITEM)) {
            tree.addItem(new Node(NODE_TYPE.LOAD_EXAMPLE));
        }

        tree.addItem(new Node(NODE_TYPE.REPO_INFO));

        if (hasGrantTo(ActionIdConstants.REINDEX_ACTION_ID)) {
            tree.addItem(new Node(NODE_TYPE.REINDEX));
        }

        if (hasGrantTo(ActionIdConstants.PURGE_RESOURCES)) {
            tree.addItem(new Node(NODE_TYPE.BULK_TASKS));
        }

        for (Object object : tree.getContainerDataSource().getItemIds()) {
            tree.setChildrenAllowed(object, false);
        }
    }

    private boolean hasGrantTo(String actionId) throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().forCurrentUser().isAction(actionId).forResource(AppConstants.EMPTY_STRING).permitted();
    }
}