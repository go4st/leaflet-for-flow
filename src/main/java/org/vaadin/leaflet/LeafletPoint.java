package org.vaadin.leaflet;

public class LeafletPoint {

    private int id;

    private double latitude;

    private double longitude;

    private boolean activeMarker;

    public LeafletPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isActiveMarker() {
        return activeMarker;
    }

    public void setActiveMarker(boolean activeMarker) {
        this.activeMarker = activeMarker;
    }
}
