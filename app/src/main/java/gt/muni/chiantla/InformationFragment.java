package gt.muni.chiantla;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Fragmento que muestra información de una sección.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class InformationFragment extends DialogFragment {
    private InformationInterface mListener;
    private int titleId;
    private int contentId;

    public static InformationFragment newInstance(int titleId, int contentId) {
        InformationFragment instance = new InformationFragment();
        instance.titleId = titleId;
        instance.contentId = contentId;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        ((TextView) view.findViewById(R.id.title)).setText(titleId);
        ((TextView) view.findViewById(R.id.content)).setText(contentId);
        view.findViewById(R.id.close_fragment).setOnClickListener(new onCloseClickListener());

        CustomActivity activity = (CustomActivity) getActivity();
        CustomNestedScrollView scrollView = (CustomNestedScrollView) view.findViewById(R.id.scrollableInfo);
        activity.initScroll(scrollView, view);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InformationInterface) {
            mListener = (InformationInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface InformationInterface {
        void onCloseClick(View v);
    }

    public class onCloseClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mListener.onCloseClick(view);
        }
    }
}
