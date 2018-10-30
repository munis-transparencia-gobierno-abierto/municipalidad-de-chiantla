package gt.muni.chiantla;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TutorialFragment.OnTutorialInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TutorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment extends DialogFragment implements View.OnClickListener {
    public static final int ARROW_TOP_LEFT = 0;
    public static final int ARROW_TOP_CENTER = 1;
    public static final int ARROW_TOP_RIGHT = 2;
    public static final int ARROW_BOTTOM_LEFT = 3;
    public static final int ARROW_BOTTOM_CENTER = 4;
    public static final int ARROW_BOTTOM_RIGHT = 5;
    public static final int NO_ARROW = 12;

    private static final String ARG_POSITION = "position";
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_ARROW_POSITION = "arrowPosition";

    private int[] position;
    private String title;
    private String content;
    private int arrowPosition;

    private OnTutorialInteractionListener mListener;

    public TutorialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TutorialFragment.
     */
    public static TutorialFragment newInstance(int[] position, String title, String content, int
            arrowPosition) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_POSITION, position);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        args.putInt(ARG_ARROW_POSITION, arrowPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getIntArray(ARG_POSITION);
            title = getArguments().getString(ARG_TITLE);
            content = getArguments().getString(ARG_CONTENT);
            arrowPosition = getArguments().getInt(ARG_ARROW_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        setDialog();
        moveTutorialArrow(view);
        TextView titleView = view.findViewById(R.id.title);
        if (title != null && !title.equals("")) titleView.setText(title);
        else titleView.setVisibility(View.GONE);
        TextView contentView = view.findViewById(R.id.content);
        if (content != null && !content.equals("")) contentView.setText(content);
        else contentView.setVisibility(View.GONE);
        view.setOnClickListener(this);
        view.findViewById(R.id.nextTutorialButton).setOnClickListener(this);
        view.post(new Runnable() {
            @Override
            public void run() {
                moveTutorialContainer(view);
            }
        });
        return view;
    }

    private void setDialog() {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    /**
     * Mueve la view de la flecha del tutorial a la posicion correcta
     *
     * @param view el contenedor de la flecha
     */
    private void moveTutorialArrow(View view) {
        View arrow = view.findViewById(R.id.tutorial_arrow);
        if (arrowPosition == NO_ARROW)
            arrow.setVisibility(View.GONE);
        else {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) arrow.getLayoutParams();
            // Vertical alignment
            if (arrowPosition == ARROW_BOTTOM_LEFT || arrowPosition == ARROW_BOTTOM_CENTER ||
                    arrowPosition == ARROW_BOTTOM_RIGHT)
                layoutParams.addRule(RelativeLayout.BELOW, R.id.tutorial_bubble);
            else if (arrowPosition == ARROW_TOP_LEFT || arrowPosition == ARROW_TOP_CENTER ||
                    arrowPosition == ARROW_TOP_RIGHT) {
                View bubble = view.findViewById(R.id.tutorial_bubble);
                RelativeLayout.LayoutParams bubbleParams =
                        (RelativeLayout.LayoutParams) bubble.getLayoutParams();
                bubbleParams.addRule(RelativeLayout.BELOW, R.id.tutorial_arrow);
                bubble.setLayoutParams(bubbleParams);
                arrow.setRotation(180);
            }
            // Horizontal alignment
            if (arrowPosition == ARROW_TOP_LEFT || arrowPosition == ARROW_BOTTOM_LEFT)
                layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.tutorial_bubble);
            else if (arrowPosition == ARROW_TOP_CENTER || arrowPosition == ARROW_BOTTOM_CENTER)
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            else if (arrowPosition == ARROW_TOP_RIGHT || arrowPosition == ARROW_BOTTOM_RIGHT)
                layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.tutorial_bubble);
            arrow.setLayoutParams(layoutParams);
        }
    }

    private void moveTutorialContainer(View view) {
        View container = view.findViewById(R.id.tutorial_container);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) container.getLayoutParams();
        if (arrowPosition == NO_ARROW)
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        else {
            View arrow = view.findViewById(R.id.tutorial_arrow);
            int extraWidth = (arrow.getWidth() / 2) + (int) Utils.dpToPx((Context) mListener, 16);
            int left = getLeftContainerPosition(extraWidth, container.getWidth());
            int top = getTopContainerPosition(container.getHeight());
            layoutParams.setMargins(left, top, 0, 0);
            moveTutorialNextButton(view, top + container.getHeight());
        }
        container.setLayoutParams(layoutParams);
    }

    private void moveTutorialNextButton(View view, int containerTopPosition) {
        View button = view.findViewById(R.id.nextTutorialButton);
        int[] buttonPosition = new int[2];
        button.getLocationInWindow(buttonPosition);
        // Extra = 20 padding (whe dont want the tutorial bubble too close to the button) + 30
        // statusbar
        int extra = (int) Utils.dpToPx((Context) mListener, 50);
        if (buttonPosition[1] <= containerTopPosition + extra) {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) button.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            button.setLayoutParams(layoutParams);
        }
    }

    private int getLeftContainerPosition(int extraWidth, int containerWidth) {
        if (arrowPosition == ARROW_TOP_LEFT || arrowPosition == ARROW_BOTTOM_LEFT)
            return position[0] - extraWidth;
        if (arrowPosition == ARROW_TOP_CENTER || arrowPosition == ARROW_BOTTOM_CENTER)
            return position[0] - (containerWidth / 2);
        if (arrowPosition == ARROW_TOP_RIGHT || arrowPosition == ARROW_BOTTOM_RIGHT)
            return position[0] - containerWidth + extraWidth;
        return 0;
    }

    private int getTopContainerPosition(int containerHeight) {
        if (arrowPosition == ARROW_BOTTOM_LEFT || arrowPosition == ARROW_BOTTOM_CENTER ||
                arrowPosition == ARROW_BOTTOM_RIGHT)
            return position[1] - containerHeight;
        if (arrowPosition == ARROW_TOP_LEFT || arrowPosition == ARROW_TOP_CENTER ||
                arrowPosition == ARROW_TOP_RIGHT)
            return position[1];
        return 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTutorialInteractionListener) {
            mListener = (OnTutorialInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTutorialInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v.getId() != R.id.tutorial_bubble)
                mListener.nextTutorial();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTutorialInteractionListener {
        void nextTutorial();
    }
}
