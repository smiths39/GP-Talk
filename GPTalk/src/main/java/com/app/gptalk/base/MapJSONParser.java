package com.app.gptalk.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class MapJSONParser {

    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();

        JSONArray jsonRoutes = null;
        JSONArray jsonRouteLegs = null;
        JSONArray jsonRouteSteps = null;

        try {
            jsonRoutes = jObject.getJSONArray("routes");

            // Traversing all map routes
            for (int routeIndex = 0; routeIndex < jsonRoutes.length(); routeIndex++) {

                jsonRouteLegs = ((JSONObject) jsonRoutes.get(routeIndex)).getJSONArray("legs");
                List<HashMap<String, String>> mapPath = new ArrayList<HashMap<String, String>>();

                // Traversing all legs
                for (int legIndex = 0; legIndex < jsonRouteLegs.length(); legIndex++) {

                    jsonRouteSteps = ((JSONObject) jsonRouteLegs.get(legIndex)).getJSONArray("steps");

                    // Traversing all steps
                    for (int stepIndex = 0; stepIndex < jsonRouteSteps.length(); stepIndex++) {

                        // Get polyline between two map points
                        String polyline = (String) ((JSONObject) ((JSONObject) jsonRouteSteps.get(stepIndex))
                                                                                             .get("polyline"))
                                                                                             .get("points");
                        List<LatLng> list = decodePoly(polyline);

                        // Traversing all points
                        for (int coordinateIndex = 0; coordinateIndex < list.size(); coordinateIndex++) {

                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("lat", Double.toString(((LatLng) list.get(coordinateIndex)).latitude));
                            hashMap.put("lng", Double.toString(((LatLng) list.get(coordinateIndex)).longitude));
                            mapPath.add(hashMap);
                        }
                    }
                    routes.add(mapPath);
                }
            }
        } catch (JSONException e) {
        } catch (Exception e) {
        }
        return routes;
    }

    /*
     * Method 'decodePoly' courtesy :
     * jeffreysambells.com/2010/05/27
     * decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int length = encoded.length();
        int index = 0, latitude = 0, longitude = 0;

        while (index < length) {

            // Getting latitude calculation
            int base, shift = 0, result = 0;

            do {
                base = encoded.charAt(index++) - 63;
                result |= (base & 0x1f) << shift;
                shift += 5;
            } while (base >= 0x20);

            int destinationLatitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            latitude += destinationLatitude;

            // Reset values for longitude calculation
            shift = 0;
            result = 0;

            do {
                base = encoded.charAt(index++) - 63;
                result |= (base & 0x1f) << shift;
                shift += 5;
            } while (base >= 0x20);

            int destinationLongitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            longitude += destinationLongitude;

            LatLng polyCoordinates = new LatLng((((double) latitude / 1E5)), (((double) longitude / 1E5)));
            poly.add(polyCoordinates);
        }

        return poly;
    }
}