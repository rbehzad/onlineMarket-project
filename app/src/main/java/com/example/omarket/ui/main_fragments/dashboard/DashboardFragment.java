package com.example.omarket.ui.main_fragments.dashboard;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.omarket.R;
import com.example.omarket.backend.data.data.entities.User;
import com.example.omarket.backend.data.data.repository.Repository;
import com.example.omarket.backend.data.data.repository.RepositoryCallback;
import com.example.omarket.backend.data.data.repository.Result;
import com.example.omarket.ui.NavigationFragment;
import org.jetbrains.annotations.NotNull;


import android.widget.Button;
import android.widget.ImageView;

import android.database.Cursor;
import android.graphics.BitmapFactory;

import android.provider.MediaStore;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeoutException;


public class DashboardFragment extends NavigationFragment {
 //   private static final int PICK_IMAGE = 100;
//    Button button;
//    ImageView image;
    View view;
//    AppCompatActivity appCompatActivity;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        appCompatActivity = new AppCompatActivity();
        View view =  inflater.inflate(R.layout.fragment_dashboard,container,false);
        TextView textViewName = view.findViewById(R.id.dashboardTextview1);
        TextView textViewEmail = view.findViewById(R.id.dashboardTextview2);
        TextView textViewPhone = view.findViewById(R.id.dashboardTextview3);
        Repository.getInstance(getContext()).getAllUsers(new RepositoryCallback<List<User>>() {
            @Override
            public void onComplete(Result<List<User>> result) {
                if(result instanceof Result.Success) {
                    textViewName.setText(((Result.Success<List<User>>) result).data.get(0).name);
                    textViewEmail.setText(((Result.Success<List<User>>) result).data.get(0).emailAddress);
                    textViewPhone.setText(((Result.Success<List<User>>) result).data.get(0).phoneNumber);
                }
                else if(result instanceof Result.Error) {
                    textViewName.setText(((Result.Error<List<User>>) result).exception.toString());
                    textViewEmail.setText("Error");
                    textViewPhone.setText("Error");
                }
            }
        });
        return view;
    }
//    private static int RESULT_LOAD_IMAGE = 1;

    /*
    public void loadImage(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
/*    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       appCompatActivity.setContentView(R.layout.fragment_dashboard);
        Button buttonLoadImage = (Button) view.findViewById(R.id.btnChangeImage);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == appCompatActivity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = appCompatActivity.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        saveCommand.execute();
    }

}