package com.example.softconsultingiqt.pub;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.style.TabStopSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.softconsultingiqt.DriverActivity;
import com.example.softconsultingiqt.InicioActivity;
import com.example.softconsultingiqt.R;
import com.example.softconsultingiqt.databinding.FragmentFirstBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.ViewSnapshot;

import java.util.HashMap;
import java.util.Map;

import kotlin.reflect.KFunction;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String[] PERMISOS;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");
    public static final int LOCATION_REQUEST_CODE=1001;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PERMISOS = new String[]{
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };


    }

    private boolean permisos(Context context,String... PERMISOS){
        if(context != null && PERMISOS != null){
            for (String permiso: PERMISOS){
                if(ActivityCompat.checkSelfPermission(context,permiso)== PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
            return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

       // getLocalizacionpp();
        PermisoLocalizacion();
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = binding.tietUsuario.getText().toString();
                final String password = binding.tietPassword.getText().toString();
                if(validar())
                {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Éxito", "signInWithEmail<:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("No exitoso", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(getActivity(), "Correo o contraseña incorrecta.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(getActivity(),"Faltan datos",Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.btnToRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(
                                FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        binding.btnToPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(
                        FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_PasswordFragment);
            }
        });

        return binding.getRoot();
    }

    public void updateUI(FirebaseUser account){
        if(account != null){
            FirebaseUser user = mAuth.getCurrentUser();
            usersRef.whereEqualTo("email", user.getEmail()).whereEqualTo("tipoUser", 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.isEmpty()){
                        String idd = mAuth.getCurrentUser().getUid();
                        Map<String,Object > UbicacionChofer = new HashMap<>();
                        UbicacionChofer.put("ubicacion", 1);
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
                        startActivity(
                                new Intent(
                                        getActivity(),
                                        DriverActivity.class
                                )
                        );
                    }
                    else {
                        startActivity(
                                new Intent(
                                        getActivity(),
                                        InicioActivity.class
                                )
                        );
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Fallo","fatal error"+e);
                }
            });

            Toast.makeText(getActivity(),"You Signed In successfully",Toast.LENGTH_LONG).show();

        }else {
            Toast.makeText(getActivity(),"You Didnt signed in",Toast.LENGTH_LONG).show();
        }
    }

    public boolean validar()
    {
        boolean ret= true;

        String valorcorreo = binding.tietUsuario.getText().toString();
        String valorpass = binding.tietPassword.getText().toString();
        if(valorcorreo.isEmpty())
        {
            binding.tietUsuario.setError("Llenar el campo");
            ret= false;
        }
        if(valorpass.isEmpty() )
        {
            binding.tietPassword.setError("Llenar el campo");
            ret= false;
        }
        return ret;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    private void getLocalizacionpp(){
//        int permiso = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
//        if (permiso == PackageManager.PERMISSION_DENIED){
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
//
//            }else {
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//            }
//        }
//    }

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

//    public  void  tipoUsu(){
//        FirebaseUser user = mAuth.getCurrentUser();
//        usersRef.whereEqualTo("email", user.getEmail()).whereEqualTo("tipoUser", 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if(queryDocumentSnapshots.isEmpty()){
//                    Log.d("Entro","entro usu3");
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("Fallo","fatal error"+e);
//            }
//        });
//
//        //Toast.makeText(getActivity(),"Abeeeeerrr" + usu,Toast.LENGTH_LONG).show();
//       // Log.d("Aber", "Abeer"+user);
//
//    }
}