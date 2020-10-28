package com.hyd.jfapps.zkclient.event;

import java.util.ArrayList;
import java.util.List;

public class NavigationEvents {

    public static class LocationChangedEvent {

        private final List<String> oldLocation;

        private final List<String> newLocation;

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
}
