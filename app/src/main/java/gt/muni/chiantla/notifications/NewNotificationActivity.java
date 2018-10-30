package gt.muni.chiantla.notifications;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.eclipsesource.json.JsonArray;

import gt.muni.chiantla.R;
import gt.muni.chiantla.TutorialFragment;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Notification;

/**
 * Actividad que contiene el formulario para crear un nuevo reporte. Envía el reporte al servidor
 * luego de que se complete la información requerida.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NewNotificationActivity extends RestConnectionActivity implements
        LocationFragment.LocationDialogInterface {
    public static final String PREFS_NAME = "DbIDs";
    private static final int STEP_COUNT = 5;
    private final Handler handler = new Handler();
    protected InformationOpenHelper db;
    private ViewPager pager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private LocationFragment locationFragment;
    private LocationManager locationManager;
    private Location lastLocation;
    private LocationListener locationListener;
    private boolean saved;
    private Notification notification;

    private String problemType;
    private String problem;
    private String location;
    private String address;
    private String name;
    private String phone;
    private String email;
    private boolean gpsCheckboxChecked;
    private Uri fileUri;

    private boolean isBudgetFragment;

    // flag to call nextTutorial only once
    private boolean tutorialShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCustomActionBar(null, true);
        createOptionsMenu = true;

        setContentView(R.layout.activity_new_notification);

        db = InformationOpenHelper.getInstance(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name", null);
            if (name != null) {
                isBudgetFragment = true;
                String imageUri = extras.getString("image", null);
                fileUri = Uri.parse(imageUri);
                problemType = getResources().getString(R.string.budget_problem_type);
                problem = getResources().getString(R.string.budget_notification, name);
            }
        }

        fragmentManager = getSupportFragmentManager();
        pager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(fragmentManager);
        pager.setAdapter(pagerAdapter);

        Utils.sendFirebaseEvent("Avisos_a_la_muni", null, null, null,
                "Formulario_Aviso", "Formulario_Aviso", this);
        saved = false;
        tutorialShown = false;
    }

    /**
     * Shows the tutorial
     *
     * @param hasFocus Whether the window of this activity has focus.
     * @see #hasWindowFocus()
     * @see #onResume
     * @see View#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!tutorialShown) {
            nextTutorial();
            tutorialShown = true;
        }
    }

    /**
     * Se muestra un fragmento indicando si se envió o no el reporte. Si no se envió se guarda
     * localmente y se guarda su id local para enviarlo posteriormente.
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        String id;
        if (response != null && response.get(0).asBoolean()) {
            id = NotificationProcessedActivity.ACTION_SENT;
            notification.setGenId(response.get(1).asString());
            notification.setOffice(response.get(3).asString());
            notification.save(db);
        } else {
            notification.setStatus(getString(R.string.saved));
            long dbId = notification.save(db);
            id = NotificationProcessedActivity.ACTION_SAVED;
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Long.toString(dbId), dbId);
            editor.apply();
        }
        Intent intent = new Intent(this, NotificationProcessedActivity.class);
        intent.setAction(id);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }

    /**
     * Obtiene la información ingresada para el reporte y obtiene la ubicacicón. Si no se debe
     * de obtener la ubicación se intenta enviar el reporte. Si se ha ingresado toda la información
     * necesaria se muestra un fragmento que lo indica.
     */
    public void saveNotification() {
        if (gpsCheckboxChecked) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                String locationProvider = LocationManager.GPS_PROVIDER;
                locationManager = (LocationManager) this.getSystemService(Context
                        .LOCATION_SERVICE);
                lastLocation = locationManager.getLastKnownLocation(locationProvider);
                locationListener = new LocationListener();
                locationManager.requestLocationUpdates(locationProvider, 1000, 0,
                        locationListener);
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

    public void closeLocationFragment(View view) {
        cancelLocationUpdates();
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
     *
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

            String fileUriString = (fileUri != null) ? fileUri.toString() : null;
            notification = new Notification(problemType, problem, location, address,
                    email, name, phone, fileUriString, lat, lon, getString(R.string.sent));
            if (!internet) {
                notification.setStatus(getString(R.string.saved));
                long dbId = notification.save(db);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong(Long.toString(dbId), dbId);
                editor.apply();
                Intent intent = new Intent(this, NotificationProcessedActivity.class);
                intent.setAction(NotificationProcessedActivity.ACTION_SAVED);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                finish();
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

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void prevStep(View view) {
        this.onBackPressed();
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGpsCheckboxChecked(boolean gpsCheckboxChecked) {
        this.gpsCheckboxChecked = gpsCheckboxChecked;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View rootView = findViewById(android.R.id.content);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    /**
     * Pasa al siguiente paso, si se ha llenado toda la informacion necesaria
     *
     * @param view la view que fue presionada
     */
    public void nextStep(View view) {
        int nextItem = this.pager.getCurrentItem() + 1;
        if (nextItem <= STEP_COUNT) {
            if (pagerAdapter.saveData()) {
                this.pager.setCurrentItem(nextItem, true);
                if (nextItem == 3) {
                    currentTutorial = 0;
                    nextTutorial();
                }
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.required_fields_content)
                        .setTitle(R.string.required_fields)
                        .create()
                        .show();
            }
            if (nextItem == STEP_COUNT) saveNotification();
        }
    }

    /**
     * Obtiene el id del recurso array del tutorial a mostrar en esta actividad
     *
     * @return el id del tutorial
     */
    @Override
    protected Integer getTutorialResourceId() {
        if (pager.getCurrentItem() == 3) {
            return R.array.tutorial_new_notification_photo;
        }
        return R.array.tutorial_new_notification;
    }

    /**
     * Obtiene la cantidad de tutoriales a mostrar en la actividad
     *
     * @return la cantidad de tutoriales
     */
    @Override
    protected Integer getTutorialCount() {
        if (pager.getCurrentItem() == 3) {
            return 2;
        }
        return 1;
    }

    /**
     * Obtiene el nombre del setting en donde se guarda si ya se realizo el tutorial
     *
     * @return el nombre del setting
     */
    @Override
    protected String getTutorialSettingName() {
        if (pager.getCurrentItem() == 3) {
            return "NewNotificationPhotoTutorial";
        }
        return "NewNotificationTutorial";
    }

    /**
     * Obtiene el tutorial que se debe mostrar
     *
     * @return el tutorial a mostrar
     */
    @Override
    protected View getCurrentTutorialView() {
        if (pager.getCurrentItem() == 3) {
            switch (currentTutorial) {
                case 0:
                    return findViewById(R.id.camera_button);
                case 1:
                    return findViewById(R.id.photo_button);
                default:
                    return null;
            }
        }
        switch (currentTutorial) {
            case 0:
                return findViewById(R.id.nextButton);
        }
        return null;
    }

    /**
     * Obtiene la posicion de la flecha del tutorial actual
     *
     * @return la posicion de la flecha del tutorial
     */
    @Override
    protected Integer getCurrentTutorialArrowPosition() {
        if (pager.getCurrentItem() == 3) {
            switch (currentTutorial) {
                case 0:
                case 1:
                    return TutorialFragment.ARROW_BOTTOM_CENTER;
                default:
                    return null;
            }
        }
        switch (currentTutorial) {
            case 0:
                return TutorialFragment.ARROW_BOTTOM_RIGHT;
        }
        return null;
    }

    /**
     * Custom ViewPager que no permite al usuario cambiar de fragmento haciendo swipe
     */
    public static class NewNotificationViewPager extends ViewPager {

        public NewNotificationViewPager(Context context) {
            super(context);
        }

        public NewNotificationViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return false;
        }
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

    /**
     * Adaptador que guarda en una SparseArray los fragmentos que se muestran.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private SparseArray<StepValidation> currentFragments;

        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            currentFragments = new SparseArray<>();
        }

        @Override
        public Fragment getItem(int position) {
            hideKeyboard();
            StepValidation fragment;
            switch (position) {
                case 0:
                    fragment = NotificationProblemTypeFragment.createFragment(isBudgetFragment);
                    currentFragments.put(position, fragment);
                    break;
                case 1:
                    fragment = NotificationProblemDescFragment.createFragment(problem);
                    currentFragments.put(position, fragment);
                    break;
                case 2:
                    currentFragments.put(position, new NotificationProblemLocationFragment());
                    break;
                case 3:
                    fragment = NotificationProblemImageFragment.createFragment(fileUri);
                    currentFragments.put(position, fragment);
                    break;
                case 4:
                    currentFragments.put(position, new NotificationProblemContactFragment());
                    break;
            }
            return (Fragment) currentFragments.get(position);
        }

        private boolean saveData() {
            int current = pager.getCurrentItem();
            if (currentFragments.get(current).validate()) {
                currentFragments.get(current).getData(NewNotificationActivity.this);
                return true;
            }
            return false;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            currentFragments.delete(position);
        }

        @Override
        public int getCount() {
            return STEP_COUNT;
        }
    }
}
