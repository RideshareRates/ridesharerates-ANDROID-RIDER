package com.rideshare.app.models;

import java.util.List;

public class DirectionsResponses {
    public List<Route> routes;

    public static class Route {
        public OverviewPolyline overview_polyline;
        public List<Leg> legs;
    }

    public static class OverviewPolyline {
        public String points;
    }

    public static class Leg {
        public TextValue distance;
        public TextValue duration;
        public Location start_location;
        public Location end_location;
    }

    public static class TextValue {
        public String text;
        public int value;
    }

    public static class Location {
        public double lat;
        public double lng;
    }
}
