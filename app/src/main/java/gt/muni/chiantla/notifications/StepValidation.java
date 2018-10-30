package gt.muni.chiantla.notifications;

/**
 * Interface para la validacion de pasos de una nueva notificacion
 */
public interface StepValidation {
    boolean validate();

    void getData(NewNotificationActivity activity);
}
