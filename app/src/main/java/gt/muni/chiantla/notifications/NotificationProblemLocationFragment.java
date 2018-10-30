package gt.muni.chiantla.notifications;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import gt.muni.chiantla.R;

/**
 * Fragmento que permite que se llene la direccion y si se obtendra posteriormente la ubicacion
 */
public class NotificationProblemLocationFragment extends RequiredStepFragment
        implements View.OnClickListener {
    private final int LOCATION_PERMISSION = 0;
    private EditText neighborhood;
    private EditText address;
    private CheckBox gpsCheckbox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_problem_location, container,
                false);
        neighborhood = view.findViewById(R.id.neighborhood);
        address = view.findViewById(R.id.location);
        gpsCheckbox = view.findViewById(R.id.gps_checkbox);
        gpsCheckbox.setOnClickListener(this);
        return view;
    }

    /**
     * Si el fragmento se esta mostrando actualmente, pedir permiso
     *
     * @param isVisibleToUser si el framento esta siendo mostrado al usuario
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            askPermission();
        }

    }

    /**
     * Valida si los datos requeridos fueron llenados
     *
     * @return si los datos requeridos fueron llenados
     */
    public boolean validate() {
        boolean neighborhoodFilled = (neighborhood.getText() != null &&
                !neighborhood.getText().toString().equals(""));
        boolean addressFilled = (address.getText() != null &&
                !address.getText().toString().equals(""));
        return !IS_REQUIRED || neighborhoodFilled && addressFilled;
    }

    /**
     * Le envia a la actividad la informacion llenada
     *
     * @param activity la actividad en donde se colocan los datos
     */
    public void getData(NewNotificationActivity activity) {
        activity.setLocation(neighborhood.getText().toString());
        activity.setAddress(address.getText().toString());
        activity.setGpsCheckboxChecked(gpsCheckbox.isChecked());
    }

    public void askPermission() {
        if (gpsCheckbox.isChecked()) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
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
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gps_checkbox:
                askPermission();
                break;
        }
    }

}
