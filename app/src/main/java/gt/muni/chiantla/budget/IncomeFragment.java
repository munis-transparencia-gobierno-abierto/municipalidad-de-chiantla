package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Income;
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Fragmento de ingresos, muestra los totales dependiendo de si son ingresos de la municipalidad
 * o transferencias del gobierno central.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class IncomeFragment extends Fragment {
    private Income income;

    public static IncomeFragment newInstance() {
        return new IncomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        income = Income.getInstance();

        TextView ownIncome = (TextView) view.findViewById(R.id.own_income);
        TextView otherIncome = (TextView) view.findViewById(R.id.other_income);
        ownIncome.setText(Utils.formatDouble(income.getOwnIncome()));
        otherIncome.setText(Utils.formatDouble(income.getOtherIncome()));

        Utils.sendFirebaseEvent("Presupuesto", "Ingresos", null, null,
                "Menu_Ingresos", "Menu_Ingresos", getActivity());

        CustomNestedScrollView scroll = (CustomNestedScrollView) view.findViewById(R.id.scrollableInfo);
        ((CustomActivity) getActivity()).initScroll(scroll, view);

        return view;
    }
}
