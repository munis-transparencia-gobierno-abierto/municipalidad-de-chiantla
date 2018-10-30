package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Income;
import gt.muni.chiantla.databinding.FragmentIncomeSectionBinding;

/**
 * Fragmento que muestra ingresos auxiliares de una sección
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class IncomeSectionFragment extends Fragment implements View.OnClickListener {
    private long sectionId;
    private long classId;
    private String typeId;
    private String sectionName;
    private String className;
    private String sectionValue;
    private BudgetActivity activity;

    public static IncomeSectionFragment newInstance(String typeId, long classId, String className,
                                                    long sectionId, String sectionName,
                                                    String sectionValue) {
        IncomeSectionFragment instance = new IncomeSectionFragment();
        instance.typeId = typeId;
        instance.classId = classId;
        instance.className = className;
        instance.sectionId = sectionId;
        instance.sectionName = sectionName;
        instance.sectionValue = sectionValue;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (BudgetActivity) getActivity();

        FragmentIncomeSectionBinding binding = FragmentIncomeSectionBinding.inflate(inflater);
        View view = binding.getRoot();
        activity.setViewListeners(view);
        view.findViewById(R.id.projects_button).setOnClickListener(this);

        Income income = Income.getInstance();

        binding.setName(sectionName);
        TextView value = view.findViewById(R.id.amount);
        value.setText(sectionValue);

        LinearLayout listView = view.findViewById(R.id.cardList);

        View header = inflater.inflate(R.layout.section_budget_table_header, listView, false);
        TextView headerText = header.findViewById(R.id.name);
        headerText.setText(R.string.details);
        listView.addView(header);

        ArrayList<String[]> objects = income.getAuxBySection(sectionId, classId);
        for (int i = 0; i < objects.size(); i++) {
            initElement(inflater, listView, objects.get(i));
        }

        if (typeId != null)
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Ingresos_Propios", "Clase_" +
                            className,
                    "Secciones_Clase_" + sectionName, "Seccion" + sectionId, activity);
        else
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Transferencias_Gobierno",
                    "Clase_" + className,
                    "Recursos_Seccion_" + sectionName, "Seccion" + sectionId, activity);

        activity.moveProgress(3);

        return view;
    }

    /**
     * Inicializa un elemento
     *
     * @param inflater  el inflater
     * @param container el contenedor del elemento
     * @param strings   los textos del elemento
     */
    private void initElement(LayoutInflater inflater, LinearLayout container,
                             String[] strings) {
        View elementView = inflater.inflate(R.layout.section_budget_table_subheader, container,
                false);
        int[] ids = new int[]{R.id.name, R.id.value};
        String[] viewStrings = new String[]{strings[1], strings[2]};
        Utils.setTexts(ids, viewStrings, elementView);
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
