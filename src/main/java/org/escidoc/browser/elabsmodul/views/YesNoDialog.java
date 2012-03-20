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
package org.escidoc.browser.elabsmodul.views;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * Confirmation dialog's modal window.
 */
// TODO bind this window with the save functionality of the controller classes
public class YesNoDialog extends Window implements Button.ClickListener {

    private static final long serialVersionUID = 2444046110793999051L;

    private Callback callback;

    private Button yes = new Button("Yes", this);

    private Button no = new Button("No", this);

    public YesNoDialog(String caption, String question, Callback callback) {
        super(caption);

        setModal(true);

        this.callback = callback;

        if (question != null) {
            addComponent(new Label(question));
        }

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        // layout.setMargin(true);
        layout.addComponent(no);
        layout.addComponent(yes);
        addComponent(layout);
        setWidth("300px");
        setHeight("160px");
        setClosable(false);
        setResizable(false);
        setScrollable(false);
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        if (getParent() != null) {
            getParent().removeWindow(this);
        }
        callback.onDialogResult(event.getSource() == yes);
    }

    public interface Callback {
        public void onDialogResult(boolean resultIsYes);
    }
}
