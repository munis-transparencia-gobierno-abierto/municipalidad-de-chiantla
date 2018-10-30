package gt.muni.chiantla.budget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.databinding.SectionBudgetItemBinding;

/**
 * Adaptador general para elementos del presupuesto.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class BudgetListAdapter extends BaseAdapter {
    private ArrayList<String[]> objects;
    private Context context;
    private int resId;
    private boolean expense;
    private String na;
    private int progressBackgroundColor;
    private int progressColor;
    private int cardBackgroundColor;
    private int subtitleSize;
    private int buttonRotation;
    private boolean buttonInverted;
    private boolean hasTopBorder;
    private int itemId;
    private boolean lightBackgroundColor;

    private SectionBudgetItemBinding binding;
    private LayoutInflater inflater;

    private BudgetListAdapter(Builder builder) {
        objects = builder.objects;
        context = builder.context;
        resId = builder.resId;
        expense = builder.expense;
        na = builder.na;
        progressBackgroundColor = builder.progressBackgroundColor;
        progressColor = builder.progressColor;
        cardBackgroundColor = builder.cardBackgroundColor;
        subtitleSize = builder.subtitleSize;
        buttonRotation = builder.buttonRotation;
        buttonInverted = builder.buttonInverted;
        hasTopBorder = builder.hasTopBorder;
        itemId = builder.itemId;
        lightBackgroundColor = builder.lightBackgroundColor;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public String[] getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(getItem(i)[0]);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String[] strings = getItem(i);
        if (inflater == null) {
            inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            binding = SectionBudgetItemBinding.inflate(inflater, viewGroup, false);
            setBindingVariables();
            view = binding.getRoot();
            view.setId(this.itemId);
            ((BudgetActivity) getContext()).setViewListeners(view);
        }
        binding.setName(strings[1]);
        if (expense) {
            int[] ids = new int[]{R.id.card_name, R.id.amount, R.id.expense, R.id.percentage};
            if (strings[1].toLowerCase().trim().equals("na"))
                strings[1] = na;
            String[] viewStrings = new String[]{strings[1], strings[2], strings[3], strings[4] +
                    "%"};
            Utils.setTexts(ids, viewStrings, view);
            ((ProgressBar) view.findViewById(R.id.progressExpensesBar))
                    .setProgress((int) Double.parseDouble(strings[4]));
        } else {
            Utils.setTexts(new int[]{R.id.card_name, R.id.amount}, new String[]{strings[1],
                    strings[2]}, view);
        }
        return view;
    }

    /**
     * Coloca las variables para el binding del view
     */
    private void setBindingVariables() {
        binding.setProgressBackgroundColor(this.progressBackgroundColor);
        binding.setProgressColor(this.progressColor);
        binding.setCardColor(this.cardBackgroundColor);
        binding.setSubtitleSize(this.subtitleSize);
        binding.setExpense(this.expense);
        binding.setButtonInverted(this.buttonInverted);
        binding.setHasTopBorder(this.hasTopBorder);
        binding.setButtonRotation(this.buttonRotation);
        binding.setLightBackgroundColor(this.lightBackgroundColor);
    }

    public Context getContext() {
        return context;
    }

    public int getResId() {
        return resId;
    }

    /**
     * El constructor del BudgetListAdapter. Crea un BudgetListAdapter con las propiedades deseadas.
     */
    public static class Builder {
        private ArrayList<String[]> objects;
        private Context context;
        private int resId;
        private boolean expense;
        private String na;
        private int progressBackgroundColor;
        private int progressColor;
        private int cardBackgroundColor;
        private int subtitleSize;
        private int buttonRotation;
        private boolean buttonInverted;
        private boolean hasTopBorder;
        private int itemId;
        private boolean lightBackgroundColor;

        public Builder setObjects(ArrayList<String[]> objects) {
            this.objects = objects;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setResId(int resId) {
            this.resId = resId;
            return this;
        }

        public Builder setExpense(boolean expense) {
            this.expense = expense;
            return this;
        }

        public Builder setNa(String na) {
            this.na = na;
            return this;
        }

        public Builder setProgressBackgroundColor(int progressBackgroundColor) {
            this.progressBackgroundColor = context.getResources().getColor(progressBackgroundColor);
            return this;
        }

        public Builder setProgressColor(int progressColor) {
            this.progressColor = context.getResources().getColor(progressColor);
            return this;
        }

        public Builder setCardBackgroundColor(int cardBackgroundColor) {
            this.cardBackgroundColor = context.getResources().getColor(cardBackgroundColor);
            return this;
        }

        public Builder setSubtitleSize(int subtitleSize) {
            this.subtitleSize = subtitleSize;
            return this;
        }

        public Builder setButtonRotation(int buttonRotation) {
            this.buttonRotation = buttonRotation;
            return this;
        }

        public Builder setButtonInverted(boolean buttonInverted) {
            this.buttonInverted = buttonInverted;
            return this;
        }

        public Builder setHasTopBorder(boolean hasTopBorder) {
            this.hasTopBorder = hasTopBorder;
            return this;
        }

        public Builder setItemId(int itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder setLightBackgroundColor(boolean lightBackgroundColor) {
            this.lightBackgroundColor = lightBackgroundColor;
            return this;
        }

        public BudgetListAdapter create() {
            return new BudgetListAdapter(this);
        }
    }
}
