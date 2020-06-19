package com.example.goout.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.goout.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import java.util.Calendar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class makePlansActivity extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {

    private static String TAG = "makePlansActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    Marker locationTag;

    //Calendar declarations
    DatePickerDialog picker;
    EditText eText;
    Button btnGet;
    TextView tvw;
    EditText eTextEnd;
    TextView tvwEnd;

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    //Variables
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_plans);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        getLocationPermission();


        /*
         * Implementing the calender functionality
         */

        //Vars for the Start Date Field
        tvw=(TextView)findViewById(R.id.confirmStartDate);
        eText=(EditText) findViewById(R.id.startDateText);
        //vars for the ending of the voting field
        tvwEnd=(TextView)findViewById(R.id.confirmVoteDate);
        eTextEnd = (EditText) findViewById(R.id.endDateText);


        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(makePlansActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        eTextEnd.setInputType(InputType.TYPE_NULL);
        eTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldrEnd = Calendar.getInstance();
                int day = cldrEnd.get(Calendar.DAY_OF_MONTH);
                int month = cldrEnd.get(Calendar.MONTH);
                int year = cldrEnd.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(makePlansActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eTextEnd.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        /*
         * For the spinner that selects the search radius
         */

        Spinner spinner = (Spinner) findViewById(R.id.radiiSelection);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.RadiiSelection, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);


        /*
         * Location Tag Methods
         */
        Button locationTagButton = (Button) findViewById(R.id.placeTags);
        locationTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(makePlansActivity.this, v);
                popup.setOnMenuItemClickListener(makePlansActivity.this);
                popup.inflate(R.menu.location_tag_menu);
                popup.show();
            }
        });

        btnGet=(Button)findViewById(R.id.sendInvite);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvw.setText("Event will begin on: "+ eText.getText());
                tvwEnd.setText("Voting allowed until: "+ eTextEnd.getText());
            }
        });
    }

    private void init(){
        Log.d(TAG,"init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: clicked gps icon");
                getDeviseLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d(TAG,"geoLocate: geolocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(makePlansActivity.this);
        List<Address> list = new ArrayList<>();

        //ToDo: Below, the geocoder is return 1 results. We could, in theory, return many more

        try {
            list = geocoder.getFromLocationName(searchString,1);

        }catch (IOException e){
            Log.e(TAG,"geoLocate: IOException " + e.getMessage() );
        }

        if(list.size() >0){
            Address address = list.get(0);

            Log.d(TAG,"geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

    private void getDeviseLocation() {
        Log.d(TAG, "getDeviceLocation: Getting the device's current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Found location");
                            //ToDo: Initial mark will be placed here. Might not want the pin to re-center to user if they hit the re-center button
                            Location currentLocation = (Location) task.getResult();
                            LatLng pinLoc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            locationTag = mMap.addMarker(new MarkerOptions().position(pinLoc).draggable(true));

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        } else {
                            Log.d(TAG, "onComplete: Current location is null");
                            Toast.makeText(makePlansActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDevicesLocation: Security Exception: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            //Creating a dropped pin
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            //ToDo This adds the marker at the Lat/Long specified
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap() {
        Log.d(TAG, "initMap: Initializing Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(makePlansActivity.this);

    }

    //ToDO: is this method required??
    public Boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(makePlansActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            //all good an the map requests can be made
            Log.d(TAG, "isServicesOK: Google PLay Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but can be resolved
            Log.d(TAG, "isServices OK: an error occured but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(makePlansActivity.this, available, ERROR_DIALOG_REQUEST);
        } else {
            Toast.makeText(this, "Map request cannot be made", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviseLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }


    private void getLocationPermission(){

        Log.d(TAG,"getLocationPermission: Getting location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult: Called");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 ){
                    for (int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG,"onRequestPermissionsResult: Permission failed.");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionsResult: Permission granted.");
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        Log.d(TAG,"hideSoftKeyboard");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.location_tag_menu, popup.getMenu());
        popup.show();
    }

    /**
     * Location Tag Pop-up menu functions
     */

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.location_tag_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Button mButton = (Button) findViewById(R.id.placeTags);
        mButton.setText(item.getTitle());
        mButton.setTextColor(Color.GREEN);

        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.bar:
                Toast.makeText(makePlansActivity.this, "Selected bar", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.nightclub:
                Toast.makeText(makePlansActivity.this, "Selected club", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.restaurant:
                Toast.makeText(makePlansActivity.this, "Selected restaurant", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.spa:
                Toast.makeText(makePlansActivity.this, "Selected spa", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.amusementPark:
                Toast.makeText(makePlansActivity.this, "Selected amusement park", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
    /**
     * Functions handling the radii selection/Spinner
     */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
        Log.d(TAG,"Radii was specified in the spinner");
        Toast.makeText(makePlansActivity.this, "Selected Radii of " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG,"Nothing was selected in the spinner");
    }
}