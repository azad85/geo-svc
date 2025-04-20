package com.example.geosvc.dto;

public class DistanceResponse {
    private Location location1;
    private Location location2;
    private double distance;
    private final String unit = "km";

    public DistanceResponse() {
    }

    public DistanceResponse(Location location1, Location location2, double distance) {
        this.location1 = location1;
        this.location2 = location2;
        this.distance = distance;
    }

    public Location getLocation1() {
        return location1;
    }

    public void setLocation1(Location location1) {
        this.location1 = location1;
    }

    public Location getLocation2() {
        return location2;
    }

    public void setLocation2(Location location2) {
        this.location2 = location2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getUnit() {
        return unit;
    }

    public static class Location {
        private String postcode;
        private double latitude;
        private double longitude;

        public Location() {
        }

        public Location(String postcode, double latitude, double longitude) {
            this.postcode = postcode;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
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
    }
} 