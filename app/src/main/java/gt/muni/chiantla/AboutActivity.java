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

/**
 * Actividad que muestra el acerca de de la aplicación.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class AboutActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_about);

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
        ((TextView) findViewById(R.id.lastUpdate)).setText(format.format(date));

    }

    public void goToLink(View view) {
        ViewGroup group = (ViewGroup) view;
        TextView link = (TextView) group.getChildAt(group.getChildCount() - 2);
        Intent i;
        if (link.getText().toString().contains("@")) {
            i = new Intent(Intent.ACTION_SEND);
            i.setType("*/*");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{link.getText().toString()});
        } else {
            i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://" + link.getText().toString()));
        }
        startActivity(i);
    }

    public void goToGitHub(View view) {
        TextView link = findViewById(R.id.githubLink);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://" + link.getText().toString()));
        startActivity(i);
    }

    public void goToGooglePlayServicesPrivacy(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://policies.google.com/privacy"));
        startActivity(i);
    }

    public void goToFirebaseAnalyticsPrivacy(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://firebase.google.com/policies/analytics/"));
        startActivity(i);
    }
}
