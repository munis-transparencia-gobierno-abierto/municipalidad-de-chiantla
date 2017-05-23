package gt.muni.chiantla.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.ArrayList;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.InformationFragment;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Notification;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Muestra los reportes que se encuentran guardadas en la base de datos y un botón para
 * enviar nuevos reportes.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NotificationsActivity extends RestConnectionActivity implements AdapterView.OnItemClickListener {
    protected InformationOpenHelper db;
    private ArrayList<Notification> objects;
    private CustomListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.notification, true);
        setContentView(R.layout.activity_notifications);

        db = InformationOpenHelper.getInstance(this);

        listView = (CustomListView) findViewById(R.id.list);
        initScroll(listView, findViewById(android.R.id.content));
        listView.setOnScrollListener(this);

        updateStatus();

        Utils.sendFirebaseEvent("Avisos_a_la_muni", null, null, null,
                "Menu_Avisos", "Menu_Avisos", this);

    }

    /**
     * Crea un json array con las ids de las notificaciones de la base de datos. Luego
     * envía estos ids al servidor para obtener actualización de su estatus.
     */
    private void updateStatus() {
        objects = Notification.getAll(db);
        String ids = "[";
        for (Notification object: objects) {
            if (object.getGenId() != null) {
                if (objects.get(0) != object)
                    ids += ",";
                ids += '"' + object.getGenId() + '"';
            }
        }
        ids += "]";
        connect("notifications/status/", new String[] {"ids"}, new String[] {ids});
    }

    public void goToNewNotification(View view) {
        Intent intent = new Intent(this, NewNotificationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, NotificationActivity.class);
        Notification object = (Notification) adapterView.getAdapter().getItem(position);
        intent.putExtra("notificationId", object.getId());
        intent.putExtra("genId", object.getGenId());
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_info:
                fragmentManager = getSupportFragmentManager();
                informationFragment = InformationFragment.newInstance(
                        R.string.notifications_info_title,
                        R.string.notifications_info_content
                );
                informationFragment.show(fragmentManager, "dialog");
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    /**
     * Actualiza el estatus de las notificaciones para las que el mismo ha cambiado.
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        if (response != null) {
            JsonObject status = response.get(0).asObject();
            for (Notification object :
                    objects) {
                if (object.getGenId() != null) {
                    String newStatus = status.get(object.getGenId()).asString();
                    if (!newStatus.equals(object.getStatus())) {
                        object.setStatus(newStatus);
                        object.saveStatus(db);
                    }
                }
            }
        }
        listView.setAdapter(new NotificationAdapter(objects, this));
        listView.setOnItemClickListener(this);
    }
}
