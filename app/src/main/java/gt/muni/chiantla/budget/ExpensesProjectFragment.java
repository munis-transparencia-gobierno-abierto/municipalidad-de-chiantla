package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.databinding.FragmentExpensesProjectBinding;

/**
 * Fragmento que muestra información de un proyecto.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesProjectFragment extends Fragment implements View.OnClickListener {
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
        FragmentExpensesProjectBinding binding = FragmentExpensesProjectBinding.inflate(inflater);
        View view = binding.getRoot();
        BudgetActivity activity = (BudgetActivity) getActivity();
        activity.setViewListeners(view);
        view.findViewById(R.id.projects_button).setOnClickListener(this);

        expense = Expense.getInstance();

        String text;
        if (strings[5].equals(Expense.KEY_WORK))
            text = getResources().getString(R.string.work);
        else
            text = getResources().getString(R.string.activity);
        ((TextView) activity.findViewById(R.id.text4)).setText(text);

        binding.setName(strings[1]);
        int[] ids = new int[]{R.id.amount, R.id.expense, R.id.percentage};
        String[] viewStrings = new String[]{strings[2], strings[3], strings[4] + "%"};
        Utils.setTexts(ids, viewStrings, view);
        ((ProgressBar) view
                .findViewById(R.id.progressExpensesBar))
                .setProgress((int) Double.parseDouble(strings[4]));

        ArrayList groups = expense.getGroupsByProject(projectId, acticityId, programId, project,
                strings[5]);
        HashMap objects = expense.getProjectInformation(projectId, acticityId,
                programId, groups, project, strings[5]);
        LinearLayout listContainer = view.findViewById(R.id.cardList);

        for (int i = 0; i < groups.size(); i++) {
            initGroup(inflater, listContainer, (String[]) groups.get(i));
            List groupData = (List) objects.get(i);
            for (int j = 0; j < groupData.size(); j++) {
                initElement(inflater, listContainer, (List) groupData.get(j));
            }
        }

        String category;
        if (project)
            category = "Administracion";
        else
            category = "Proyectos_Infraestructura";
        Utils.sendFirebaseEvent("Presupuesto", "Gastos", category, programName,
                programName, "ProyectoActividad" + acticityId, activity);

        activity.moveProgress(4);

        return view;
    }

    /**
     * Crea una view que es el header de un grupo de elementos
     *
     * @param inflater     el inflater
     * @param container    el contenedor del header
     * @param groupStrings los textos que se mostraran en ese header
     */
    private void initGroup(LayoutInflater inflater, LinearLayout container, String[] groupStrings) {
        View groupView = inflater.inflate(R.layout.section_budget_table_header, container, false);
        int[] ids = new int[]{R.id.name, R.id.value};
        String[] viewStrings = new String[]{groupStrings[1], groupStrings[2]};
        Utils.setTexts(ids, viewStrings, groupView);
        container.addView(groupView);
    }

    /**
     * Crea una view de un elemento. Los elementos pueden tener subelementos, los cuales son
     * inicializados.
     *
     * @param inflater  el inflater
     * @param container el contenedor del elemento
     * @param list      la lista de sub-elementos
     */
    private void initElement(LayoutInflater inflater, LinearLayout container,
                             List list) {
        View elementView = inflater.inflate(R.layout.section_budget_table_item, container, false);
        String[] strings = (String[]) list.get(0);
        int[] ids = new int[]{R.id.name, R.id.value};
        String[] viewStrings = new String[]{strings[1], strings[2]};
        Utils.setTexts(ids, viewStrings, elementView);
        ViewGroup group = elementView.findViewById(R.id.insertPoint);
        for (int i = 1; i < list.size(); i++) {
            strings = (String[]) list.get(i);
            View innerView = inflater.inflate(R.layout.section_budget_table_subheader, group,
                    false);
            viewStrings = new String[]{strings[1], strings[2]};
            Utils.setTexts(ids, viewStrings, innerView);
            group.addView(innerView);
        }
        container.addView(elementView);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        getActivity().onBackPressed();
    }
}
