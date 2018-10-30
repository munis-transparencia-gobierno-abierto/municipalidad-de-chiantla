package gt.muni.chiantla.mymuni.development;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.DevelopmentItem;
import gt.muni.chiantla.content.Project;

/**
 * Actividad de los proyectos del plan de desarrollo
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ProjectsActivity extends DevelopmentItemActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_development_item);

        ((TextView) findViewById(R.id.sectionTitle)).setText(R.string.projects);

        int parentId = getIntent().getIntExtra("parentId", 1);
        mListView = findViewById(R.id.list);
        ((ProgressBar) findViewById(R.id.progressBar)).setProgress(100);
        ((TextView) findViewById(R.id.progressBarText)).setText("3/3");
        showTextInView(parentId);

        int axisId = getIntent().getIntExtra("axisId", 1);
        String parentName = getIntent().getStringExtra("parentName");
        Utils.sendFirebaseEvent("MiChiantla", "Plan_de_Desarrollo", "Objetivo_y_proyectos", null,
                "Eje" + axisId + "_ProyectosObjetivo" + parentId + "_" + parentName, "Objetivo" +
                        parentId, this);
    }

    @Override
    public Class getItemClass() {
        return Project.class;
    }

    @Override
    protected void setAdapter(ArrayList<DevelopmentItem> items) {
        mListView.setAdapter(new ListProjectAdapter(this, items));
    }

    protected int getCardColor() {
        return getResources().getColor(R.color.black);
    }

    protected boolean getButtonInverted() {
        return true;
    }
}
