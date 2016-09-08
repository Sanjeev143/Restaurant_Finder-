package com.anorakstech.sanjeev.restauranttrace;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import info.hoang8f.widget.FButton;

public class MainActivity extends FragmentActivity implements LocationListener {

    GoogleMap mGoogleMap;

    private String TAG = "RestaurantMainAct";
    String mPlaceTypeName = "restaurant";

    double mLatitude = 0;
    double mLongitude = 0;

    HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private FButton btnFind;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            showAlertDialog(MainActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }

        // Getting reference to Find Button
        btnFind = (FButton) findViewById(R.id.btn_find);


        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());


        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment
            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Google Map
            mGoogleMap = fragment.getMap();


            // Enabling MyLocation in Google Map
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);




            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);
            Log.v(TAG, provider);
            //  mGoogleMap.addMarker(new MarkerOptions().position(location).title("Marker in Sydney"));
            if (location != null) {
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(provider, 2000, 1, this);

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker arg0) {
//                    Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
//                    String reference = mMarkerPlaceLink.get(arg0.getId());
//                    intent.putExtra("reference", reference);
                    // Create a Uri from an intent string. Use the result to create an Intent.

                    String nameofplace = "";
                    nameofplace = arg0.getTitle().toString();
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=restaurants"+nameofplace);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");



                    // Starting the Place Details Activity
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                    Log.v(TAG, "PlaceDetailsActivity Calling");
                }
            });


            // Setting click event lister for the find button
            btnFind.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    btnFind.setFButtonPadding(20,5,20,5);
                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    sb.append("location=" + mLatitude + "," + mLongitude);
                    sb.append("&radius=5000");
                    sb.append("&types=" + mPlaceTypeName);
                    sb.append("&sensor=true");
                    sb.append("&key="Enter your Google Places API for Android");
                    Log.v(TAG, sb.toString());

                    // Creating a new non-ui thread task to download Google place json data
                    PlacesTask placesTask = new PlacesTask();

                    // Invokes the "doInBackground()" method of the class PlaceTask
                    placesTask.execute(sb.toString());
                    Log.v(TAG, "" + ":" + sb + mPlaceTypeName);
                }
            });
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

//        Uri xyz = Uri.parse("geo:32.8,74.7");
//        Intent mapIntent  = new Intent(Intent.ACTION_MEDIA_BUTTON,xyz);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        if(mapIntent.resolveActivity(getPackageManager())!=null)
//        {
//            startActivity(mapIntent);
//        }
    }

    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        //   alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.v(TAG, e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.anorakstech.sanjeev.restauranttrace/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }



    /**
     * A class, to download Google Places
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
                Log.v(TAG, data.toString());
            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
            Log.v(TAG, result.toString());
        }

    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
            Log.v(TAG, "PlaceJSONParser calling");

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);
                Log.v(TAG, places.toString());

            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            // Clears all the existing markers
            mGoogleMap.clear();
            Log.v(TAG, list.toString());
            for (int i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                String name = hmPlace.get("place_name");
                String vicinity = hmPlace.get("vicinity");
                String rating = hmPlace.get("rating");
                LatLng latLng = new LatLng(lat, lng);
                if(rating.contains("0"))
                {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.star0));
                }else if(rating.contains("1"))
                {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.star1));
                }else if(rating.contains("2"))
                {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.star2));
                }
                else if(rating.contains("3"))
                {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.star3));
                }else if(rating.contains("4"))
                {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.star4));
                }else if(rating.contains("5"))
                {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.star5));
                }




                markerOptions.position(latLng);
                markerOptions.title(name + " : " + vicinity);

                // Placing a marker on the touched position
                Marker m = mGoogleMap.addMarker(markerOptions);
                Log.v(TAG, m.toString());

                // Linking Marker id and place reference
                mMarkerPlaceLink.put(m.getId(), hmPlace.get("reference"));
                Log.v(TAG, hmPlace.get("place_name").toString());

            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}
