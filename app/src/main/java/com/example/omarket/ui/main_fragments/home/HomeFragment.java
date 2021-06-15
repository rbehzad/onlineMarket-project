package com.example.omarket.ui.main_fragments.home;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.omarket.R;
import com.example.omarket.ui.main_fragments.Color;
import com.example.omarket.ui.NavigationFragment;

import org.jetbrains.annotations.NotNull;


public class HomeFragment extends NavigationFragment {

    public HomeFragment() {
        super();
    }

    @Override
    public void changeIconColorTo(Color color) {

    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveCommand.execute();
    }
}