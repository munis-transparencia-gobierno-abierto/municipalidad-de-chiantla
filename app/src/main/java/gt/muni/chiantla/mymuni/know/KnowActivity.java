package gt.muni.chiantla.mymuni.know;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Actividad de la sección de conoce. Muestra los fragmentos de información, en un {@link ViewPager}
 * @author Ludiverse
 * @author Innerlemonade
 */
public class KnowActivity extends CustomActivity {
    private KnowPageAdapter mAdapter;
    private ViewPager mViewPager;
    private ImageView nextButton;
    private ImageView prevButton;
    private ProgressBar progress;
    private TextView progressBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.explore_muni, true);
        setContentView(R.layout.activity_know);

        mAdapter = new KnowPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.knowViewPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new KnowPageChangeListener());

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progressBarText = (TextView) findViewById(R.id.progressBarText);
        progressBarText.setText("1/7");

        nextButton = (ImageView) findViewById(R.id.nextArrow);
        prevButton = (ImageView) findViewById(R.id.prevArrow);

        changePage(1);
    }

    /**
     * Metodo que realiza cambios a las vistas de la clase cuando el {@link ViewPager} cambia
     * de fragmento. Cambia la barra de progreso, muestra u oculta los botones de anterior/sieguiente
     * y manda eventos a firebase.
     * @param position la posición del fragmento al que se cambió
     */
    private void changePage(int position) {
        int size = mAdapter.getCount();
        if (position < size) {
            progressBarText.setText(position + "/7");
            nextButton.setVisibility(View.VISIBLE);
            prevButton.setImageAlpha(128);
        } else {
            progressBarText.setText("7/7");
            prevButton.setImageAlpha(255);
            nextButton.setVisibility(View.INVISIBLE);
        }
        if (position > 1) {
            prevButton.setVisibility(View.VISIBLE);
        } else {
            prevButton.setVisibility(View.INVISIBLE);
        }
        double percentage = Math.ceil(((float) position) / mAdapter.getCount() * 100);
        progress.setProgress((int) percentage);

        Utils.sendFirebaseEvent("MiChiantla", "Conoce_a_tu_muni", null, null,
                "Pg" + position, "ConocePg" + position, this);
    }

    /**
     * Muestra el siguiente fragmento.
     * @param view el view que fue presionado.
     */
    public void goToNext(View view) {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    /**
     * Muestra el fragmento anterior.
     * @param view el view que fue presionado.
     */
    public void goToPrev(View view) {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }

    /**
     * Listener que llama al método {@link KnowActivity#changePage(int)} cuando el {@link ViewPager}
     * cambia de fragmento.
     */
    private class KnowPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            KnowActivity.this.changePage(position + 1);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
