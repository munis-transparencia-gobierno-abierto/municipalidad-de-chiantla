package gt.muni.chiantla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Actividad que muestra el acerca de de la aplicación.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class AboutActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.about_muni, true);
        setContentView(R.layout.activity_about);

        CustomNestedScrollView scroll = (CustomNestedScrollView) findViewById(R.id.scrollableInfo);
        initScroll(scroll, findViewById(android.R.id.content));

        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            ((TextView) findViewById(R.id.version)).setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        Long lastUpdate = settings.getLong("lastUpdate", 0);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(lastUpdate * 1000);
        String updateText = getString(R.string.last_update);
        String finalUpdateText = updateText + " " + format.format(date);
        ((TextView) findViewById(R.id.lastUpdate)).setText(finalUpdateText);

    }

    public void goToLink(View view) {
        ViewGroup group = (ViewGroup) view;
        TextView link = (TextView) group.getChildAt(group.getChildCount() - 1);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://" + link.getText().toString()));
        startActivity(i);
    }

    public void goToGitHub(View view) {
        TextView link = (TextView) findViewById(R.id.githubLink);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://" + link.getText().toString()));
        startActivity(i);
    }
}
