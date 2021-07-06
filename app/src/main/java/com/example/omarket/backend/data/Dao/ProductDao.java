package com.example.omarket.backend.data.Dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.omarket.backend.data.entities.Product;

import java.util.List;
import java.util.Scanner;

@Dao
public interface ProductDao {

    @Query("INSERT INTO Product VALUES(:id, :name, :info, :imagePath, :sellerName, :sellerId)")
    void insert(int id, String name, String info, String imagePath, String sellerName, String sellerId);


}