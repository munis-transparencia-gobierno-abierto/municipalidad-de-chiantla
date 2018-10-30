package gt.muni.chiantla.budget;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Expense;

/**
 * Fragmento para las actividades de los gastos.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesActivitiesFragment extends Fragment implements AdapterView
        .OnItemClickListener {
    boolean project;
    private Expense expense;
    private long programId;
    private String projectName;
    private long projectId;
    private BudgetActivity activity;

    public static ExpensesActivitiesFragment newInstance(boolean project, long programId,
                                                         long projectId, String projectName) {
        ExpensesActivitiesFragment instance = new ExpensesActivitiesFragment();
        instance.programId = programId;
        instance.projectId = projectId;
        instance.projectName = projectName;
        instance.project = project;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_activities, container, false);

        expense = Expense.getInstance();

        ListView listView = view.findViewById(R.id.list);
        ArrayList<String[]> objects;
        if (project)
            objects = expense.getActivitiesByProject(projectId, programId);
        else
            objects = expense.getProjectsByProgram(programId, false);

        BudgetListAdapter adapter = new BudgetListAdapter.Builder()
                .setObjects(objects)
                .setContext(getContext())
                .setResId(R.layout.section_budget_item)
                .setExpense(true)
                .setSubtitleSize(18)
                .setItemId(R.id.activityButton)
                .setNa(projectName)
                .setProgressBackgroundColor(R.color.backgroundBudgets)
                .setProgressColor(R.color.colorExpensesPrimary)
                .setCardBackgroundColor(R.color.white)
                .setHasTopBorder(true)
                .setButtonRotation(90)
                .setLightBackgroundColor(true)
                .create();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        String category;
        if (project)
            category = "Administracion";
        else
            category = "Proyectos_Infraestructura";
        Utils.sendFirebaseEvent("Presupuesto", "Gastos", category, projectName,
                "Actividades_" + projectName, "Proyecto" + programId, getActivity());

        activity = (BudgetActivity) getActivity();
        activity.moveProgress(3);

        return view;
    }

    /**
     * Cambiar el tema de la actividad cuando se muestre este fragmento.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!project) activity.setTheme(R.style.ExpensesTextInvertedTheme);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bundle bundle = new Bundle();
        String[] activity = (String[]) adapterView.getAdapter().getItem(position);
        bundle.putLong("activityId", Long.parseLong(activity[0]));
        bundle.putStringArray("activity", activity);
        this.activity.goToNext(bundle, view);
    }
}
