package gt.muni.chiantla.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.TutorialFragment;
import gt.muni.chiantla.Utils;

/**
 * Muestra los reportes que se encuentran guardadas en la base de datos y un botón para
 * enviar nuevos reportes.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NotificationsMenuActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true, R.drawable.big_home_button);
        setContentView(R.layout.activity_notifications_menu);

        Utils.sendFirebaseEvent("Avisos_a_la_muni", null, null, null,
                "Menu_Avisos", "Menu_Avisos", this);
        nextTutorial();
    }

    public void goToNewNotification(View view) {
        Intent intent = new Intent(this, NewNotificationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void goToNotifications(View view) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    protected Integer getTutorialResourceId() {
        return R.array.tutorial_notification_menu;
    }

    @Override
    protected Integer getTutorialCount() {
        return 1;
    }

    @Override
    protected String getTutorialSettingName() {
        return "NotificationsMenuTutorial";
    }

    @Override
    protected Integer getCurrentTutorialArrowPosition() {
        switch (currentTutorial) {
            case 0:
                return TutorialFragment.NO_ARROW;
        }
        return null;
    }

    public void phoneNumber(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        TextView phoneView = findViewById(R.id.phoneLink);
        Uri number = Uri.parse("tel:" + phoneView.getText().toString());
        intent.setData(number);
        startActivity(intent);
    }
}
