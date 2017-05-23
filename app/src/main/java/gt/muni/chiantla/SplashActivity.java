package gt.muni.chiantla;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Actividad que muestra dos splash screens.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class SplashActivity extends AppCompatActivity {
    Fragment splash;

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
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash = new Splash2Fragment();
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                        .replace(R.id.activity_splash, splash)
                        .commit();
                transparentSplash();
            }
        }, 2000);
    }

    /**
     * Muestra un fragmento vacio que sirve como transición entre el splash screen y el menu de
     * la aplicación.
     */
    private void transparentSplash() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash = new TransparentFragment();
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                        .replace(R.id.activity_splash, splash)
                        .commit();
                startMainActivity();
            }
        }, 2500);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
