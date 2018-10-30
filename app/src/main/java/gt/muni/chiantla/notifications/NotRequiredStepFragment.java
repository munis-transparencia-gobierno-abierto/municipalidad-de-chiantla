package gt.muni.chiantla.notifications;

import android.support.v4.app.Fragment;

/**
 * Clase para un paso que no es requerido
 */
public abstract class NotRequiredStepFragment extends Fragment implements StepValidation {
    protected boolean IS_REQUIRED = false;
}
