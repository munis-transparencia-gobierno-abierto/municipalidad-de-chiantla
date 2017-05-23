package gt.muni.chiantla.notifications;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import gt.muni.chiantla.R;

/**
 * Fragmento que se muestra mientras se obtiene la ubicación actual.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class LocationFragment extends DialogFragment  {
    LocationDialogInterface callback;

    public LocationFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (LocationDialogInterface) context;
        }
        catch (ClassCastException error) {
            throw new ClassCastException(context.toString() +
                    " must implement LocationDialogInterface");
        }

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_location, null));
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        callback.cancelLocationUpdates();
    }

    protected interface LocationDialogInterface {
        void cancelLocationUpdates();
    }

}
