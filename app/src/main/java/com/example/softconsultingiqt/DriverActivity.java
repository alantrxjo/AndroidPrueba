package com.example.softconsultingiqt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.softconsultingiqt.databinding.ActivityDriverBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class DriverActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDriverBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    LocationService mLocationService = new LocationService();
    Intent mServiceIntent;
    private static int MY_FINE_LOCATION_REQUEST = 99;
    private static int MY_BACKGROUND_LOCATION_REQUEST = 100;
    public int servicioact = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarDriver.toolbar);
        binding.appBarDriver.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mLocationService = new LocationService();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (servicioact == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            saveLocation();
                            //Log.d("HILOsact1","hilo hecho");
                        }
                    });
                    try {
                        Thread.sleep(5000);
                        Log.d("HILOsact2","hilo hecho");
                    } catch (InterruptedException e){
                        Log.d("HILOERROR","hilo eroro");
                    }
                }
            }
        }).start();
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_driver, R.id.nav_gallery_driver, R.id.nav_slideshow_driver)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_driver);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {


                    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(DriverActivity.this).create();
                    alertDialog.setTitle("Background permission");
                    alertDialog.setMessage("SE NECESITA LA UBICACION EN SEGUNDO PLANO");

                    alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Start service anyway",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    starServiceFunc();
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Grant background Permission",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    requestBackgroundLocationPermission();
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();


                }else if (ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    starServiceFunc();
                }
            }else{
                starServiceFunc();
            }

        }else if (ActivityCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {


                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(DriverActivity.this).create();
                alertDialog.setTitle("ACCESS_FINE_LOCATION");
                alertDialog.setMessage("Location permission required");

                alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                requestFineLocationPermission();
                                dialog.dismiss();
                            }
                        });


                alertDialog.show();

            } else {
                requestFineLocationPermission();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings_driver) {
            salir();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_driver);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    //Metodo salir para que al salir se muestre una alerta
    public void salir() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(DriverActivity.this);
        alerta.setTitle("Cerrar sesión")
                .setMessage("¿Realmente deseas salir?")
                .setIcon(R.drawable.ic_warning)
                .setNegativeButton("Quedarme", null)
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UbicacionChofer();
                        stopServiceFunc();
                        //Usamos finish para quitar esta actividad
                        //del stack y no regresar sin pasar al login
                        finish();
                        startActivity(
                                new Intent(
                                        DriverActivity.this,
                                        MainActivity.class
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        );
                        FirebaseAuth.getInstance().signOut();
                    }
                })
                .setCancelable(true)
                .show();

    }
    @Override
    public void onBackPressed() {
        salir();
    }

    public  void UbicacionChofer(){
        String idd = mAuth.getCurrentUser().getUid();
        Map<String,Object > UbicacionChofer = new HashMap<>();
        UbicacionChofer.put("ubicacion", 0);
        db.collection("users")
                .document(idd).update(UbicacionChofer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                                    Toast.makeText(getActivity(), "Guardado Exitoso, id: "+id,
//                                            Toast.LENGTH_LONG).show();
                        Log.d("Guardado exitosooo", "Id"+idd);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FireAppppp","Errorrr",e);
                    }
                });

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    private void starServiceFunc(){
        mLocationService = new LocationService();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        if (!Util.isMyServiceRunning(mLocationService.getClass(), this)) {
            startService(mServiceIntent);
            Toast.makeText(this, "El servicio se inicio correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "El servicio ya esta corriendo", Toast.LENGTH_LONG).show();
        }
        servicioact =1;

    }

    private void stopServiceFunc(){
        mLocationService = new LocationService();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        if (Util.isMyServiceRunning(mLocationService.getClass(), this)) {
            stopService(mServiceIntent);
            Toast.makeText(this, "Service stopped!!", Toast.LENGTH_SHORT).show();
            saveLocation();
        } else {
            Toast.makeText(this, "Service is already stopped!!", Toast.LENGTH_SHORT).show();
        }
        servicioact =0;
    }

    private void requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                MY_BACKGROUND_LOCATION_REQUEST);
    }

    private void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, MY_FINE_LOCATION_REQUEST);
    }

    public void saveLocation(){
        String idd = mAuth.getCurrentUser().getUid();
        Double latituds = (LocationService.latituds);
        Double longituds = (LocationService.longituds);
        Map<String,Object > UbicacionChofer = new HashMap<>();
        UbicacionChofer.put("Latitud", latituds);
        UbicacionChofer.put("Longitud", longituds);
        db.collection("users")
                .document(idd).update(UbicacionChofer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.d("Guardado exitosoenhilo", "Id"+idd);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FireApphilo","Errorhilo",e);
                    }
                });


    }

}