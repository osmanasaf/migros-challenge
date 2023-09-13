package com.example.migros.util;

public class LocationUtil {

    private static final double EARTH_RADIUS = 6371000;

    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance;
    }

    public static boolean isWithinProximity(double lat1, double lng1, double lat2, double lng2, double radius) {
        double distance = calculateDistance(lat1, lng1, lat2, lng2);
        return distance <= radius;
    }
}
