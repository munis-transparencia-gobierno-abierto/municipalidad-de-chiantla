package gt.muni.chiantla.notifications;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.content.Comment;
import gt.muni.chiantla.content.Notification;

/**
 * Actividad que muestra un reporte.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NotificationActivity extends RestConnectionActivity implements OnMapReadyCallback {
    private Notification notification;
    private View collapse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_notification);
        createOptionsMenu = true;

        notification = (Notification) getIntent().getSerializableExtra("notification");
        collapse = findViewById(R.id.collapse);

        showData();
        showComments();
        showAttachments();

        Utils.sendFirebaseEvent("Avisos_a_la_muni", "Vista_de_Aviso_Enviado", null, null,
                "Vista_de_Aviso_Enviado", "Vista_de_Aviso_Enviado", this);
    }

    private void showData() {
        ((TextView) findViewById(R.id.date)).setText(notification.getDate());
        ((TextView) findViewById(R.id.problem_type)).setText(notification.getProblemType());

        fillOrHideField(R.id.description, R.id.description_subtitle, notification.getProblem());
        fillOrHideField(R.id.address, R.id.address_subtitle, notification.getAddress());
        fillOrHideField(R.id.solution, R.id.solution_subtitle, notification.getSolution());
        fillOrHideField(R.id.name, R.id.name_subtitle, notification.getName());
        fillOrHideField(R.id.phone, R.id.phone_subtitle, notification.getPhone());
        fillOrHideField(R.id.status, R.id.status_subtitle, notification.getStatus());
        fillOrHideField(R.id.neighborhood, R.id.neighborhood_subtitle, notification.getLocation());
        fillOrHideField(R.id.office, R.id.office_subtitle, notification.getOffice());
    }

    /**
     * Muestra un texto o esconde la view que lo deberia de mostrar y el su correspondiente titulo
     *
     * @param resourceId         el id del TextView
     * @param subtitleResourceId el id del subtitulo
     * @param value              el contenido a mostrar
     */
    private void fillOrHideField(int resourceId, int subtitleResourceId, String value) {
        TextView textView = findViewById(resourceId);
        TextView subtitle = findViewById(subtitleResourceId);
        if (value == null || value.equals("")) {
            textView.setVisibility(View.GONE);
            subtitle.setVisibility(View.GONE);
        } else {
            textView.setText(value);
        }
    }

    /**
     * Muestra los comentarios
     */
    private void showComments() {
        if (notification.getCommentsNumber() > 0) {
            findViewById(R.id.responses).setVisibility(View.VISIBLE);
            LinearLayout container = findViewById(R.id.responsesContainer);
            for (Comment comment : notification.getComments()) {
                View view = getLayoutInflater().inflate(R.layout.section_notifiction_response,
                        null);
                ((TextView) view.findViewById(R.id.date)).setText(comment.getDate());
                ((TextView) view.findViewById(R.id.content)).setText(comment.getContent());
                container.addView(view);
            }
        }
    }

    private void showAttachments() {

        Uri imageUri = notification.getImageUri();
        Double lat = notification.getLat();
        Double lon = notification.getLon();
        if (imageUri != null || lat != null || lon != null) {
            findViewById(R.id.extras).setVisibility(View.VISIBLE);
            imageUri = Uri.parse("file://" + imageUri);
            if (imageUri != null) {
                try {
                    ImageView image = findViewById(R.id.image);
                    Bitmap bitmap = null;
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    image.setImageBitmap(bitmap);
                    findViewById(R.id.imageWrap).setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (lat != null && lon != null && lat != 0 && lon != 0) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        findViewById(R.id.mapWrap).setVisibility(View.VISIBLE);

        double lat = notification.getLat();
        double lon = notification.getLon();
        LatLng latLng = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Ubicación"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    /**
     * Deletes the notification if it has not been sent to the server, otherwise it sends the
     * request to the server
     *
     * @param view the button that was tapped
     */
    public void deleteNotification(View view) {
        // The gen id is generated by the server, so we can only delete this notification if
        // the servers notification status is updated. Otherwise the device would receive
        // (android) notifications for a non existing notification.
        if (notification.getGenId() != null && !notification.getGenId().equals("")) {
            String url = "notification/delete/";
            connect(url, new String[]{"case"}, new String[]{notification.getGenId()});
        } else {
            notification.delete(db);
            finish();
        }
    }

    /**
     * Removes the notification if the server removed it. Shows a dialog if it didn't.
     *
     * @param response the servers response
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);

        if (response != null && response.get(0).asBoolean()) {
            notification.delete(db);
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.notification_not_deleted)
                    .setTitle(R.string.error)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                    .show();
        }
    }

    public void expand(View view) {
        if (collapse.getVisibility() == View.VISIBLE) {
            collapse.setVisibility(View.GONE);
            ((ImageView) view).setImageDrawable(
                    getResources().getDrawable(R.drawable.arrow_down_white)
            );
        } else {
            collapse.setVisibility(View.VISIBLE);
            ((ImageView) view).setImageDrawable(
                    getResources().getDrawable(R.drawable.arrow_up_white)
            );
        }
    }
}
