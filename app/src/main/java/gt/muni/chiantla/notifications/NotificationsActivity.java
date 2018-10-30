package gt.muni.chiantla.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.Collection;
import java.util.HashMap;

import gt.muni.chiantla.R;
import gt.muni.chiantla.TutorialFragment;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Notification;

/**
 * Actividad que muestra las notificaciones guardadas
 */
public class NotificationsActivity extends RestConnectionActivity implements AdapterView
        .OnItemClickListener {
    protected InformationOpenHelper db;
    private ListView listView;
    private HashMap<String, Notification> objects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_notifications);
        createOptionsMenu = true;

        Utils.sendFirebaseEvent("Avisos_a_la_muni", null, null, null,
                "Menu_Avisos", "Avisos_Enviados", this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        db = InformationOpenHelper.getInstance(this);
        listView = findViewById(R.id.list);
        updateStatus();
    }

    /**
     * Crea un json array con las ids de las notificaciones de la base de datos. Luego
     * envía estos ids al servidor para obtener actualización de su estatus.
     */
    private void updateStatus() {
        objects = Notification.getAll(db);
        String ids = "[";
        Collection<Notification> values = objects.values();
        int cont = 0;
        for (Notification object : values) {
            if (object.getGenId() != null) {
                if (cont != 0)
                    ids += ",";
                ids += '"' + object.getGenId() + '"';
            }
            cont++;
        }
        ids += "]";
        connect("notifications/info/", new String[]{"ids"}, new String[]{ids});
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, NotificationActivity.class);
        Notification object = (Notification) adapterView.getAdapter().getItem(position);
        intent.putExtra("notification", object);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    /**
     * Actualiza el estatus de las notificaciones para las que el mismo ha cambiado.
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        if (response != null) {
            JsonObject info = response.get(0).asObject();
            for (Notification object : objects.values()) {
                if (!object.getGenId().equals("")) {
                    JsonObject currentInfo = info.get(object.getGenId()).asObject();
                    String newStatus = currentInfo.get(Notification.KEY_STATUS).asString();
                    if (!newStatus.equals(object.getStatus())) {
                        object.setStatus(newStatus);
                        object.saveStatus(db);
                    }
                    if (currentInfo.get(Notification.KEY_COMMENTS) != null) {
                        JsonArray comments = currentInfo.get(Notification.KEY_COMMENTS).asArray();
                        for (int j = object.getCommentsNumber(); j < comments.size(); j++) {
                            JsonObject comment = comments.get(j).asObject();
                            String date = comment.get("date").asString();
                            String commentContent = comment.get("content").asString();
                            object.addComment(date, commentContent, db);
                        }
                    }
                }
            }
        }
        if (getIntent().getExtras() != null &&
                getIntent().getExtras().getString("genId") != null) {
            String id = getIntent().getExtras().getString("genId");
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("notification", objects.get(id));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            listView.setAdapter(new NotificationAdapter(objects.values(), this));
            listView.setOnItemClickListener(this);
            nextTutorial();
        }
    }

    @Override
    protected Integer getTutorialResourceId() {
        return R.array.tutorial_notifications;
    }

    @Override
    protected Integer getTutorialCount() {
        return 1;
    }

    @Override
    protected String getTutorialSettingName() {
        return "NotificationsTutorial";
    }

    @Override
    protected Integer getCurrentTutorialArrowPosition() {
        switch (currentTutorial) {
            case 0:
                return TutorialFragment.NO_ARROW;
        }
        return null;
    }
}
