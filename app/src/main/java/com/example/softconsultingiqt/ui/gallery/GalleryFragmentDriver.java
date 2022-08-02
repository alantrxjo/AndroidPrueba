package com.example.softconsultingiqt.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.softconsultingiqt.R;
import com.example.softconsultingiqt.databinding.FragmentGalleryDriverBinding;
import com.example.softconsultingiqt.pub.SecondFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GalleryFragmentDriver extends Fragment {

    private  FragmentGalleryDriverBinding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentGalleryDriverBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        binding.btnDesactivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });

        binding.btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}