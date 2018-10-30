package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Income;
import gt.muni.chiantla.databinding.FragmentIncomeBinding;

/**
 * Fragmento de ingresos, muestra los totales dependiendo de si son ingresos de la municipalidad
 * o transferencias del gobierno central.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class IncomeFragment extends Fragment {
    private Income income;
    private BudgetActivity activity;

    public static IncomeFragment newInstance() {
        return new IncomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (BudgetActivity) getActivity();

        FragmentIncomeBinding binding = FragmentIncomeBinding.inflate(inflater);
        View view = binding.getRoot();

        income = Income.getInstance();

        initView(view.findViewById(R.id.own_income_button), income.getOwnIncome());
        initView(view.findViewById(R.id.other_income_button), income.getOtherIncome());

        Utils.sendFirebaseEvent("Presupuesto", "Ingresos", null, null,
                "Menu_Ingresos", "Menu_Ingresos", getActivity());

        activity.moveProgress(0);

        return view;
    }

    /**
     * Sets the text for the two cards (divided in own income and other income)
     *
     * @param rootView the card
     * @param amount   the main amount displayed in the card
     */
    private void initView(View rootView, double amount) {
        TextView amountView = rootView.findViewById(R.id.amount);
        amountView.setText(Utils.formatDouble(amount));

        rootView.setClickable(true);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.goToNext(v);
            }
        });

        activity.setViewListeners(rootView);
    }

    /**
     * Cuando este fragmento se muestre, cambiar el tema de la actividad.
     */
    @Override
    public void onResume() {
        super.onResume();
        activity.setTheme(R.style.IncomeTheme);
    }
}
