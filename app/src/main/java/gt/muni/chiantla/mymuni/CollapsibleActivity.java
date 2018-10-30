package gt.muni.chiantla.mymuni;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;

/**
 * Actividad que muestra un acordion
 */
public class CollapsibleActivity extends CustomActivity {
    protected ExpandableListView mListView;
    private Page page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_collapsible);

        mListView = findViewById(R.id.lawsListView);
        page = (Page) getIntent().getSerializableExtra("page");
        createOptionsMenu = true;
        init();
    }

    protected void init() {
        ((TextView) findViewById(R.id.sectionTitle)).setText(page.getName());
        showTextInView();

        Utils.sendFirebaseEvent("MiChiantla", "Ley_de_Acceso_a_la_Info_Publica", null, null,
                "Ley_de_Acceso_a_la_Info_Publica", "Ley_de_Acceso_a_la_Info_Publica", this);
    }

    protected void showItems() {
        List<PageItem> items = page.getItems();
        ArrayList<String> headerList = new ArrayList<String>();
        ArrayList<String> childData = new ArrayList<String>();
        for (int i = 0; i <= items.size() - 1; i++) {
            headerList.add(items.get(i).getName());
            childData.add(Utils.fromHtml(items.get(i).getContent()).toString());
        }
        mListView.setAdapter(getAdapter(headerList, childData));
    }

    protected void showTextInView() {
        showItems();
    }

    protected ExpandableListAdapter getAdapter(List headers, List data) {
        return new ExpandableListAdapter(headers, data, this, true);
    }

}
