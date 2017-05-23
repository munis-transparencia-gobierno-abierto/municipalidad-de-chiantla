package gt.muni.chiantla;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import gt.muni.chiantla.widget.CustomExpandableListView;
import gt.muni.chiantla.widget.CustomListView;
import gt.muni.chiantla.widget.CustomNestedScrollView;
import gt.muni.chiantla.widget.CustomOnScrollChangeListener;
import gt.muni.chiantla.widget.CustomScrollableView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Actividad base que tiene los métodos generales para todas las aplicaciónes del app.
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class CustomActivity extends AppCompatActivity implements
        InformationFragment.InformationInterface,
        CustomOnScrollChangeListener,
        NestedScrollView.OnScrollChangeListener,
        AbsListView.OnScrollListener {
    protected InformationFragment informationFragment;
    protected FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Para tener distintos tipos de font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Poppins-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
                               int oldScrollY) {
        onScrollChange((CustomScrollableView) v, scrollX, scrollY, oldScrollX, oldScrollY);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        hideListScroll((View) v.getParent(), v);
    }

    @Override
    public void onScrollChange(CustomScrollableView v, int scrollX, int scrollY, int oldscrollX,
                               int oldscrollY) {

        View scrollParent = (View) v.getParent();
        View tempButton = scrollParent.findViewById(R.id.imageButton3);
        ImageView tempGradientView = (ImageView) scrollParent.findViewById(R.id.imageView10);
        View firstChild = v.getChildAt(0);
        boolean isButtonVisible = false;
        boolean isGradientVisible = false;
        int contentHeight;

        // Muestra u oculta el indicador para scrollear dependiendo de la posición del scroll
        // con respecto al final del scroll
        if (firstChild != null) {

            scrollY = Utils.pxToDp(this, scrollY);
            contentHeight = Utils.pxToDp(this, firstChild.getMeasuredHeight());
            int contentShownHeight = Utils.pxToDp(this, v.getMeasuredHeight());
            int btnPosition = contentHeight - 50 - contentShownHeight;

            if (tempButton.getVisibility() == View.VISIBLE)
                isButtonVisible = true;
            if(tempGradientView != null && tempGradientView.getVisibility() == View.VISIBLE)
                isGradientVisible = true;

            if (isButtonVisible && scrollY + 10 >= ( btnPosition)) {
                tempButton.setVisibility(View.INVISIBLE);
                tempButton.setPadding(0, 0, 0, 0);
            } else if (!isButtonVisible && scrollY + 10 < (btnPosition)) {
                tempButton.setPadding(0, Utils.pxToDp(this, 10), 0, Utils.pxToDp(this, 20));
                tempButton.setVisibility(View.VISIBLE);
                tempButton.invalidate();
            }

            if (tempGradientView != null)
                if (isGradientVisible && scrollY + 10 >= (btnPosition)) {
                    tempGradientView.setVisibility(View.INVISIBLE);
                    tempGradientView.invalidate();
                } else if (!isGradientVisible && scrollY + 10 < (btnPosition)) {
                    tempGradientView.setVisibility(View.VISIBLE);
                }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // para tener distintos tipos de font
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setScrollBarListener(CustomScrollableView scrollBar) {
        scrollBar.setOnScrollChangeListener(this);
    }

    private void checkScrollHeight(final View view, final CustomScrollableView scrollBar) {
        final View scrollView = (View) scrollBar;
        ViewTreeObserver observer = scrollView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (scrollBar instanceof ListView) {
                    hideListScroll(view, (ListView) scrollBar);
                } else {
                    int viewHeight = Utils.pxToDp(CustomActivity.this, scrollView.getMeasuredHeight());
                    int contentHeight = scrollBar.getChildAt(0).getHeight();
                    contentHeight = Utils.pxToDp(CustomActivity.this, contentHeight);
                    boolean hide = (viewHeight - contentHeight >= 0);
                    toggleScroll(view, hide, false);
                }
            }
        });
    }

    private void hideListScroll(View view, AbsListView listView) {
        int lastPosition = listView.getLastVisiblePosition();
        int shown = lastPosition - listView.getFirstVisiblePosition();
        View lastChild = listView.getChildAt(shown);
        boolean hide = true;
        if (lastChild != null) {
            double threshold = Utils.dpToPx(this, 50);
            hide = lastPosition == listView.getAdapter().getCount() - 1 &&
                    (lastChild.getHeight() < threshold ||
                            lastChild.getBottom() - listView.getBottom() < threshold);
        }
        toggleScroll(view, hide, true);
    }

    private void toggleScroll(View view, boolean hide, boolean canShow) {
        View tempButton = view.findViewById(R.id.imageButton3);
        View tempGradientView = view.findViewById(R.id.imageView10);
        if (hide) {
            if (tempButton.getVisibility() == View.VISIBLE)
                tempButton.setVisibility(View.INVISIBLE);
            if (tempGradientView != null && tempGradientView.getVisibility() == View.VISIBLE)
                tempGradientView.setVisibility(View.INVISIBLE);
        } else if (canShow) {
            if (tempGradientView != null && tempGradientView.getVisibility() == View.INVISIBLE) {
                tempGradientView.setVisibility(View.VISIBLE);
                tempGradientView.bringToFront();
            }
            if (tempButton.getVisibility() == View.INVISIBLE) {
                tempButton.setVisibility(View.VISIBLE);
                tempButton.bringToFront();
            }
        }
    }

    /**
     * Coloca una Action Bar customizada para que utilice la font que utiliza en el resto
     * de la aplicación.
     * @param titleResource el id del recurso que se colocará en el título
     * @param backButton si tiene el botón para regresar
     */
    protected void setCustomActionBar(int titleResource, boolean backButton) {
        final ActionBar actionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(getString(titleResource));
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(backButton);
        getSupportActionBar().setElevation(0);
    }

    public void initScroll(CustomNestedScrollView scrollBar, View view) {
        setScrollBarListener(scrollBar);
        checkScrollHeight(view, scrollBar);
        setScrollButtonListener(scrollBar, view);
    }

    public void initScroll(CustomListView scrollBar, View view) {
        initListView(scrollBar, view);
        setScrollButtonListener(scrollBar, view);
    }

    public void initScroll(CustomExpandableListView scrollBar, View view) {
        initListView(scrollBar, view);
        setScrollButtonListener(scrollBar, view);
    }

    private void initListView(ListView scrollBar, View view) {
        scrollBar.setOnScrollListener(this);
        checkScrollHeight(view, (CustomScrollableView) scrollBar);
    }

    /**
     * Listener que permite hacer scroll al precionar el botón de scroll
     * @param scroll el elemento al que se hara scroll
     * @param view la vista que fue presionada
     */
    private void setScrollButtonListener(final CustomScrollableView scroll, View view) {
        View button = view.findViewById(R.id.imageButton3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll.smoothScrollTo(
                        scroll.getScrollX(),
                        (int) (scroll.getScrollY() + Utils.dpToPx(CustomActivity.this, 200))
                );
            }
        });

    }

    /**
     * Listener para un botón de cerrar. En caso sea el botón de cerrar del fragmento de información
     * cierra el fragmento en lugar de terminar la actividad.
     * @param v la view presionada
     */
    public void onCloseClick(View v) {
        if ((v.getId() == R.id.close_fragment) || v.getId() == R.id.button5) {
            informationFragment.dismiss();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
}
