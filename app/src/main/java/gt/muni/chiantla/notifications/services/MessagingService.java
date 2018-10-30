package gt.muni.chiantla.notifications.services;

import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Servicio que recibe notificaciones del sistema y muestra un toast cuando fue recibida.
 */
public class MessagingService extends FirebaseMessagingService {

    Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(
                        MessagingService.this,
                        remoteMessage.getNotification().getTitle(),
                        Toast.LENGTH_LONG
                );
                toast.show();
            }
        });
    }
}
