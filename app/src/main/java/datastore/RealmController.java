package datastore;

/**
 * Created by root on 8/6/16.
 */

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }


    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(User.class);
        realm.commitTransaction();
    }

    //clear all objects from Book.class
    public void clearAllVisibility() {

        realm.beginTransaction();
        realm.clear(Visibility.class);
        realm.commitTransaction();
    }

    //clear all objects from Book.class
    public void clearAllLocation() {

        realm.beginTransaction();
        realm.clear(Location_Stats.class);
        realm.commitTransaction();
    }

    //find all objects in the Book.class
    public RealmResults<User> getBooks() {

        return realm.where(User.class).findAll();
    }

    //query a single item with the given id
    public User getUser(String id) {

        return realm.where(User.class).equalTo("id", id).findFirst();
    }

    //query a single item with the given id
    public Location_Stats getLocation(String id) {

        return realm.where(Location_Stats.class).equalTo("id", id).findFirst();
    }
   //query a single item with the given id
    public Visibility getVisibility(String id) {

        return realm.where(Visibility.class).equalTo("id", id).findFirst();
    }

    //check if Book.class is empty
    public boolean hasUser() {

        return !realm.allObjects(User.class).isEmpty();
    }
  //check if Book.class is empty
    public boolean hasVisible() {

        return !realm.allObjects(Visibility.class).isEmpty();
    }
    public boolean hasLocation() {

        return !realm.allObjects(Location_Stats.class).isEmpty();
    }

    //query example
    public RealmResults<User> queryedBooks() {

        return realm.where(User.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();

    }


}
