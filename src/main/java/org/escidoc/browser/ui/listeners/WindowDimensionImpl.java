package org.escidoc.browser.ui.listeners;

public class WindowDimensionImpl implements WindowDimension {

    private final float width;

    private final float height;

    public WindowDimensionImpl(final float windowWidth, final float windowHeight) {
        width = windowWidth;
        height = windowHeight;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(height);
        result = prime * result + Float.floatToIntBits(width);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WindowDimensionImpl other = (WindowDimensionImpl) obj;
        if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height)) {
            return false;
        }
        if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder
            .append("WindowDimensionImpl [width=").append(width)
            .append(", height=").append(height).append("]");
        return builder.toString();
    }
}