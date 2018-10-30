package gt.muni.chiantla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;

import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Page;

/**
 * Actividad que muestra información de la discución a travez de facebook. Tiene un botón que
 * abre la página de facebook.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class DiscussionActivity extends RestConnectionActivity {
    private int PAGE_ID = 1;
    private String url;
    private InformationOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_discussion);

        ((TextView) findViewById(R.id.title)).setText(R.string.discussion);

        paths = new String[]{"page/" + PAGE_ID + "/link/"};
        helper = InformationOpenHelper.getInstance(this);
        loadUrl();
        checkForUpdates();

        Utils.sendFirebaseEvent("Noticias_y_Disusion", null, null, null, "Noticias_y_Discusion",
                "Discusion", this);
    }

    private void loadUrl() {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        url = settings.getString("discussionUrl", "https://www.facebook.com/MuniChiantla/");
    }

    private void checkForUpdates() {
        if (helper != null && !helper.isUpdated(Page.APP_NAME, "LinkPage", PAGE_ID)) {
            connect();
        }
    }

    @Override
    public void restResponseHandler(JsonArray response) {
        if (response != null) {
            url = response.get(0).asString();
            SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("discussionUrl", url);
            editor.apply();
            helper.addOrUpdateUpdate(Page.APP_NAME, "LinkPage", PAGE_ID, true);
        }
        super.restResponseHandler(response);
    }

    /**
     * Listener que abre la página de facebook. Dependiendo de la versión de facebook y de si
     * facebook se encuentra instalado en el dispoitivo, se utilizan disntos links.
     *
     * @param view la view que fue presionada
     */
    public void goToFacebook(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String pageUrl = url;
        if (url.contains("https://www.facebook.com")) {
            PackageManager packageManager = this.getPackageManager();
            try {
                int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0)
                        .versionCode;
                if (versionCode >= 3002850) {
                    pageUrl = "fb://facewebmodal/f?href=" + url;
                } else {
                    String pageName = url.replace("https://www.facebook.com", "");
                    pageUrl = "fb://page" + pageName;
                }
            } catch (PackageManager.NameNotFoundException e) {
                // In case there is no facebook app installed we open the default url in the browser
                // which is the default case. ie. this catch is empty on purpose.
            }
        }
        intent.setData(Uri.parse(pageUrl));
        startActivity(intent);
    }
}
