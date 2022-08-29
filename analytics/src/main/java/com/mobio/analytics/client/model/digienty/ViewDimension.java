package com.mobio.analytics.client.model.digienty;

public class ViewDimension {
    public int height;
    public int width;

    public ViewDimension(int width, int height) {
        this.height = height;
        this.width = width;
    }

    @Override public String toString() {
        return "ViewDimension{" + "height=" + height + ", width=" + width + '}';
    }
}
