package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Income;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Fragmento que muestra ingresos auxiliares de una sección
 * @author Ludiverse
 * @author Innerlemonade
 */
public class IncomeSectionFragment extends Fragment {
    private long sectionId;
    private long classId;
    private long typeId;
    private String sectionName;
    private String className;
    private String sectionValue;

    public static IncomeSectionFragment newInstance(long typeId, long classId, String className,
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
        View view = inflater.inflate(R.layout.fragment_income_section, container, false);
        TextView name = (TextView) view.findViewById(R.id.sectionName);
        name.setText(sectionName);
        TextView value = (TextView) view.findViewById(R.id.sectionValue);
        value.setText(sectionValue);
        CustomListView listView = (CustomListView) view.findViewById(R.id.list);
        Income income = Income.getInstance();
        ArrayList<String[]> objects = income.getAuxBySection(sectionId, classId);
        listView.setAdapter(new BudgetListAdapter(objects, getContext(), R.layout.section_income_item));
        ((CustomActivity) getActivity()).initScroll(listView, view);

        if (typeId != 0)
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Ingresos_Propios", "Clase_" + className,
                    "Secciones_Clase_" + sectionName, "Seccion" + sectionId, getActivity());
        else
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Transferencias_Gobierno", "Clase_" + className,
                    "Recursos_Seccion_" + sectionName, "Seccion" + sectionId, getActivity());
        return view;
    }
}
