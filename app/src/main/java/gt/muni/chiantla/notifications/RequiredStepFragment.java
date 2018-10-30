package gt.muni.chiantla.notifications;

import android.support.v4.app.Fragment;

/**
 * Clase para un paso que es requerido
 */
public abstract class RequiredStepFragment extends Fragment implements StepValidation {
    protected boolean IS_REQUIRED = true;
}
