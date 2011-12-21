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
package org.escidoc.browser.elabsmodul.views.listeners;

import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.USER_DESCR_ON_HOR_LAYOUT_TO_SAVE;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.USER_DESCR_ON_LABEL_TO_SAVE;

import java.util.Iterator;

import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.interfaces.ILabsInstrumentAction;
import org.escidoc.browser.elabsmodul.model.FileFormatBean;
import org.escidoc.browser.elabsmodul.views.helpers.LabsLayoutHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class FileFormatSelectionLayoutListener implements LayoutClickListener {

    private static final long serialVersionUID = 3285963424870578840L;

    private static final Logger LOG = LoggerFactory.getLogger(FileFormatSelectionLayoutListener.class);

    private final ILabsInstrumentAction labsInstrumentAction;

    public FileFormatSelectionLayoutListener(final ILabsInstrumentAction labsInstrumentAction) {
        Preconditions.checkNotNull(labsInstrumentAction, "LabsInstrumentAction is null");
        this.labsInstrumentAction = labsInstrumentAction;
    }

    @Override
    public void layoutClick(LayoutClickEvent event) {
        final Component component = event.getComponent();

        if (!(component instanceof HorizontalLayout)) {
            LOG.error("This listener is defined only for horizontalLayout");
            return;
        }

        Component dataComponent = null;
        try {
            if ((dataComponent = ((HorizontalLayout) component).getComponent(1)) == null) {
                return;
            }
            if (dataComponent instanceof Label) {
                BeanItemContainer<FileFormatBean> itemContainer =
                    new BeanItemContainer<FileFormatBean>(FileFormatBean.class);
                for (Iterator<FileFormatBean> iterator = ELabsCache.getFileFormats().iterator(); iterator.hasNext();) {
                    FileFormatBean bean = iterator.next();
                    itemContainer.addItem(bean);
                }

                Component newComponent =
                    LabsLayoutHelper.createDynamicComboBoxFieldForInstrument(this.labsInstrumentAction, null, "title",
                        itemContainer);
                if (newComponent != null) {
                    ((HorizontalLayout) component).replaceComponent(dataComponent, newComponent);
                    ((HorizontalLayout) component).setComponentAlignment(
                        ((HorizontalLayout) component).getComponent(1), Alignment.TOP_RIGHT);
                    ((HorizontalLayout) component).setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_SAVE);
                    ((Label) ((HorizontalLayout) component).getComponent(0))
                        .setDescription(USER_DESCR_ON_LABEL_TO_SAVE);
                }
            }
            else if (dataComponent instanceof ComboBox) {
                if (!event.getChildComponent().equals(dataComponent)) {
                    LabsLayoutHelper.switchToLabelFromEditedField((HorizontalLayout) component);
                }
            }
            else {
                LOG.error("Listener is not bound to this type of UI component: "
                    + dataComponent.getClass().getSimpleName());
            }
        }
        catch (IndexOutOfBoundsException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }
}