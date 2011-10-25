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
package org.escidoc.browser.ui.administration;

import java.net.URISyntaxException;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.repository.AdminRepository;
import org.escidoc.browser.repository.PdpRepository;
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
public class AdministrationTreeView extends VerticalLayout {

    private class Node {

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
            ViewConstants.REINDEX);

        private String label;

        private NODE_TYPE(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private Tree tree = new Tree();

    private Router router;

    private AdminRepository adminRepository;

    private PdpRepository pdpRepository;

    public AdministrationTreeView(Router router, AdminRepository adminRepository, PdpRepository pdpRepository) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(adminRepository, "adminRepository is null: %s", adminRepository);
        Preconditions.checkNotNull(pdpRepository, "pdpRepository is null: %s", pdpRepository);
        this.router = router;
        this.adminRepository = adminRepository;
        this.pdpRepository = pdpRepository;
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
                NODE_TYPE type = ((Node) event.getItemId()).getType();
                switch (type) {
                    case LOAD_EXAMPLE:
                        router.openTab(new LoadExampleView(router, adminRepository), type.getLabel());
                        break;
                    case REPO_INFO:
                        RepositoryInfoView infoView = new RepositoryInfoView(adminRepository);
                        try {
                            infoView.init();
                            router.openTab(infoView, type.getLabel());
                        }
                        catch (EscidocClientException e) {
                            router.getMainWindow().showNotification("Error", e.getMessage(),
                                Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                        break;
                    case REINDEX:
                        ReindexView view = new ReindexView(router, adminRepository);
                        view.init();
                        router.openTab(view, type.getLabel());
                        break;
                    default:
                        break;
                }
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

        for (Object object : tree.getContainerDataSource().getItemIds()) {
            tree.setChildrenAllowed(object, false);
        }
    }

    private boolean hasGrantTo(String actionId) throws EscidocClientException, URISyntaxException {
        return pdpRepository.forCurrentUser().isAction(actionId).forResource(AppConstants.EMPTY_STRING).permitted();
    }
}