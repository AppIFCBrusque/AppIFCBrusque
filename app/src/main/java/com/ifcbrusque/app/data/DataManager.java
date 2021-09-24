package com.ifcbrusque.app.data;

import com.ifcbrusque.app.data.db.DbHelper;
import com.ifcbrusque.app.data.network.NetworkHelper;
import com.ifcbrusque.app.data.notification.NotificationHelper;
import com.ifcbrusque.app.data.prefs.PreferencesHelper;

public interface DataManager extends DbHelper, NetworkHelper, NotificationHelper, PreferencesHelper {

}
