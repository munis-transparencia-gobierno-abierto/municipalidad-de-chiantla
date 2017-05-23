package gt.muni.chiantla.mymuni.development;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Objective;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Actividad de los objetivos del plan de desarrollo
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ObjectivesActivity extends DevelopmentItemActivity implements AdapterView.OnItemClickListener {
    private Integer parentId;
    private String parentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.objectives, true);
        setContentView(R.layout.activity_development_item);

        parentId = getIntent().getIntExtra("parentId", 1);
        parentName = getIntent().getStringExtra("parentName");
        mListView = (CustomListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
        initScroll(mListView, findViewById(android.R.id.content));
        ((ProgressBar) findViewById(R.id.progressBar)).setProgress(67);
        ((TextView) findViewById(R.id.progressBarText)).setText("2/3");
        showTextInView(parentId);

        Utils.sendFirebaseEvent("MiChiantla", "Plan_de_Desarrollo", "Eje_y_Objetivos", null,
                "Menu_Eje" + parentId + "_" + parentName, "Eje" + parentId, this);
    }

    @Override
    public Class getItemClass() {
        return Objective.class;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, ProjectsActivity.class);
        Objective objective = (Objective) adapterView.getAdapter().getItem(position);
        intent.putExtra("parentId", objective.getId());
        intent.putExtra("axisId", parentId);
        intent.putExtra("parentName", objective.getName());
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }
}
