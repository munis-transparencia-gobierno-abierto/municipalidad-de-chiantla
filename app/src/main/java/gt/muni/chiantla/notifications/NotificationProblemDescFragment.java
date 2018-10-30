package gt.muni.chiantla.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import gt.muni.chiantla.R;

/**
 * Fragmento para la descripcion del problema de una nueva notificacion
 */
public class NotificationProblemDescFragment extends RequiredStepFragment {
    private EditText problemDesc;
    private String description;

    public static NotificationProblemDescFragment createFragment(String description) {
        NotificationProblemDescFragment instance = new NotificationProblemDescFragment();
        instance.description = description;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_problem_desc, container, false);
        problemDesc = view.findViewById(R.id.problem_desc);
        if (description != null) problemDesc.setText(description);
        return view;
    }

    /**
     * Valida si los datos requeridos fueron llenados
     *
     * @return si los datos requeridos fueron llenados
     */
    public boolean validate() {
        return (!IS_REQUIRED ||
                problemDesc.getText() != null && !problemDesc.getText().toString().equals("")
        );
    }

    /**
     * Le envia a la actividad la informacion llenada
     *
     * @param activity la actividad en donde se colocan los datos
     */
    public void getData(NewNotificationActivity activity) {
        activity.setProblem(problemDesc.getText().toString());
    }
}
