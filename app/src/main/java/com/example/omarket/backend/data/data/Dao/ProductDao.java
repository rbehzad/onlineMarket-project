package com.example.omarket.backend.data.data.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.omarket.backend.data.data.entities.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("INSERT INTO Product VALUES(:id, :name, :info, :imagePath, :sellerName, :sellerId)")
    void insert(int id, String name, String info, String imagePath, String sellerName, String sellerId);

    // get product info by searching its name
    @Query("SELECT * FROM Product WHERE name IN (:productName) ")
    Product loadByName(String productName);

    // get all product
    @Query("SELECT * FROM Product")
    List<Product> getAll();
    // insert products
    @Insert
    void insertAll(Product...products);
    // delete products
    @Delete
    void delete(Product product);
}