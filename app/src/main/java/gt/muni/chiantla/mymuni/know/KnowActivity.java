package gt.muni.chiantla.mymuni.know;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.mymuni.MenuActivity;

/**
 * Actividad de la sección de conoce. Muestra los fragmentos de información, en un {@link ViewPager}
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class KnowActivity extends MenuActivity {
    private KnowPageAdapter mAdapter;
    private ViewPager mViewPager;
    private Button nextButton;
    private Button prevButton;
    private ProgressBar progress;
    private TextView progressBarText;
    private int pageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        createOptionsMenu = true;
    }

    /**
     * Metodo que realiza cambios a las vistas de la clase cuando el {@link ViewPager} cambia
     * de fragmento. Cambia la barra de progreso, muestra u oculta los botones de
     * anterior/sieguiente
     * y manda eventos a firebase.
     *
     * @param position la posición del fragmento al que se cambió
     */
    private void changePage(int position) {
        int size = mAdapter.getCount();
        progressBarText.setText(position + " / " + pageCount);
        if (position == 1) {
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        } else if (position < size) {
            nextButton.setVisibility(View.VISIBLE);
            prevButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.GONE);
            prevButton.setVisibility(View.VISIBLE);
        }
        double percentage = Math.ceil(((float) position) / mAdapter.getCount() * 100);
        progress.setProgress((int) percentage);

        Utils.sendFirebaseEvent("MiChiantla", "Conoce_a_tu_muni", null, null,
                "Pg" + position, "ConocePg" + position, this);
    }

    /**
     * Muestra el siguiente fragmento.
     *
     * @param view el view que fue presionado.
     */
    public void goToNext(View view) {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    /**
     * Muestra el fragmento anterior.
     *
     * @param view el view que fue presionado.
     */
    public void goToPrev(View view) {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }

    public void changeTitle(int position) {
        // Sets the title for the current fragment
        String title = mAdapter.getCurrentFragment(position).getTitle();
        ((TextView) findViewById(R.id.sectionTitle)).setText(title);
    }

    /**
     * Instead of showing a normal menu it shows the slides.
     */
    @Override
    protected void showMenu() {
        pageCount = menu.getElements().size();
        setContentView(R.layout.activity_know);

        mAdapter = new KnowPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.knowViewPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new KnowPageChangeListener());

        progress = findViewById(R.id.progressBar);
        progressBarText = findViewById(R.id.progressBarText);

        nextButton = findViewById(R.id.nextArrow);
        prevButton = findViewById(R.id.prevArrow);

        changePage(1);
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
            KnowActivity.this.changeTitle(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    /**
     * Adaptador para la sección de conoce.
     *
     * @author Ludiverse
     * @author Innerlemonade
     */
    private class KnowPageAdapter extends FragmentStatePagerAdapter {
        private SparseArray<KnowFragment> currentFragments;

        KnowPageAdapter(FragmentManager manager) {
            super(manager);
            currentFragments = new SparseArray<>();
        }

        @Override
        public Fragment getItem(int i) {
            KnowFragment fragment = null;
            Page element = (Page) menu.getElements().get(i);
            switch (element.getTemplate()) {
                case "Números":
                    fragment = NumbersFragment.newInstance(element);
                    break;
                case "Números en dos Columnas":
                    fragment = TwoColumnsFragment.newInstance(element);
                    break;
                case "Texto":
                    fragment = TextFragment.newInstance(element);
                    break;
                case "Tabla":
                    fragment = TableFragment.newInstance(element);
                    break;
                case "Imagen":
                    fragment = ImageFragment.newInstance(element);
                    break;
            }
            currentFragments.put(i, fragment);
            return fragment;
        }

        private KnowFragment getCurrentFragment(int current) {
            return currentFragments.get(current);
        }

        /**
         * Elimina los fragmentos guardados cuando se destruye un item.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            currentFragments.delete(position);
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

}
