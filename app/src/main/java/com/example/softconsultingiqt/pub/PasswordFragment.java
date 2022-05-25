package com.example.softconsultingiqt.pub;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.softconsultingiqt.R;
import com.example.softconsultingiqt.databinding.FragmentPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class PasswordFragment extends Fragment {

    private FragmentPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPasswordBinding.inflate(inflater, container,false);
        binding.btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailAddress = binding.tietEmail.getText().toString();
                if (validar() & validarEmail(emailAddress) == 200) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Exito", "Email sent.");
                                        Toast.makeText(getActivity(), "Correo enviado.",
                                                Toast.LENGTH_SHORT).show();
                                        NavHostFragment.findNavController(
                                                        PasswordFragment.this)
                                                .navigate(R.id.action_PasswordFragment_to_FirstFragment);
                                    } else {
                                        Log.w("No exitoso", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(getActivity(), "Algo salió mal.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                if (validar() & validarEmail(emailAddress) == 300) {
                    Toast.makeText(getActivity(), "Dirección de email incorrecta.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnToLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(
                        PasswordFragment.this)
                        .navigate(R.id.action_PasswordFragment_to_FirstFragment);
            }
        });
        return binding.getRoot();
    }

    public boolean validar()
    {
        boolean ret= true;

        String valorcorreo = binding.tietEmail.getText().toString();
        if(valorcorreo.isEmpty()) {
            binding.tietEmail.setError("Llenar el campo");
            ret= false;
        }
        return ret;
    }

    private int validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (pattern.matcher(email).matches() == true) {
            return 200;
        }
        else
            return 300;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}