package com.example.softconsultingiqt.pub;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.style.TabStopSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.softconsultingiqt.InicioActivity;
import com.example.softconsultingiqt.R;
import com.example.softconsultingiqt.databinding.FragmentFirstBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;

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
}