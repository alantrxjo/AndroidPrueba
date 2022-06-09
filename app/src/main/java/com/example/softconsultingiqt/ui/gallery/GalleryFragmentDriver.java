package com.example.softconsultingiqt.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.softconsultingiqt.databinding.FragmentGalleryDriverBinding;

public class GalleryFragmentDriver extends Fragment {


    private  FragmentGalleryDriverBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentGalleryDriverBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGalleryDriver;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}