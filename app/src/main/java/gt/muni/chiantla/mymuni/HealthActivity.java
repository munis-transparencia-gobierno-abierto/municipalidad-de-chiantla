package gt.muni.chiantla.mymuni;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Actividad de la sección de centros de salud. Muestra información de contacto y luego una lista
 * de contactos con su información de contacto.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class HealthActivity extends ContactsActivity {
    /**
     * Los ids de las views en las que se colocarán los textos
     */
    private int[] VIEW_IDS = new int[]{
            R.id.schedule,
            R.id.mail,
            R.id.phone,
            R.id.head
    };
    private int PAGE_ID = 247;
    private int HEADER_ITEM_COUNT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        setCustomActionBar(R.string.health_centers_header, true);
        setContentView(R.layout.activity_health);

        CustomListView mListView = (CustomListView) findViewById(R.id.list);
        setmListView(mListView);
        initScroll(mListView, findViewById(android.R.id.content));
        mListView.setOnScrollListener(this);
        View view = getLayoutInflater().inflate(R.layout.section_health_header, null);
        mListView.addHeaderView(view, null, false);
        findViewById(R.id.head);
        showTextInView();
        mListView.setOnItemClickListener(this);

        Utils.sendFirebaseEvent("MiChiantla", "Centros_de_Salud", null, null,
                "Centros_de_Salud", "Centros_de_Salud", this);
    }

    public void callNumber(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        intent.setData(Uri.parse("tel:" + phone.getText()));
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public int[] getViewIds() {
        return VIEW_IDS;
    }

    @Override
    public int getPageId() {
        return PAGE_ID;
    }

    @Override
    public int getHeaderItemCount() {
        return HEADER_ITEM_COUNT;
    }

    @Override
    public boolean getShowHeaderIcon() {
        return true;
    }
}
