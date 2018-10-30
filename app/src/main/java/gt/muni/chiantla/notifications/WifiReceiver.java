package gt.muni.chiantla.notifications;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.SparseArray;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import gt.muni.chiantla.R;
import gt.muni.chiantla.connections.api.AbstractRestConnection;
import gt.muni.chiantla.connections.api.RestConnection;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Notification;

/**
 * {@link BroadcastReceiver} que intenta reenviar las notificaciones que no se pudienron enviar,
 * si hay un cambio en la red
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class WifiReceiver extends BroadcastReceiver
        implements AbstractRestConnection.RestConnectionInterface<JsonArray> {
    public static final String PREFS_NAME = "DbIDs";
    private InformationOpenHelper db;
    private SparseArray<Notification> notifications;
    private boolean isConnected = false;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ConnectivityManager connectMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo net = connectMan.getActiveNetworkInfo();
        if (net != null && net.getState() == NetworkInfo.State.CONNECTED && !isConnected) {
            isConnected = true;
            db = InformationOpenHelper.getInstance(context);
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            Map<String, ?> mapKeys = settings.getAll();
            if (notifications == null) notifications = new SparseArray<>();
            for (Map.Entry<String, ?> entry : mapKeys.entrySet()) {
                Integer notificationId = Integer.parseInt(entry.getValue().toString());
                Notification notification = new Notification(db, notificationId);
                notifications.put(notificationId, notification);
                String[] keys = notification.getKeys();
                String[] values = notification.getStrings();
                Uri fileUri = notification.getImageUri();
                if (fileUri != null) {
                    String[] fileKeys = notification.getImageKey();
                    String[] filePaths = new String[]{notification.getImageUri().toString()};
                    connectMultipart("notification/new/", keys, values, fileKeys, filePaths);
                } else {
                    connect("notification/new/", keys, values);
                }
            }
        } else
            isConnected = false;
    }

    public void connect(String path, String[] keys, String[] parameters) {
        RestConnection connection = new RestConnection(this);
        List<NameValuePair> pairs = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            pairs.add(new BasicNameValuePair(keys[i], parameters[i]));
        }
        connection.setParameters(pairs);
        connection.execute(path);
    }

    public void connectMultipart(String path, String[] keys, String[] values,
                                 String[] fileKeys, String[] filePaths) {
        RestConnection connection = new RestConnection(this);
        connection.setMultipart(true, values, keys, filePaths, fileKeys);
        connection.execute(path);
    }

    @Override
    public void restResponseHandler(JsonArray response) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if (response != null && response.get(0).asBoolean()) {
            int notificationId = Integer.parseInt(response.get(2).asString());
            Notification notification = notifications.get(notificationId);
            notification.setGenId(response.get(1).asString());
            notification.setStatus(context.getString(R.string.sent));
            notification.setOffice(response.get(3).asString());
            notification.saveGenId(db);
            notification.saveStatus(db);
            notification.saveOffice(db);
            editor.remove(notificationId + "");
            editor.apply();
        }
    }

    @Override
    public ContentResolver getContentResolver() {
        return context.getContentResolver();
    }
}