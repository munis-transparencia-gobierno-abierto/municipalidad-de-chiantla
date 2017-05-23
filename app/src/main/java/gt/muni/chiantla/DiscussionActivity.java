package gt.muni.chiantla;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Actividad que muestra información de la discución a travez de facebook. Tiene un botón que
 * abre la página de facebook.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class DiscussionActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.discussion, true);
        setContentView(R.layout.activity_discussion);

        CustomNestedScrollView scroll = (CustomNestedScrollView) findViewById(R.id.scrollableInfo);
        initScroll(scroll, findViewById(android.R.id.content));

        Utils.sendFirebaseEvent("Noticias_y_Disusion", null, null, null, "Noticias_y_Discusion", "Discusion", this);
    }

    /**
     * Listener que abre la página de facebook. Dependiendo de la versión de facebook y de si
     * facebook se encuentra instalado en el dispoitivo, se utilizan disntos links.
     * @param view
     */
    public void goToFacebook(View view) {
        PackageManager packageManager = this.getPackageManager();
        String facebook;
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                facebook = "fb://facewebmodal/f?href=https://www.facebook.com/MuniChiantla/";
            } else {
                facebook = "fb://page/MuniChiantla";
            }
        } catch (PackageManager.NameNotFoundException e) {
            facebook = "https://www.facebook.com/MuniChiantla/";
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(facebook));
        startActivity(facebookIntent);
    }
}
