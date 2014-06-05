package com.app.gptalk.homepage.patient;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.app.gptalk.base.MapJSONParser;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.base.HttpConnection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatientGPDirections extends FragmentActivity implements LocationListener {

    private GoogleMap map;
    private Geocoder geocoder;
    private LatLng startPosition, endPosition;

    public String gpAddress, gpName;
    public double startLatitude, startLongitude, endLatitude, endLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gp_directions);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            gpAddress= extras.getString("address");
            gpName= extras.getString("gp_name");
            startLatitude = Double.parseDouble(extras.getString("latitude"));
            startLongitude = Double.parseDouble(extras.getString("longitude"));
        }

        startPosition = new LatLng(startLatitude, startLongitude);

        geocoder = new Geocoder(this);
        getDestinationCoordinates(geocoder);

        SupportMapFragment supportFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gp_direction_map);
        map = supportFragment.getMap();

        setMarkerPositions();

        String url = getURLMapDirections();
        ReadMapPath readPath = new ReadMapPath();
        readPath.execute(url);

        // Move camera to area directed by coordinates
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 13));

        addMarkers();
    }

    private void setMarkerPositions() {

        MarkerOptions options = new MarkerOptions();
        options.position(startPosition);
        options.position(endPosition);
        map.addMarker(options);
    }

    private void addMarkers() {

        if (map != null) {
            map.addMarker(new MarkerOptions().position(startPosition)
                                             .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                             .title("Your are here"));
            map.addMarker(new MarkerOptions().position(endPosition)
                                             .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                             .title(gpName + "'s Office"));
        }
    }

    private void getDestinationCoordinates(Geocoder geocoder) {

        List<Address> locationAddresses;

        try {
            locationAddresses = geocoder.getFromLocationName(gpAddress, 1);

            if (locationAddresses.size() > 0) {

                // Get GP office location based on address
                endLatitude = locationAddresses.get(0).getLatitude();
                endLongitude = locationAddresses.get(0).getLongitude();
            }
        } catch (IOException e) {
         }

        this.endPosition = new LatLng(endLatitude, endLongitude);
    }

    @Override
    public void onLocationChanged(Location location) {

        // Update current location coordinates
        startLatitude = location.getLatitude();
        endLatitude = location.getLongitude();

        startPosition = new LatLng(startLatitude, startLongitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    private String getURLMapDirections() {

        String mapWaypoints = "waypoints=optimize:true|" + startPosition.latitude + "," + startPosition.longitude +
                               "|" + "|" + endPosition.latitude + "," + endPosition.longitude;

        String sensor = "sensor=false";
        String parameters = mapWaypoints + "&" + sensor;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    private class ReadMapPath extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                // Establish url connection
                HttpConnection httpConnection = new HttpConnection();
                data = httpConnection.readUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            new GetPathRoute().execute(result);
        }
    }

    private class GetPathRoute extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jsonObject;
            List<List<HashMap<String, String>>> pathRoutes = null;

            try {
                jsonObject = new JSONObject(jsonData[0]);
                MapJSONParser parser = new MapJSONParser();
                pathRoutes = parser.parse(jsonObject);
            } catch (Exception e) {
            }
            return pathRoutes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {

            ArrayList<LatLng> coordinates = null;
            PolylineOptions polyLineOptions = null;

            // Traverse through path routes
            for (int index_1 = 0; index_1 < routes.size(); index_1++) {

                coordinates = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> completePath = routes.get(index_1);

                for (int index_2 = 0; index_2 < completePath.size(); index_2++) {

                    HashMap<String, String> mapPoint = completePath.get(index_2);

                    double latitudeCoordinate = Double.parseDouble(mapPoint.get("lat"));
                    double longitudeCoordinate = Double.parseDouble(mapPoint.get("lng"));

                    LatLng position = new LatLng(latitudeCoordinate, longitudeCoordinate);

                    coordinates.add(position);
                }

                // Path appearance
                polyLineOptions.addAll(coordinates);
                polyLineOptions.width(10);
                polyLineOptions.color(Color.BLUE);
            }

            // Add path to map
            map.addPolyline(polyLineOptions);
        }
    }
}
