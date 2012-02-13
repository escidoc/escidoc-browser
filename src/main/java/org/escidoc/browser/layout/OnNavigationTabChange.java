package org.escidoc.browser.layout;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;
import org.escidoc.browser.ui.orgunit.Reloadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class OnNavigationTabChange implements SelectedTabChangeListener {

    private final static Logger LOG = LoggerFactory.getLogger(OnNavigationTabChange.class);

    @Override
    public void selectedTabChange(final SelectedTabChangeEvent event) {
        Preconditions.checkNotNull(event, "event is null: %s", event);

        final Object source = event.getSource();

        Preconditions.checkNotNull(source, "source is null: %s", source);
        if (!(source instanceof Accordion)) {
            return;
        }

        if (isOrgUniTabSelected(source)) {
            reloadOrgUnitTree(source);
        }
    }

    private static void reloadOrgUnitTree(final Object source) {
        try {
            reloadContent(source);
        }
        catch (final EscidocClientException e) {
            LOG.error("Can not reload data source: " + e.getMessage(), e);
        }
    }

    private static boolean isOrgUniTabSelected(final Object source) {
        return getSelectedTabCaption(source).equalsIgnoreCase(ViewConstants.ORG_UNITS)
            && getTabContent(source) instanceof OrgUnitTreeView;
    }

    private static void reloadContent(final Object source) throws EscidocClientException {
        ((Reloadable) getTabContent(source)).reload();
    }

    private static Component getTabContent(final Object source) {
        return ((Accordion) source).getSelectedTab();
    }

    private static String getSelectedTabCaption(final Object source) {
        return ((Accordion) source).getTab(getTabContent(source)).getCaption();
    }
}