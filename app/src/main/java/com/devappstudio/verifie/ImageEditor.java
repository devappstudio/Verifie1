package com.devappstudio.verifie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import datastore.RealmController;
import de.hdodenhof.circleimageview.CircleImageView;

public class ImageEditor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        String imagename ="profile";
        CircleImageView profile = (CircleImageView)findViewById(R.id.profile_image);
        if(ImageStorage.checkifImageExists(imagename))
        {
            File file = ImageStorage.getImage("/verifie/profile/"+imagename+".jpg");
            String path = file.getAbsolutePath();
            if (path != null){
                Bitmap b = BitmapFactory.decodeFile(path);
                profile.setImageBitmap(b);

            }
        } else {
            new GetImages(RealmController.with(ImageEditor.this).getUser(1).getFile_name(), profile, imagename).execute() ;
        }
    }
}
