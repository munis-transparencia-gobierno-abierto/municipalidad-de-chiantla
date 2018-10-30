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
 * Fragmento que muestra los programas de un proyecto o actividad.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesProgramFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Expense expense;
    private long programId;
    private boolean project;
    private String programName;
    private BudgetActivity activity;

    public static ExpensesProgramFragment newInstance(boolean project, long programId, String
            programName) {
        ExpensesProgramFragment instance = new ExpensesProgramFragment();
        instance.programId = programId;
        instance.project = project;
        instance.programName = programName;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_program, container, false);

        expense = Expense.getInstance();

        ListView listView = view.findViewById(R.id.list);
        ArrayList<String[]> objects = expense.getProjectsByProgram(programId, project);

        BudgetListAdapter adapter = new BudgetListAdapter.Builder()
                .setObjects(objects)
                .setContext(getContext())
                .setResId(R.layout.section_budget_item)
                .setExpense(true)
                .setSubtitleSize(18)
                .setItemId(R.id.projectButton)
                .setProgressBackgroundColor(R.color.white)
                .setProgressColor(R.color.backgroundBudgets)
                .setCardBackgroundColor(R.color.colorExpensesActivitiesPrimary)
                .create();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        Utils.sendFirebaseEvent("Presupuesto", "Gastos", "Administracion", programName,
                "ProyectosActividades_" + programName, "Programa" + programId, getActivity());

        activity = (BudgetActivity) getActivity();
        activity.moveProgress(2);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTheme(R.style.ExpensesTextInvertedTheme);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bundle bundle = new Bundle();
        String[] project = (String[]) adapterView.getAdapter().getItem(position);
        bundle.putLong("projectId", Long.parseLong(project[0]));
        if (this.project)
            bundle.putString("projectName", project[1]);
        else
            bundle.putStringArray("project", project);
        activity.goToNext(bundle, view);
    }
}
