package datastore;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 9/16/16.
 */

public class DummyRealm  extends RealmObject {
    @PrimaryKey
    private int id;
}
