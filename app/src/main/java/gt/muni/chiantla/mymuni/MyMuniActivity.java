package gt.muni.chiantla.mymuni;

import android.os.Bundle;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;

/**
 * Actividad de la sección de mi muni. Muesta un menú.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class MyMuniActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true, R.drawable.big_home_button);
        setContentView(R.layout.activity_my_muni);
        createOptionsMenu = true;

        Utils.sendFirebaseEvent("MiChiantla", null, null, null, "Menu_MiChiantla", "Menu002", this);
    }
}
