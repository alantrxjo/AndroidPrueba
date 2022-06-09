package com.example.softconsultingiqt.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.softconsultingiqt.R;
import com.example.softconsultingiqt.databinding.FragmentHomeBinding;
import com.example.softconsultingiqt.databinding.FragmentHomeDriverBinding;

public class HomeFragmentDriver extends Fragment {


    private FragmentHomeDriverBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeDriverBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHomeDriver;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}