package gt.muni.chiantla.notifications;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Notification;
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Actividad que muestra un reporte.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NotificationActivity extends CustomActivity implements OnMapReadyCallback {
    private InformationOpenHelper db;
    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.sent_notification, true);
        setContentView(R.layout.activity_notification);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getView().setVisibility(View.GONE);

        CustomNestedScrollView scroll = (CustomNestedScrollView) findViewById(R.id.scrollableInfo);
        initScroll(scroll, findViewById(android.R.id.content));

        db = InformationOpenHelper.getInstance(this);

        int notificationId = getIntent().getIntExtra("notificationId", 0);
        notification = new Notification(db, notificationId);
        String genId = getIntent().getStringExtra("genId");

        ((TextView) findViewById(R.id.gen_id)).setText(genId);
        ((TextView) findViewById(R.id.date)).setText(notification.getDate());
        ((TextView) findViewById(R.id.problem_type)).setText(notification.getProblemType());
        ((TextView) findViewById(R.id.description)).setText(notification.getProblem());
        ((TextView) findViewById(R.id.address)).setText(notification.getAddress());
        ((TextView) findViewById(R.id.solution)).setText(notification.getSolution());
        ((TextView) findViewById(R.id.name)).setText(notification.getName());
        ((TextView) findViewById(R.id.phone)).setText(notification.getPhone());
        ((TextView) findViewById(R.id.status)).setText(notification.getStatus());

        // muestra los attachments
        Uri imageUri = notification.getImageUri();
        double lat = notification.getLat();
        double lon = notification.getLon();
        if (imageUri != null || lat != 0 || lon != 0) {
            imageUri = Uri.parse("file://"+imageUri);
            findViewById(R.id.attachments).setVisibility(View.VISIBLE);
            if (imageUri != null) {
                try {
                    ImageView image = ((ImageView) findViewById(R.id.image));
                    Bitmap bitmap = null;
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    image.setImageBitmap(bitmap);
                    image.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageWrap).setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (lat != 0 || lon != 0) {
                mapFragment.getMapAsync(this);
                mapFragment.getView().setVisibility(View.VISIBLE);
                findViewById(R.id.mapWrap).setVisibility(View.VISIBLE);
            }
        }

        Utils.sendFirebaseEvent("Avisos_a_la_muni", "Vista_de_Aviso_Enviado", null, null,
                "Vista_de_Aviso_Enviado", "Vista_de_Aviso_Enviado", this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        double lat = notification.getLat();
        double lon = notification.getLon();
        LatLng latLng = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Ubicación"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }
}
