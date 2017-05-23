package gt.muni.chiantla.notifications;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;

import java.io.File;
import java.io.IOException;

import gt.muni.chiantla.MainActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Notification;
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Actividad que contiene el formulario para crear un nuevo reporte. Envía el reporte al servidor
 * luego de que se complete la información requerida.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NewNotificationActivity extends RestConnectionActivity implements
        LocationFragment.LocationDialogInterface,
        AdapterView.OnItemSelectedListener {
    public static final String PREFS_NAME = "DbIDs";
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PHOTO = 2;
    private final int LOCATION_PERMISSION = 0;
    private final int FILES_PERMISSION = 1;
    private final Handler handler = new Handler();
    protected InformationOpenHelper db;
    TextView problemView;
    TextView locationView;
    TextView addressView;
    TextView solutionView;
    TextView nameView;
    TextView phoneView;
    private Uri fileUri;
    private CheckBox gpsCheckbox;
    private FragmentManager fragmentManager;
    private LocationFragment locationFragment;
    private LocationManager locationManager;
    private Location lastLocation;
    private LocationListener locationListener;
    private Spinner spinner;
    private boolean firstSelection;
    private ImageView selectedImage;
    private View selectedImageWrap;
    private boolean saved;
    private TextView problemTypeView;
    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        firstSelection = true;
        setCustomActionBar(R.string.new_notification, true);

        setContentView(R.layout.activity_new_notification);

        CustomNestedScrollView scroll = (CustomNestedScrollView) findViewById(R.id.scrollableInfo);
        initScroll(scroll, findViewById(android.R.id.content));

        db = InformationOpenHelper.getInstance(this);

        spinner = (Spinner) findViewById(R.id.problem_spinner);
        // Muestra una view customizada para la selección de tipo de problema
        NotificationProblemsAdapter customAdapter = new NotificationProblemsAdapter(this,
                getResources().getStringArray(R.array.problem_types));
        spinner.setAdapter(customAdapter);
        spinner.setOnItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        gpsCheckbox = (CheckBox) findViewById(R.id.gpsLocation);
        problemTypeView = (TextView) findViewById(R.id.problem_type);
        selectedImage = (ImageView) findViewById(R.id.selectedImage);
        selectedImageWrap = findViewById(R.id.selectedImageWrap);

        problemView = (TextView) findViewById(R.id.problem);
        locationView = (TextView) findViewById(R.id.location);
        addressView = (TextView) findViewById(R.id.address);
        solutionView = (TextView) findViewById(R.id.solution);
        nameView = (TextView) findViewById(R.id.name);
        phoneView = (TextView) findViewById(R.id.phone);

        askPermission(null);

        Utils.sendFirebaseEvent("Avisos_a_la_muni", null, null, null,
                "Formulario_Aviso", "Formulario_Aviso", this);
        saved = false;
    }

    public void openSpinner(View view) {
        spinner.performClick();
    }

    public void askPermission(View view) {
        if (gpsCheckbox.isChecked()) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    gpsCheckbox.setChecked(false);
                }
                break;
            case FILES_PERMISSION:
                setGalleryBitmap();
                break;
        }
    }

    /**
     * Se muestra un fragmento indicando si se envió o no el reporte. Si no se envió se guarda
     * localmente y se guarda su id local para enviarlo posteriormente.
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        PopupWindow window = new PopupWindow(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int id;
        if (response != null && response.get(0).asBoolean()) {
            id = R.layout.fragment_sent;
            notification.setGenId(response.get(1).asString());
            notification.save(db);
        } else {
            notification.setStatus(getString(R.string.saved));
            long dbId = notification.save(db);
            id = R.layout.fragment_saved;
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Long.toString(dbId), dbId);
            editor.apply();
        }
        View rootView = inflater.inflate(id,
                (ViewGroup) findViewById(android.R.id.content).getRootView());
        window.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    /**
     * Obtiene la información ingresada para el reporte y obtiene la ubicacicón. Si no se debe
     * de obtener la ubicación se intenta enviar el reporte. Si se ha ingresado toda la información
     * necesaria se muestra un fragmento que lo indica.
     * @param view El botón que fue presionadao.
     */
    public void saveNotification(View view) {
        String problemType = problemTypeView.getText().toString();
        String problem = problemView.getText().toString();
        String location = locationView.getText().toString();
        String address = addressView.getText().toString();
        String name = nameView.getText().toString();
        String phone = phoneView.getText().toString();
        if (!problemType.equals("") &&
                !problem.equals("") &&
                !location.equals("") &&
                !address.equals("") &&
                !name.equals("") &&
                !phone.equals("")) {

            if (gpsCheckbox.isChecked()) {
                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    String locationProvider = LocationManager.GPS_PROVIDER;
                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    lastLocation = locationManager.getLastKnownLocation(locationProvider);
                    locationListener = new LocationListener();
                    locationManager.requestLocationUpdates(locationProvider, 1000, 0, locationListener);
                    locationFragment = new LocationFragment();
                    locationFragment.show(fragmentManager, "dialog");
                    handler.postDelayed(
                            new Runnable() {
                                public void run() {
                                    cancelLocationUpdates();
                                }
                            }
                            , 20000);
                }
            } else {
                createNotification(0, 0);
            }
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Error");
            alert.setMessage("Debes llenar todos los campos que tienen un asterisco (*).");
            alert.setPositiveButton("Aceptar", null);
            alert.show();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void cancelLocationUpdates() {
        locationManager.removeUpdates(locationListener);
        locationFragment.dismiss();
        handler.removeCallbacksAndMessages(null);
        if (lastLocation != null) {
            createNotification(lastLocation.getLatitude(), lastLocation.getLongitude());
        } else {
            createNotification(0, 0);
        }
    }

    private void locationObtained(Location location) {
        handler.removeCallbacksAndMessages(null);
        locationFragment.dismiss();
        createNotification(location.getLatitude(), location.getLongitude());
    }

    /**
     * Crea la notificación e intenta enviarla al servidor. Si no hay internet la notificación
     * es guardada, junto con su id para ser enviado posteriormente, y se muestra un
     * fragmento que indica que fue guardado localmente.
     * @param lat la latitud obtenida
     * @param lon la longitud obtenida
     */
    private void createNotification(double lat, double lon) {
        if (!saved) {
            boolean internet = false;
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null)
                internet = true;
            saved = true;

            String problemType = problemTypeView.getText().toString();
            String problem = problemView.getText().toString();
            String location = locationView.getText().toString();
            String address = addressView.getText().toString();
            String solution = solutionView.getText().toString();
            String name = nameView.getText().toString();
            String phone = phoneView.getText().toString();
            notification = new Notification(problemType, problem, location, address,
                    solution, name, phone, fileUri, lat, lon, getString(R.string.sent));
            if (!internet) {
                notification.setStatus(getString(R.string.saved));
                long dbId = notification.save(db);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong(Long.toString(dbId), dbId);
                editor.apply();
                PopupWindow window = new PopupWindow(this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                int id = R.layout.fragment_saved;
                View rootView = inflater.inflate(id,
                        (ViewGroup) findViewById(android.R.id.content).getRootView());
                window.showAtLocation(rootView, Gravity.CENTER, 0, 0);
            } else {
                String[] keys = notification.getKeys();
                String[] values = notification.getStrings();
                if (fileUri != null) {
                    String[] fileKeys = notification.getImageKey();
                    String[] filePaths = new String[]{notification.getImageUri().toString()};
                    connectMultipart("notification/new/", keys, values, fileKeys, filePaths);
                } else {
                    connect("notification/new/", keys, values);
                }
            }
        }
    }

    public void selectImages(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);

    }

    public void closeFragment(View view) {
        if (view.getId() == R.id.cancel) {
            locationFragment.dismiss();
            cancelLocationUpdates();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void phoneNumber(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        TextView phoneView = (TextView) findViewById(R.id.phoneLink);
        Uri number = Uri.parse("tel:" + phoneView.getText().toString());
        intent.setData(number);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    fileUri = Utils.getAbsoluteUri(this, data.getData());
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        int permissionCheck = ContextCompat.checkSelfPermission(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    FILES_PERMISSION);
                        } else {
                            setGalleryBitmap();
                        }
                    } else {
                        setGalleryBitmap();
                    }
                }
                break;
            case TAKE_PHOTO:
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
                    setSelectedImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setGalleryBitmap() {
        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.toString());
        setSelectedImage(bitmap);
    }

    private void setSelectedImage(Bitmap bitmap) {
        selectedImage.setImageBitmap(bitmap);
        selectedImageWrap.setVisibility(View.VISIBLE);
    }

    public void removeImage(View view) {
        selectedImageWrap.setVisibility(View.GONE);
        fileUri = null;
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = Utils.createImageFile(this);
            fileUri = Uri.fromFile(photoFile);
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (!firstSelection) {
            String newType = (String) adapterView.getSelectedItem();
            problemTypeView.setText(newType);
        } else {
            firstSelection = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * Listener de ubicación que guarda la nueva ubicación, si es más precisa que la que se había
     * encontrado con anterioridad.
     */
    private class LocationListener implements android.location.LocationListener {

        @Override
        @SuppressWarnings({"MissingPermission"})
        public void onLocationChanged(Location location) {
            if (Utils.isBetterLocation(location, getLastLocation())) {
                if (location.getAccuracy() < 10) {
                    locationManager.removeUpdates(this);
                    locationObtained(location);
                } else {
                    setLastLocation(location);
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
