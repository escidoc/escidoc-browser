package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.ui.WindowDimensionImpl;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.ResizeEvent;

@SuppressWarnings("serial")
public final class WindowResizeListener implements Window.ResizeListener {

    private final WindowResizeObserver observer;

    public WindowResizeListener(final WindowResizeObserver observer) {
        this.observer = observer;
    }

    @Override
    public void windowResized(final ResizeEvent e) {
        final float windowWidth = e.getWindow().getWidth();
        final float windowHeight = e.getWindow().getHeight();
        observer.setDimension(new WindowDimensionImpl(windowWidth, windowHeight));
    }
}