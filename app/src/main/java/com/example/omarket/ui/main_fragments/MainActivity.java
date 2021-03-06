package com.example.omarket.ui.main_fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.omarket.R;
import com.example.omarket.backend.api.APIHandler;
import com.example.omarket.backend.product.Product;
import com.example.omarket.backend.response.Result;
import com.example.omarket.backend.response.ServerCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private NavController navController;
    private NavHostFragment navHostFragment;
    public static ProgressBar progressBar;
    public static MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        progressBar = findViewById(R.id.progressBar2);
        bottomNavigation = findViewById(R.id.nav_host_bottom);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigation,navController);
    }

    public static void loadProducts(ServerCallback<Object> serverCallback){
        MainActivity.progressBar.setVisibility(View.VISIBLE);
        Product.allProducts.clear();
        APIHandler.getAllProductApi(new ServerCallback<ArrayList<Product>>() {
            @Override
            public void onComplete(Result<ArrayList<Product>> result) {
                MainActivity.progressBar.setVisibility(View.INVISIBLE);
                if (result instanceof Result.Success){
                    ArrayList<Product> products = ((Result.Success<ArrayList<Product>>) result).data;
                    Product.allProducts.addAll(products);
                    serverCallback.onComplete(new Result.Success<>("S"));
                } else if (result instanceof Result.Error) {
                    Toast.makeText(mainActivity, "Loading products failed, try again.", Toast.LENGTH_SHORT).show();
                    serverCallback.onComplete(new Result.Error<>("F"));
                }
            }
        }, mainActivity);
    }

//    public void searchUser() {
//        User user = User.currentLoginUser;
//        RepoUser user1 = Repository.getInstance(this).searchUser(new RepositoryCallback<RepoUser>() {
//            @Override
//            public void onComplete(com.example.omarket.backend.data.data.repository.Result<RepoUser> result) {
//
//            }
//        }, user.emailAddress);
//    }

}

