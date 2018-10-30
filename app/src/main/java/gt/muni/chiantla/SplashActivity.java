package gt.muni.chiantla;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gt.muni.chiantla.notifications.NotificationsActivity;

/**
 * Actividad que muestra dos splash screens.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class SplashActivity extends AppCompatActivity {
    private final Handler HANDLER = new Handler();
    private Fragment splash;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        showSplash();
    }

    /**
     * Muestra el primer fragmento de splash.
     */
    private void showSplash() {
        splash = new SplashFragment();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.activity_splash, splash)
                .commit();
        setSplash2();
    }

    /**
     * Crea el handler para mostrar el segundo splash cuando haya pasado el tiempo requerido.
     */
    private void setSplash2() {
        HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSplash2();
            }
        }, 2000);
    }

    private void showSplash2() {
        splash = new Splash2Fragment();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.activity_splash, splash)
                .commit();
        setTransparentSplash();
        state++;
    }

    /**
     * Muestra un fragmento vacio que sirve como transición entre el splash screen y el menu de
     * la aplicación.
     */
    private void setTransparentSplash() {
        HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                showTransparentSplash();
            }
        }, 2500);
    }

    private void showTransparentSplash() {
        splash = new TransparentFragment();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.activity_splash, splash)
                .commit();
        // Revisa si el app empezo al tocar una notificacion
        if (getIntent().getExtras() != null &&
                getIntent().getExtras().getString("notification_id") != null) {
            startNotificationActivity();
        } else {
            startMainActivity();
        }

    }

    /**
     * Inicia la actividad de notification
     */
    private void startNotificationActivity() {
        String id = getIntent().getExtras().getString("notification_id");
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.putExtra("genId", id);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void dismissSplash(View view) {
        HANDLER.removeCallbacksAndMessages(null);
        switch (state) {
            case 0:
                showSplash2();
                break;
            default:
                showTransparentSplash();
                break;
        }
    }

    // Si se detuvo la actividad a medias, quitar los callbacks que muestran los splash
    @Override
    protected void onStop() {
        super.onStop();
        HANDLER.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HANDLER.removeCallbacksAndMessages(null);
    }

    // Si se regresó a la app, mostrar los splash
    @Override
    protected void onResume() {
        super.onResume();
        showSplash();
    }
}
