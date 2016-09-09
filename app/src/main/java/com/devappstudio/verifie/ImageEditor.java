package com.devappstudio.verifie;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import datastore.Api;
import datastore.RealmController;
import datastore.User;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class ImageEditor extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private Uri outputFileUri;
    static String server_id;
    CircleImageView profile;
    final String imagename ="profile.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

         profile = (CircleImageView)findViewById(R.id.profile_image);
        if(ImageStorage.checkifImageExists(imagename))
        {
            File file = ImageStorage.getImage("/verifie/profile/"+imagename);
            String path = file.getAbsolutePath();
            if (path != null){
                Bitmap b = BitmapFactory.decodeFile(path);
                profile.setImageBitmap(b);

            }
        } else {
            new GetImages(RealmController.with(ImageEditor.this).getUser(1).getFile_name(), profile, imagename).execute() ;
        }

        server_id = RealmController.with(ImageEditor.this).getUser(1).getServer_id();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "/verifie/profile/" + File.separator);
                root.mkdirs();
                final String fname = imagename;
                final File sdImageMainDirectory = new File(root, fname);
                outputFileUri = Uri.fromFile(sdImageMainDirectory);

                // Camera.
                final List<Intent> cameraIntents = new ArrayList<Intent>();
                final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                final PackageManager packageManager = getPackageManager();
                final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
                for(ResolveInfo res : listCam) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(captureIntent);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    cameraIntents.add(intent);
                }

                // Filesystem.
                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                if (action == null) {
                    isCamera = false;
                } else {
                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }
            String path="";
            Uri selectedImageUri;
            if (isCamera) {
                path = outputFileUri.getPath().toString();

            } else {
                /*
                selectedImageUri = data == null ? null : data.getData();
                String uriString = selectedImageUri.toString();
                File myFile = new File(uriString);
                path = myFile.getAbsolutePath();
                */

                Uri uri = data.getData();
                String[] projection = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                 path = cursor.getString(columnIndex); // returns null
                cursor.close();

            }
            Toast.makeText(getApplicationContext(),path,Toast.LENGTH_LONG).show();

            uploadMultipart(getApplicationContext(),path);

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }


    public void uploadMultipart(final Context context,String path) {
        try {
            String uploadId =
                    new MultipartUploadRequest(context, Api.getApi() + "upload_image")
                            .addFileToUpload(path, "file_name")
                            .addParameter("server_id", server_id)
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate() {
                                @Override
                                public void onProgress(UploadInfo uploadInfo) {
                                    // your code here
                                    Toast.makeText(getApplicationContext(), uploadInfo.getProgressPercent() + " %", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(UploadInfo uploadInfo, Exception exception) {
                                    // your code here
                                    exception.printStackTrace();
                                }

                                @Override
                                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                                    // your code here
                                    // if you have mapped your server response to a POJO, you can easily get it:
                                    // YourClass obj = new Gson().fromJson(serverResponse.getBodyAsString(), YourClass.class);
                                    // JSONObject obj = serverResponse.getBodyAsString()
                                    try {

                                        JSONObject obj = new JSONObject(serverResponse.getBodyAsString());

                                        String location = obj.get("data").toString();

                                        Realm realm = Realm.getDefaultInstance();
                                        new GetImages(location, profile, imagename).execute() ;
                                        User us = realm.where(User.class).findFirst();
                                        us.setFile_name(location);
                                        realm.beginTransaction();
                                        realm.copyToRealmOrUpdate(us);
                                        realm.commitTransaction();
                                        Log.d("My App", obj.toString());
                                        Toast.makeText(getApplicationContext(),us.getFile_name(),Toast.LENGTH_LONG).show();

                                    } catch (Throwable t) {
                                        Log.e("My App", "Could not parse malformed JSON: ");
                                    }

//                                    new GetImages(RealmController.with(ImageEditor.this).getUser(1).getFile_name(), profile, imagename).execute() ;

                                }

                                @Override
                                public void onCancelled(UploadInfo uploadInfo) {
                                    // your code here
                                }
                            })
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

}
