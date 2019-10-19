package com.hyd.jfapps.zkclient.event;

import java.util.ArrayList;
import java.util.List;

public class LocationChangedEvent {

    private List<String> oldLocation;

    private List<String> newLocation;

    public LocationChangedEvent(List<String> oldLocation, List<String> newLocation) {
        this.oldLocation = new ArrayList<>(oldLocation);
        this.newLocation = new ArrayList<>(newLocation);
    }

    public List<String> getOldLocation() {
        return oldLocation;
    }

    public List<String> getNewLocation() {
        return newLocation;
    }
}
