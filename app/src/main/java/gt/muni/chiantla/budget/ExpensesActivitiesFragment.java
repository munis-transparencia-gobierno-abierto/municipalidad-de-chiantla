package gt.muni.chiantla.budget;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Fragmento para las actividades de los gastos.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesActivitiesFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Expense expense;
    private long programId;
    private String projectName;
    private long projectId;

    public static ExpensesActivitiesFragment newInstance(long programId,
                                                         long projectId, String projectName) {
        ExpensesActivitiesFragment instance = new ExpensesActivitiesFragment();
        instance.programId = programId;
        instance.projectId = projectId;
        instance.projectName = projectName;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_activities, container, false);

        expense = Expense.getInstance();

        CustomListView listView = (CustomListView) view.findViewById(R.id.list);
        ArrayList<String[]> objects = expense.getActivitiesByProject(projectId, programId);
        listView.setAdapter(new BudgetExpenseAdapter(objects, getContext(),
                R.layout.section_expenses_activity, false, projectName));
        listView.setOnItemClickListener(this);
        ((CustomActivity) getActivity()).initScroll(listView, view);

        String category = "Administracion";
        Utils.sendFirebaseEvent("Presupuesto", "Gastos", category, projectName,
                "Actividades_" + projectName, "Proyecto" + programId, getActivity());

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bundle bundle = new Bundle();
        String[] activity = (String[]) adapterView.getAdapter().getItem(position);
        bundle.putLong("activityId", Long.parseLong(activity[0]));
        bundle.putStringArray("activity", activity);
        ((BudgetActivity) getActivity()).goToNext(bundle, view);
    }
}
