package gt.muni.chiantla.mymuni;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.mymuni.development.DevelopmentActivity;
import gt.muni.chiantla.mymuni.know.KnowActivity;

/**
 * Actividad de la sección de mi muni. Muesta un menú.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class MyMuniActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.my_muni, true);
        setContentView(R.layout.activity_my_muni);

        Utils.sendFirebaseEvent("MiChiantla", null, null, null, "Menu_MiChiantla", "Menu002", this);
    }

    public void menu(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.know_button:
                intent = new Intent(this, KnowActivity.class);
                break;
            case R.id.development_button:
                intent = new Intent(this, DevelopmentActivity.class);
                break;
            case R.id.contacts_button:
                intent = new Intent(this, ContactsActivity.class);
                break;
            case R.id.laws_button:
                intent = new Intent(this, LawsActivity.class);
                break;
            case R.id.centers_button:
                intent = new Intent(this, HealthActivity.class);
                break;
        }
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }
}
