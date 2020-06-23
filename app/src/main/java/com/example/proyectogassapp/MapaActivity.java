package com.example.proyectogassapp;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private GoogleMap nMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private LocationCallback locationCallback;
    private MaterialSearchBar materialSearchBar;
    private List<AutocompletePrediction> predictionList;
    private PlacesClient placesClient;
    private View vista;
    Button btnFind;
    DrawerLayout drawerLayout;
    private RippleBackground rippleBackground;


    private final float Zoom = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        drawerLayout = findViewById(R.id.drawerLayout);
        materialSearchBar = findViewById(R.id.buscador);
        btnFind = findViewById(R.id.find);
        rippleBackground = findViewById(R.id.round);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        vista = mapFragment.getView();

        String apikey = "AIzaSyA4Auj1gdD-dTLkkKZsKLk52GIZACcrww0";

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapaActivity.this);
        Places.initialize(MapaActivity.this,apikey);
        placesClient = Places.createClient(MapaActivity.this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION){
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("co")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                        if (predictionsResponse != null){
                            predictionList = predictionsResponse.getAutocompletePredictions();
                            List<String>suggestionList = new ArrayList<>();
                            for (int i = 0; i<predictionList.size(); i++){
                                AutocompletePrediction prediction = predictionList.get(i);
                                suggestionList.add(prediction.getFullText(null).toString());
                            }
                            materialSearchBar.updateLastSuggestions(suggestionList);
                            if (!materialSearchBar.isSuggestionsVisible()){
                                materialSearchBar.showSuggestionsList();
                            }
                        }
                    }else {
                        Log.i("MyTag","Error");
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position>=predictionList.size()){
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                },1000);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
                    Place place = fetchPlaceResponse.getPlace();
                    Log.i("MyTag","Lugar encontrado: "+place.getName());
                    LatLng latLngOfPlace = place.getLatLng();
                    if (latLngOfPlace!=null){
                        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, Zoom));
                    }
                }).addOnFailureListener(e -> {
                    if (e instanceof ApiException){
                        ApiException apiException = (ApiException) e;
                        apiException.printStackTrace();
                        int statusCode = apiException.getStatusCode();
                        Log.i("MyTag","Lugar no encontrado: "+ e.getMessage());
                        Log.i("MyTag","Código estatus: "+ statusCode);
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
        btnFind.setOnClickListener(v -> {
            LatLng currentMarkerLocation = nMap.getCameraPosition().target;
            rippleBackground.startRippleAnimation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rippleBackground.stopRippleAnimation();
                }
            },3000);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMyLocationEnabled(true);
        nMap.getUiSettings().setMyLocationButtonEnabled(true);

        LatLng Zeus = new LatLng(6.3449861749091925,-75.56434269994497);
        nMap.addMarker(new MarkerOptions().position(Zeus).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));

        LatLng Gazzel = new LatLng(6.3093191507894035,-75.55969074368477);
        nMap.addMarker(new MarkerOptions().position(Gazzel).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));

        LatLng Esso = new LatLng(6.315202231514889,-75.55741522461174);
        nMap.addMarker(new MarkerOptions().position(Esso).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_station)));

        nMap.setOnMapClickListener(this);

        if (vista != null && vista.findViewById(Integer.parseInt("1"))!=null){
            View locationButton = ((View) vista.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,40,180);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(MapaActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapaActivity.this, locationSettingsResponse -> DeviceLocation());

        task.addOnFailureListener(MapaActivity.this, e -> {
            if (e instanceof ResolvableApiException){
                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(MapaActivity.this, 13);
                }catch (IntentSender.SendIntentException ex){
                    ex.printStackTrace();
                }
            }
        });
        nMap.setOnMyLocationButtonClickListener(() -> {
            if (materialSearchBar.isSuggestionsVisible()){
                materialSearchBar.clearSuggestions();
            }
            if (materialSearchBar.isSearchOpened()){
                materialSearchBar.closeSearch();
                materialSearchBar.setText("");
            }

            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13){
            if (resultCode == RESULT_OK){
                DeviceLocation();
            }
        }
    }

    private void DeviceLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()){
                            lastLocation = task.getResult();
                            if (lastLocation!=null){
                                nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), Zoom));
                            }else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null){
                                            return;
                                        }
                                        lastLocation = locationResult.getLastLocation();
                                        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()), Zoom));
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }else {
                            Toast.makeText(MapaActivity.this, "Ubicación desconocida", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        materialSearchBar.setText(""+latLng);
    }
}
