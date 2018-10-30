package gt.muni.chiantla.notifications;

import android.os.Bundle;
import android.view.View;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;

/**
 * Fragmento que indica si se ha guardado o no un reporte
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NotificationProcessedActivity extends CustomActivity {
    public static String ACTION_SENT = "SENT NOTIFICAITON";
    public static String ACTION_SAVED = "SAVED NOTIFICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);

        if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_SENT))
            setContentView(R.layout.activity_notification_processed_sent);
        else
            setContentView(R.layout.activity_notification_processed_saved);
    }

    public void endActivity(View view) {
        finish();
    }
}
