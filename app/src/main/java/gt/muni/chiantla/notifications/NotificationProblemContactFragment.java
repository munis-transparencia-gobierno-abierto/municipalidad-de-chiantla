package gt.muni.chiantla.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gt.muni.chiantla.R;

/**
 * Fragmento para la informacion de contacto de una nueva notificacion
 */
public class NotificationProblemContactFragment extends NotRequiredStepFragment {
    private TextView name;
    private TextView phone;
    private TextView email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_problem_contact, container,
                false);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        return view;
    }

    /**
     * Valida si los datos requeridos fueron llenados
     *
     * @return si los datos requeridos fueron llenados
     */
    public boolean validate() {
        boolean isNameFilled = name.getText() != null && !name.getText().equals("");
        boolean isPhoneFilled = phone.getText() != null && !phone.getText().equals("");
        boolean isEmailFilled = email.getText() != null && !email.getText().equals("");
        return !IS_REQUIRED || isEmailFilled && isNameFilled && isPhoneFilled;
    }

    /**
     * Le envia a la actividad la informacion llenada
     *
     * @param activity la actividad en donde se colocan los datos
     */
    public void getData(NewNotificationActivity activity) {
        activity.setName(name.getText().toString());
        activity.setPhone(phone.getText().toString());
        activity.setEmail(email.getText().toString());
    }
}
