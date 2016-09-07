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
    public RealmResults<Visibility> getVisibility() {

        return realm.where(Visibility.class).findAll();
    }

    //find all objects in the Book.class
    public RealmResults<Location_Stats> getLocations() {

        return realm.where(Location_Stats.class).findAll();
    }

    //find all objects in the Book.class
    public RealmResults<User> getUsers() {

        return realm.where(User.class).findAll();
    }

    //query a single item with the given id
    public User getUser(int id) {
        RealmResults<User> u = getUsers();

        return u.first();
    }

    //query a single item with the given id
    public Location_Stats getLocation(int id) {
        RealmResults<Location_Stats> u = getLocations();

        return u.first();
    }
   //query a single item with the given id
    public Visibility getVisibility(int id) {

        RealmResults<Visibility> u = getVisibility();

        return u.first();
    }

    //check if Book.class is empty
    public boolean hasUser() {
        RealmResults<User> u = getUsers();
        return !u.isEmpty();
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


    // check if a particular number has been stored

    public boolean numberStored(String number)
    {
        RealmResults<ContactsList> cl = realm.where(ContactsList.class).equalTo("telephone",number).findAll();
        if(cl.size() != 0)
            return true;
        return  false;
    }


    //check if a particular number has been registered

    public boolean numberRegistered(String number)
    {
        RealmResults<ContactsList> cl = realm.where(ContactsList.class).equalTo("telephone",number).findAll();
        if(cl.size() != 0)
        {
            for (int i=0; i<cl.size(); i++)
            {
              if(cl.get(i).getIs_on_verifie().equalsIgnoreCase("1"))
              {
                  return true;
              }
            }

        }
        return  false;
    }



    //get all contacts

    public RealmResults<ContactsList> getAllContacts()
    {
        return  realm.where(ContactsList.class).findAll();
    }


    // get all registered

    public RealmResults<ContactsList> getAllRegisteredContacts()
    {
        return  realm.where(ContactsList.class).equalTo("is_on_verifie","1").findAll();
    }


    // get all unregistered



    public RealmResults<ContactsList> getAllUnRegisteredContacts()
    {
        return  realm.where(ContactsList.class).equalTo("is_on_verifie","0").findAll();
    }

    // add new contact
    public void addContact(ContactsList cl)
    {
        int last = getAllContacts().size();
        last ++;
        cl.setId(last);
        realm.beginTransaction();
        realm.copyToRealm(cl);
        realm.commitTransaction();
    }

    //update contact
    public void updateContact(ContactsList cl)
    {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(cl);
        realm.commitTransaction();
    }

    // remove contact
    public void removeContact(ContactsList cl)
    {
      cl.removeFromRealm();
    }


    //get a number
    public ContactsList getContact(String number)
    {
        RealmResults<ContactsList> cl = realm.where(ContactsList.class).equalTo("telephone",number).findAll();
        return cl.first();
    }

}
