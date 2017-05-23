package gt.muni.chiantla.mymuni.development;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Axis;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Actividad de los ejes del plan de desarrollo
 * @author Ludiverse
 * @author Innerlemonade
 */
public class AxesActivity extends DevelopmentItemActivity implements AdapterView.OnItemClickListener {
    private int PARENT_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.axes, true);
        setContentView(R.layout.activity_development_item);

        ((ProgressBar) findViewById(R.id.progressBar)).setProgress(34);
        ((TextView) findViewById(R.id.progressBarText)).setText("1/3");

        mListView = (CustomListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(this);

        initScroll(mListView, findViewById(android.R.id.content));
        showTextInView(PARENT_ID);

        Utils.sendFirebaseEvent("MiChiantla", "Plan_de_Desarrollo", null, null,
                "Menu_Ejes_Plan_de_Desarrollo", "Plan_de_Desarrollo", this);
    }

    @Override
    public Class getItemClass() {
        return Axis.class;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, ObjectivesActivity.class);
        Axis axis = (Axis) adapterView.getAdapter().getItem(position);
        intent.putExtra("parentId", axis.getId());
        intent.putExtra("parentName", axis.getName());
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }
}
