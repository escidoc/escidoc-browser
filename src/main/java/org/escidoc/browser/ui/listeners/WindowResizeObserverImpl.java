package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.ui.WindowDimension;
import org.escidoc.browser.ui.WindowDimensionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WindowResizeObserverImpl implements WindowResizeObserver {

    private static final Logger LOG = LoggerFactory
        .getLogger(WindowResizeObserverImpl.class);

    private WindowDimension windowDimension = new WindowDimensionImpl(0, 0);

    @Override
    public void setDimension(final WindowDimension windowDimension) {
        LOG.debug("Dimension changed: " + windowDimension.toString());
        this.windowDimension = windowDimension;
    }

    @Override
    public WindowDimension getDimension() {
        return windowDimension;
    }
}