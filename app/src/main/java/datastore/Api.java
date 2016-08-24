package datastore;

/**
 * Created by root on 8/3/16.
 */

public class Api {

    public static final String api = "http://verifie.devappstudio.com/index.php/api_v1/";
    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://192.168.0.104/AndroidFileUpload/fileUpload.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    public static String getApi() {
        return api;
    }
}
