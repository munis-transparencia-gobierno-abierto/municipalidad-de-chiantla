package gt.muni.chiantla.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import gt.muni.chiantla.R;

/**
 * Framento que permite que se seleccione el tipo de problema para una nueva notificacion
 */
public class NotificationProblemTypeFragment extends RequiredStepFragment {
    private RadioGroup problemTypeRadio;
    private int checkedId;
    private boolean budgetNotification;

    public static NotificationProblemTypeFragment createFragment(boolean budgetNotification) {
        NotificationProblemTypeFragment instance = new NotificationProblemTypeFragment();
        instance.budgetNotification = budgetNotification;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_problem_type, container, false);
        problemTypeRadio = view.findViewById(R.id.radioGroup);
        if (budgetNotification) {
            RadioButton button = view.findViewById(R.id.problem_type_budget);
            button.setVisibility(View.VISIBLE);
            button.toggle();
        }
        return view;
    }

    /**
     * Valida si los datos requeridos fueron llenados
     *
     * @return si los datos requeridos fueron llenados
     */
    public boolean validate() {
        if (IS_REQUIRED) {
            checkedId = problemTypeRadio.getCheckedRadioButtonId();
            return checkedId > 0;
        }
        return true;
    }

    /**
     * Le envia a la actividad la informacion llenada
     *
     * @param activity la actividad en donde se colocan los datos
     */
    public void getData(NewNotificationActivity activity) {
        RadioButton checkedView = (getView() != null) ?
                (RadioButton) getView().findViewById(checkedId) :
                null;
        if (checkedView != null) {
            activity.setProblemType(checkedView.getText().toString());
        }
    }

}
