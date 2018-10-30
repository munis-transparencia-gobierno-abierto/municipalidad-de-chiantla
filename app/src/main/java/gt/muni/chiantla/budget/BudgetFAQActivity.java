package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ExpandableListView;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;
import java.util.List;

import gt.muni.chiantla.AppDatabase;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;
import gt.muni.chiantla.mymuni.ExpandableListAdapter;

/**
 * Actividad de la sección de FAQ para el presupuesto.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class BudgetFAQActivity extends RestConnectionActivity {
    protected ExpandableListView mListView;
    private int PAGE_ID = 248;
    private Page page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_collapsible);
        createOptionsMenu = true;

        mListView = findViewById(R.id.lawsListView);
        showTextInView();
        findViewById(R.id.sectionTitle).setVisibility(View.GONE);

        Utils.sendFirebaseEvent("Presupuesto", "FAQ", null, null,
                "FAQ", "FAQ", this);
    }

    /**
     * Revisa si hay actualizaciones, y descarga la nueva data, antes de mostrar la informacion
     */
    protected void showTextInView() {
        if (!db.isUpdated("pages", "Page", PAGE_ID)) {
            paths = new String[]{"page/" + PAGE_ID + "/items/"};
            connect();
        } else {
            page = Page.getFromDatabase(AppDatabase.getInstance(this), PAGE_ID);
            showItems(page);
        }
    }

    /**
     * Devuelve el adaptador
     *
     * @param headers el contenido para los headers del acordion
     * @param data    la informacion que se muestra cuando los acordiones se abren
     * @return el adaptador
     */
    protected ExpandableListAdapter getAdapter(List headers, List data) {
        return new ExpandableListAdapter(headers, data, this, false);
    }

    /**
     * Metodo que guarda la informacion que el servidor devolvio, e indica que ya fue actualizada
     * la pagina actual.
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        page = new Page(response);
        page.save(AppDatabase.getInstance(this));
        db.addOrUpdateUpdate("pages", "Page", page.getId(), true);
        showItems(page);
    }

    /**
     * Muestra los items de la pagina en un acordion
     *
     * @param page la pagina actual
     */
    protected void showItems(Page page) {
        List<PageItem> items = page.getItems();
        ArrayList<String> headerList = new ArrayList<>();
        ArrayList<String> childData = new ArrayList<>();
        SparseIntArray positions = new SparseIntArray();
        String name = null;
        if (getIntent().getExtras() != null)
            name = getIntent().getExtras().getString("name");
        Integer initialFocus = null;
        for (int i = 0; i <= items.size() - 1; i++) {
            PageItem pageItem = items.get(i);
            if (!pageItem.getName().startsWith("budget_item")) {
                headerList.add(pageItem.getName());
                childData.add(pageItem.getContent());
                positions.append(pageItem.getId(), headerList.size() - 1);
            } else if (name != null && pageItem.getContent() != null &&
                    pageItem.getContent().toLowerCase().equals(name.toLowerCase())) {
                initialFocus = Integer
                        .parseInt(pageItem.getName().substring("budget_item".length()));
            }
        }
        mListView.setAdapter(getAdapter(headerList, childData));
        if (initialFocus != null) mListView.expandGroup(positions.get(initialFocus));
    }
}
