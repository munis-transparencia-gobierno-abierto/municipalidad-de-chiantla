package gt.muni.chiantla.mymuni;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;
import gt.muni.chiantla.widget.CustomExpandableListView;

/**
 * Actividad de la sección de ley de acceso a la información. Muestra las secciónes de la ley
 * y cuando se presiona una se muestra el contenido de la misma. La información de las secciones
 * se encuentra dentro del un {@link android.widget.ExpandableListView}
 * @author Ludiverse
 * @author Innerlemonade
 */
public class LawsActivity extends RestConnectionActivity {
    private CustomExpandableListView mListView;
    private int PAGE_ID = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.law_title, true);
        setContentView(R.layout.activity_laws);

        mListView = (CustomExpandableListView) findViewById(R.id.lawsListView);
        initScroll(mListView, findViewById(android.R.id.content));
        mListView.setOnScrollListener(this);
        View view = getLayoutInflater().inflate(R.layout.section_laws_header, null);
        mListView.addHeaderView(view, null, false);

        showTextInView();
        
        Utils.sendFirebaseEvent("MiChiantla", "Ley_de_Acceso_a_la_Info_Publica", null, null,
                "Ley_de_Acceso_a_la_Info_Publica", "Ley_de_Acceso_a_la_Info_Publica", this);

    }

    /**
     * Cambia la posición del ícono de expandir del {@link android.widget.ExpandableListView} .
     * Lo muestra del lado derecho.
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        double padding = Utils.dpToPx(this, 18);
        double width = mListView.getWidth() - padding;
        double indicatorWidth = Utils.dpToPx(this, 14);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mListView.setIndicatorBounds((int) (width - indicatorWidth),(int) width);
        }
        else {
            mListView.setIndicatorBoundsRelative((int) (width - indicatorWidth),(int) width);
        }
    }

    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        Page page = new Page(response);
        page.save(db);
        db.addOrUpdateUpdate("pages", "Page", PAGE_ID, true);
        showItems(page);
    }

    private void showItems(Page page) {
        ArrayList<PageItem> items = page.getItems();
        ArrayList<String> headerList =  new ArrayList<String>();
        ArrayList<String> childData = new ArrayList<String>();
        for (int i = 0; i <= items.size() - 1; i++){
            headerList.add(items.get(i).getName());
            childData.add(items.get(i).getContent());
        }
        mListView.setAdapter(new ExpandableListAdapter(headerList, childData, this));
    }

    protected void showTextInView() {
        Integer pageId = PAGE_ID;
        if (!db.isUpdated("pages", "Page", pageId)) {
            paths = new String[]{"page/" + pageId.toString() + "/items/"};
            connect();
        } else {
            Page page = new Page(db, pageId);
            showItems(page);
        }
    }

}
