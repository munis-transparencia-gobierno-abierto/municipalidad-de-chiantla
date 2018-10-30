package gt.muni.chiantla.budget;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import gt.muni.chiantla.MainActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.TutorialFragment;
import gt.muni.chiantla.connections.api.RestConnectionActivity;

/**
 * Fragmento con información del presupuesto.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class BudgetInfoActivity extends RestConnectionActivity {
    private final int STORAGE_PERMISSION = 0;
    private Set<String> years;
    private long lastUpdateTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true, R.drawable.big_home_button);
        setContentView(R.layout.activity_budget_info);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        lastUpdateTimestamp = settings.getLong("lastBudgetUpdate", 0);

        paths = new String[]{"budget/years/"};
        connect();
    }

    /**
     * Muestra los textos de la actividad. Para cada año disponible muestra un boton.
     */
    private void setTexts() {
        boolean first = true;
        LinearLayout container = findViewById(R.id.insertPoint);
        for (String year : years) {
            if (first) {
                String mainString = getResources().getString(R.string.budget_year, year);
                ((TextView) findViewById(R.id.subtitle)).setText(mainString);
                first = false;
            } else {
                View view = getLayoutInflater().inflate(R.layout.section_budget_year, container,
                        false);
                ((TextView) view.findViewById(R.id.year)).setText(year);
                container.addView(view);
            }
        }
        showLastUpdate();
        nextTutorial();
    }

    /**
     * Muestra la ultima actualizacion de la informacion que fue realizada por la fuente de datos.
     */
    private void showLastUpdate() {
        Locale locale = new Locale("es");
        Date date = new Date(lastUpdateTimestamp * 1000);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, hh:mm a", locale);
        String updatedString = getResources().getString(
                R.string.budget_updated_info,
                dateFormat.format(date)
        );
        ((TextView) findViewById(R.id.updateInfo)).setText(updatedString);
    }

    /**
     * Guarda la ultima actualizacion de la data del presupuesto y los años disponibles para el
     * presupuesto, que el servidor devolvio.
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        ArrayList<Integer> savedYears = db.getBudgetYears();
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (response != null) {
            lastUpdateTimestamp = (long) response.get(0).asDouble();
            saveLastUpdateTimestamp();
            years = new HashSet<>();
            for (int i = 1; i < response.size(); i++) {
                if (savedYears.contains(response.get(i).asInt()))
                    savedYears.remove(Integer.valueOf(response.get(i).asInt()));
                years.add(response.get(i).asInt() + "");
            }
        } else {
            years = new HashSet<>();
            years.add(currentYear.toString());
        }
        Collections.sort(savedYears);
        Collections.reverse(savedYears);
        for (int year : savedYears) {
            if (year != currentYear) years.add(year + "");
        }
        setTexts();
    }

    /**
     * Guarda el timestamp de la ultima actualizacion de la data que fue realizada por la fuente de
     * datos.
     */
    private void saveLastUpdateTimestamp() {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("lastBudgetUpdate", lastUpdateTimestamp);
        editor.apply();
    }

    /**
     * Inicia la actividad del presupuesto de un año
     *
     * @param view el boton presionado
     */
    public void goToBudget(View view) {
        TextView yearView = view.findViewById(R.id.year);

        Intent intent = new Intent(this, BudgetActivity.class);
        if (yearView != null) intent.putExtra("year", yearView.getText());
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    /**
     * Revisa si se cuentan con permisos antes de descargar el archivo con la informacion del
     * presupuesto
     *
     * @param view el boton presionado
     */
    public void downloadFile(View view) {
        if (checkForPermission()) {
            makeDownload();
        }
    }

    /**
     * Inicia la descarga del archivo con los datos del servidor
     */
    private void makeDownload() {
        String expensesStringUri =
                "https://datos.minfin.gob" +
                        ".gt/dataset/707e6e9d-367d-41be-a41d-c5b9e5518310/resource" +
                        "/7aec23ba-9311-4e66-802d-d911c3677ee9/download/egresos-region-vii" +
                        "-2018.xlsx";
        Uri expensesUri = Uri.parse(expensesStringUri);
        DownloadManager.Request expensesRequest = new DownloadManager.Request(expensesUri);
        expensesRequest.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "egresos.xlsx");
        expensesRequest.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String incomeStringUri = "https://datos.minfin.gob" +
                ".gt/dataset/707e6e9d-367d-41be-a41d-c5b9e5518310/resource/105c966a-b71f-4db6" +
                "-8e8e-caacca249823/download/ingresos-municipales-2018.xlsx";
        Uri incomeUri = Uri.parse(incomeStringUri);
        DownloadManager.Request incomeRequest = new DownloadManager.Request(incomeUri);
        incomeRequest.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "ingresos.xlsx");
        incomeRequest.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(expensesRequest);
        dm.enqueue(incomeRequest);
    }

    /**
     * Revisa si se cuenta con permisos para realizar la descarga
     *
     * @return si se cuenta con permisos o no
     */
    public boolean checkForPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION);
                return false;
            }
        }
        return true;
    }

    /**
     * Listener del resultado de la solicitud de permiso
     *
     * @param requestCode  el codigo del permiso
     * @param permissions  los permisos
     * @param grantResults las respuestas
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION:
                if (grantResults.length != 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeDownload();
                }
                break;
        }
    }

    /**
     * Muestra la actividad de FAQ
     */
    public void goToFaq() {
        Intent intent = new Intent(this, BudgetFAQActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    /**
     * {@link android.app.Activity#onCreateOptionsMenu(Menu)}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        return true;
    }

    /**
     * Muestra el FAQ si se selecciona en el menu
     *
     * @param item el item seleccionado
     * @return si se realizo la operacion
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_info:
                goToFaq();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Obtiene el id del recurso array del tutorial a mostrar en esta actividad
     *
     * @return el id del tutorial
     */
    @Override
    protected Integer getTutorialResourceId() {
        return R.array.tutorial_budget_info;
    }

    /**
     * Obtiene la cantidad de tutoriales a mostrar en la actividad
     *
     * @return la cantidad de tutoriales
     */
    @Override
    protected Integer getTutorialCount() {
        return 2;
    }


    /**
     * Obtiene el nombre del setting en donde se guarda si ya se realizo el tutorial
     *
     * @return el nombre del setting
     */
    @Override
    protected String getTutorialSettingName() {
        return "BudgetInfoTutorial";
    }

    /**
     * Obtiene el tutorial que se debe mostrar
     *
     * @return el tutorial a mostrar
     */
    @Override
    protected View getCurrentTutorialView() {
        switch (currentTutorial) {
            case 0:
                return findViewById(R.id.nextButton);
            case 1:
                return findViewById(R.id.download_button);
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
        switch (currentTutorial) {
            case 0:
                return TutorialFragment.ARROW_BOTTOM_RIGHT;
            case 1:
                return TutorialFragment.ARROW_BOTTOM_CENTER;
        }
        return null;
    }
}
