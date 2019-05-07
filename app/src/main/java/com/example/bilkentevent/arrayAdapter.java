package com.example.bilkentevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class arrayAdapter extends ArrayAdapter<ClubEvent> {

    Context context;
    public arrayAdapter(Context context , int resourceId , List<ClubEvent>items){
        super(context,resourceId,items);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        ClubEvent event = getItem(position);

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item , parent , false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        final ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(event.getTopic());

        String getStorage = "images/"+event.getEventID();


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        storageRef.child(getStorage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                //System.out.println(uri.toString());
                Picasso.get().load(uri.toString()).into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Picasso.get().load("https://media.wired.com/photos/5b17381815b2c744cb650b5f/master/w_1164,c_limit/GettyImages-134367495.jpg").into(image);
            }
        });



        return convertView;
    }



}
