package com.example.softconsultingiqt.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.softconsultingiqt.R;
import com.example.softconsultingiqt.databinding.FragmentHomeBinding;
import com.google.android.libraries.places.api.Places;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Places.initialize(getActivity(),"AIzaSyAeL253W2VXE-lifAkIFlmPXt4TK_riAiA");
        Fragment fragment = new MapaFragment();

        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout,fragment).commit();

        final TextView textView = binding.textHome;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}