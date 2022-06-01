package com.example.softconsultingiqt.pub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.softconsultingiqt.InicioActivity;
import com.example.softconsultingiqt.R;
import com.example.softconsultingiqt.databinding.FragmentSecondBinding;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nombre = binding.tietNombre.getText().toString();
                final String password = binding.tietPass.getText().toString();
                final String email = binding.tietCorreo.getText().toString();
                final Integer tipoUser = 1;

                if (validar() && validarEmail(email) == 200) {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Exito: ", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(nombre)
                                                .build();
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("Nombre insertado", "User profile updated.");
                                                            Log.d("Uusario", user.getDisplayName());
                                                        }
                                                    }
                                                });
                                        Map<String,Object > Cliente = new HashMap<>();
                                        Cliente.put("nombre", nombre);
                                        Cliente.put("email", email);
                                        Cliente.put("tipoUser", tipoUser);

                                        String id = mAuth.getCurrentUser().getUid();

                                        db.collection("users")
                                                .document(id).set(Cliente)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getActivity(), "Guardado Exitoso, id: "+id,
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("FireApp","Error",e);
                                                    }
                                                });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Algo salió mal: ", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getActivity(), "Correo ya registrado.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
                if (validarEmail(email) == 300 & validar()) {
                    Toast.makeText(getActivity(), "Forma de correo incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(
                        SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        return binding.getRoot();
    }

    public void updateUI(FirebaseUser account){
        if(account != null){

            //Toast.makeText(getActivity(),"You Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(
                    new Intent(
                            getActivity(),
                            InicioActivity.class
                    )
            );

        }else {
            Toast.makeText(getActivity(),"You Didnt signed in",Toast.LENGTH_LONG).show();
        }
    }

    private int validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (pattern.matcher(email).matches() == true) {
            return 200;
        }
        else
            return 300;
    }

    public boolean validar()
    {
        boolean ret= true;

        String valorNombre = binding.tietNombre.getText().toString();
        String valorCorreo = binding.tietCorreo.getText().toString();
        String valorPass = binding.tietPass.getText().toString();

        if(valorNombre.isEmpty()) {
            binding.tietNombre.setError("Llenar el campo");
            ret= false;
        }
        if(valorCorreo.isEmpty()) {
            binding.tietCorreo.setError("Llenar el campo");
            ret= false;
        }
        if(valorPass.isEmpty()) {
            binding.tietPass.setError("Llenar el campo");
            ret= false;
        }
        return ret;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}