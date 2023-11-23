package com.Arjunagi.DoctorApp.services;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GeoServices {
    @Value("${geocoding.api.key}")
    private String API_KEY;


    public List<String> findCity(double latitude, double longitude) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();
        GeocodingResult[] results = GeocodingApi.reverseGeocode(context, new LatLng(latitude, longitude)).await();

        for (GeocodingResult result : results) {
            for (var type : result.types) {
                if (type.equals(AddressType.LOCALITY)) {
                    return Arrays.stream(result.formattedAddress.split(",")).toList();
                }
            }
        }

        return null;
    }
    public  double calculateDistance(double originLat, double originLng, double destinationLat, double destinationLng) {
        try {
            // Create origins and destinations for the Distance Matrix request
            String[] origins = { originLat + "," + originLng };
            String[] destinations = { destinationLat + "," + destinationLng };
            GeoApiContext context = new GeoApiContext.Builder().apiKey(API_KEY).build();
            // Make the Distance Matrix API request
            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context)
                    .origins(origins)
                    .destinations(destinations)
                    .await();

            // Parse the response to get the distance in meters
            DistanceMatrixRow[] rows = distanceMatrix.rows;
            if (rows.length > 0) {
                DistanceMatrixElement[] elements = rows[0].elements;
                if (elements.length > 0) {
                    DistanceMatrixElement element = elements[0];
                    if (element.status == com.google.maps.model.DistanceMatrixElementStatus.OK) {
                        return element.distance.inMeters;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // Error or distance not found
    }
}
