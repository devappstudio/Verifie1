package datastore;

/**
 * Created by root on 8/3/16.
 */

public class Api {

    public static final String api = "http://admin.verifieworld.com/index.php/api_v1/";
    public static final String image_end = "http://admin.verifieworld.com/index.php/auth/user_image/";
    // File upload url (replace the ip with your server address)
   public static final String FILE_UPLOAD_URL = "http://192.168.0.104/AndroidFileUpload/fileUpload.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    public static String getApi() {
        return api;
    }

    public static String getImage_end()
    {
        return image_end;
    }
}
