package com.example.softconsultingiqt.pub;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

import kotlin.reflect.KFunction;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");
    private TextView texto ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
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
                        Log.d("Entro","entro usu3");
                    }
                    else {
                        Log.d("Entro","entro usu1");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Fallo","fatal error"+e);
                }
            });

            Toast.makeText(getActivity(),"You Signed In successfully",Toast.LENGTH_LONG).show();
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

    public  void  tipoUsu(){
        FirebaseUser user = mAuth.getCurrentUser();
        usersRef.whereEqualTo("email", user.getEmail()).whereEqualTo("tipoUser", 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Log.d("Entro","entro usu3");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Fallo","fatal error"+e);
            }
        });

        //Toast.makeText(getActivity(),"Abeeeeerrr" + usu,Toast.LENGTH_LONG).show();
       // Log.d("Aber", "Abeer"+user);

    }
}