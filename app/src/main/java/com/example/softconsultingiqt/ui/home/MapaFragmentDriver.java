package com.example.softconsultingiqt.ui.home;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.softconsultingiqt.LocationService;
import com.example.softconsultingiqt.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.internal.BackgroundDetector;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MapaFragmentDriver extends Fragment {

    public FusedLocationProviderClient fusedLocationProviderClient;
    public LocationRequest locationRequest;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference mDatabase;
    private double latituds;
    private double longituds;
    LocationService mLocationService = new LocationService();
    Intent mServiceIntent;
    public static final int LOCATION_REQUEST_CODE = 1001;


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
           if (locationResult == null){
                return;
           }
           for (Location location: locationResult.getLocations()){
               Log.d(TAG,"onlocationResult"+location.toString());
           }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            checkSettingsAndStartLocationUpdates();
        } else {
            PermisoLocalizacion();
        }

    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        stopLocationUpdates();
//    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Places.initialize(getActivity(), "AIzaSyAeL253W2VXE-lifAkIFlmPXt4TK_riAiA");
            // Sacamos permiso de ubicacion del ususario y cordenadas acatuales
            mAuth = FirebaseAuth.getInstance();
            String id = mAuth.getCurrentUser().getUid();
            String nombre = mAuth.getCurrentUser().getDisplayName();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference bdRealtime = database.getReference();
            DatabaseReference operadoresRef = bdRealtime.child("operadores");
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            String key = mDatabase.child("operadores").push().getKey();

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(@NonNull Location location) {
                    System.out.println("DEBUG 1");
                    LatLng miUbicacion = new LatLng(location.getLatitude(),location.getLongitude());
                    //Toast.makeText(getActivity(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
                    //googleMap.addMarker(new MarkerOptions().position(miUbicacion).title("yop"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(miUbicacion)
                            .zoom(15)
                            .bearing(0)
                            .tilt(0)
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    latituds= location.getLatitude();
                    longituds= location.getLongitude();

                    Map<String,Object > UbicacionChofer = new HashMap<>();
                    UbicacionChofer.put("Latitud", latituds);
                    UbicacionChofer.put("Longitud", longituds);
                    UbicacionChofer.put("nombre", nombre);
                    UbicacionChofer.put("ubicacion", 1);
                    db.collection("users")
                            .document(id).update(UbicacionChofer)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Log.d("Guardado exitoso", "Id"+id);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("FireApp","Error",e);
                                }
                            });
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    System.out.println("DEBUG 2");
                    //Toast.makeText(getActivity(), "onProviderEnabled", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    System.out.println("DEBUG 3");
                    Toast.makeText(getActivity(), "onProviderDisabled", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    System.out.println("DEBUG 4");
                    Toast.makeText(getActivity(), "onStatusChanged", Toast.LENGTH_LONG).show();

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0.5f, locationListener);


        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa_driver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_driver);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings of devise are satisfied and we can start location updates
                startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(getActivity(), 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void  PermisoLocalizacion(){
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(TAG,"askLocationPermission:you should show an alert dialog...");
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
            else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }

        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                Log.d(TAG,"askLocationPermission:you should show an alert dialog...");
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},LOCATION_REQUEST_CODE);
            }
            else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},LOCATION_REQUEST_CODE);
            }



        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    TODO FALTA COMPLETAR CODIGO SI NO JALA EN UN FRAGEMENTO https://www.youtube.com/watch?v=4eWoXPSpA5Y
//    }

}