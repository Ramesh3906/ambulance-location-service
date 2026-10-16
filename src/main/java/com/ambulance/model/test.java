package com.ambulance.model;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;

import java.util.List;

public class test{
    public static void dfdfdf() throws Exception {

        H3Core h3 = H3Core.newInstance();

        double patientLat = 13.0827;
        double patientLng = 80.2707;

        int resolution = 9;
        int k = 1;

        // Step 1: Find the patient's H3 cell
        String patientCell = h3.latLngToCellAddress(
                patientLat,
                patientLng,
                resolution
        );

        System.out.println("Patient cell: " + patientCell);

        // Step 2: Find the surrounding cells
        List<String> nearbyCells =
                h3.gridDisk(patientCell, k);

        // Step 3: Print the candidate cells
        System.out.println("Candidate cells:");

        for (String cell : nearbyCells) {
            System.out.println(cell);
        }

        System.out.println(
                "Total candidate cells: " + nearbyCells.size()
        );
    }


    public static void main(String[] args) throws Exception {

        H3Core h3 = H3Core.newInstance();

        double latitude = 13.0827;
        double longitude = 80.2707;
        int resolution = 9;

        // 1. Find the cell containing the GPS point
        String cellId = h3.latLngToCellAddress(
                latitude, longitude, resolution
        );

        System.out.println("H3 Cell ID: " + cellId);

        // 2. Find the cell's center
        LatLng center = h3.cellToLatLng(cellId);

        System.out.println(
                "Cell center: " + center.lat + ", " + center.lng
        );

        // 3. Retrieve the polygon boundary
        List<LatLng> boundary = h3.cellToBoundary(cellId);

        System.out.println("Boundary vertices:");

        for (int i = 0; i < boundary.size(); i++) {
            LatLng point = boundary.get(i);

            System.out.printf(
                    "Vertex %d: latitude=%f, longitude=%f%n",
                    i + 1, point.lat, point.lng
            );
        }
        test.dfdfdf();
    }
}