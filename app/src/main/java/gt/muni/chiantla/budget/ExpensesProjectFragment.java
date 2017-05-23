package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.widget.CustomExpandableListView;

/**
 * Fragmento que muestra información de un proyecto.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesProjectFragment extends Fragment {
    String programName;
    private Expense expense;
    private boolean project;
    private long programId;
    private long projectId;
    private long acticityId;
    private String[] strings;

    public static ExpensesProjectFragment newInstance(boolean project, long activityId,
                                                      long programId, String programName,
                                                      String[] strings) {
        ExpensesProjectFragment instance = new ExpensesProjectFragment();
        instance.strings = strings;
        instance.programId = programId;
        instance.acticityId = activityId;
        instance.project = project;
        instance.programName = programName;
        return instance;
    }

    public static ExpensesProjectFragment newInstance(boolean project, long activityId,
                                                      long projectId, long programId,
                                                      String programName, String[] strings) {
        ExpensesProjectFragment instance = new ExpensesProjectFragment();
        instance.strings = strings;
        instance.programId = programId;
        instance.projectId = projectId;
        instance.acticityId = activityId;
        instance.project = project;
        instance.programName = programName;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_project, container, false);

        expense = Expense.getInstance();

        int[] ids = new int[]{R.id.name, R.id.value, R.id.expense, R.id.percetage};
        String[] viewStrings = new String[]{strings[1], strings[2], strings[3], strings[4] + "%"};
        Utils.setTexts(ids, viewStrings, view);
        ((ProgressBar) view.findViewById(R.id.progressExpensesBar)).setProgress((int) Double.parseDouble(strings[4]));

        CustomExpandableListView listView = (CustomExpandableListView) view.findViewById(R.id.list);
        ArrayList groups = expense.getGroupsByProject(projectId, acticityId, programId, project);
        HashMap objects = expense.getProjectInformation(projectId, acticityId, programId, groups, project);
        listView.setAdapter(new ProjectsAdapter(getContext(), groups, objects));
        ((CustomActivity) getActivity()).initScroll(listView, view);

        String category;
        if (project)
            category = "Administracion";
        else
            category = "Proyectos_Infraestructura";
        Utils.sendFirebaseEvent("Presupuesto", "Gastos", category, programName,
                programName, "ProyectoActividad" + acticityId, getActivity());
        return view;
    }
}
