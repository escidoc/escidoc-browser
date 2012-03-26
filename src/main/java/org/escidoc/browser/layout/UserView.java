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
package org.escidoc.browser.layout;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
public class UserView extends HorizontalLayout {

    protected static final String NAME_PROPERTY = "Name";

    protected static final String HOURS_PROPERTY = "Hours done";

    protected static final String MODIFIED_PROPERTY = "Last Modified";

    public void init() {
        setSpacing(true);

        // vertically divide the right area
        VerticalLayout left = new VerticalLayout();
        left.setSpacing(true);
        addComponent(left);

        addSearch(left);
        addTreeTable(left);
        addDetailView(left);
    }

    private void addSearch(VerticalLayout left) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(true, true, true, true);
        horizontalLayout.setSpacing(true);

        TextField c = new TextField();
        c.setWidth("400px");
        Button button = new Button("Search");
        button.setStyleName(Reindeer.BUTTON_SMALL);

        horizontalLayout.addComponent(c);
        horizontalLayout.addComponent(button);

        left.addComponent(horizontalLayout);
    }

    TextField name = new TextField("Name: ");

    private void addDetailView(VerticalLayout left) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setSizeFull();
        left.addComponent(cssLayout);

        cssLayout.addComponent(name);
    }

    private void addTreeTable(VerticalLayout left) {
        TreeTable treetable = new TreeTable();
        treetable.setSelectable(true);
        treetable.setWidth("100%");
        treetable.setHeight("50%");
        // Calendar
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 10, 30, 14, 40, 26);

        // Add Table columns
        treetable.addContainerProperty(NAME_PROPERTY, String.class, "");
        treetable.addContainerProperty(HOURS_PROPERTY, Integer.class, 0);
        treetable.addContainerProperty(MODIFIED_PROPERTY, Date.class, cal.getTime());

        // Populate table
        Object allProjects = treetable.addItem(new Object[] { "All Projects", 18, cal.getTime() }, null);
        Object year2010 = treetable.addItem(new Object[] { "Year 2010", 18, cal.getTime() }, null);
        Object customerProject1 = treetable.addItem(new Object[] { "Customer Project 1", 13, cal.getTime() }, null);
        Object customerProject1Implementation =
            treetable.addItem(new Object[] { "Implementation", 5, cal.getTime() }, null);
        Object customerProject1Planning = treetable.addItem(new Object[] { "Planning", 2, cal.getTime() }, null);
        Object customerProject1Prototype = treetable.addItem(new Object[] { "Prototype", 5, cal.getTime() }, null);
        Object customerProject2 = treetable.addItem(new Object[] { "Customer Project 2", 5, cal.getTime() }, null);
        Object customerProject2Planning = treetable.addItem(new Object[] { "Planning", 5, cal.getTime() }, null);

        // Set hierarchy
        treetable.setParent(year2010, allProjects);
        treetable.setParent(customerProject1, year2010);
        treetable.setParent(customerProject1Implementation, customerProject1);
        treetable.setParent(customerProject1Planning, customerProject1);
        treetable.setParent(customerProject1Prototype, customerProject1);
        treetable.setParent(customerProject2, year2010);
        treetable.setParent(customerProject2Planning, customerProject2);

        // Disallow children from leaves
        treetable.setChildrenAllowed(customerProject1Implementation, false);
        treetable.setChildrenAllowed(customerProject1Planning, false);
        treetable.setChildrenAllowed(customerProject1Prototype, false);
        treetable.setChildrenAllowed(customerProject2Planning, false);

        // Expand all
        treetable.setCollapsed(allProjects, false);
        treetable.setCollapsed(year2010, false);
        treetable.setCollapsed(customerProject1, false);
        treetable.setCollapsed(customerProject2, false);
        left.addComponent(treetable);
        setWidth("100%");

        treetable.addListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                name.setValue(event.getItemId());
            }
        });
    }
}