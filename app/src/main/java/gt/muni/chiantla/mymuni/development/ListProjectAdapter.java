package gt.muni.chiantla.mymuni.development;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.DevelopmentItem;
import gt.muni.chiantla.content.Project;

/**
 * Adaptador para los proyectos de desarrollo
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ListProjectAdapter extends ListAdapter {
    private final int[] ids = new int[]{
            R.id.numbering,
            R.id.content,
            R.id.location
    };

    public ListProjectAdapter(Context context, ArrayList<DevelopmentItem> objects) {
        super(context, objects, 0, false);
    }

    /**
     * Muestra un proyecto. Para cada proyecto muestra el estado correcto.
     *
     * @see android.widget.BaseAdapter
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Project current = (Project) getItem(i);
        String currentNumbering = getCurrentNumbering(current, i);
        String name = current.getName();
        String location = current.getLocation();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.section_development_project, null);
        }
        String[] strings = new String[]{currentNumbering, name, location};
        Utils.setTexts(ids, strings, view);
        int color = ContextCompat.getColor(getContext(), R.color.black);
        ColorDrawable drawable = new ColorDrawable(color);
        int colorDone = ContextCompat.getColor(getContext(), R.color.colorMiMuniPrimary);
        ColorDrawable drawableDone = new ColorDrawable(colorDone);
        switch (current.getState()) {
            case 0:
                setStatusColor(0, R.drawable.circlegreencheckstatus, view, drawableDone);
                setStatusColor(1, R.drawable.gray_circle, view, drawable);
                setStatusColor(2, R.drawable.gray_circle, view, drawable);
                break;
            case 1:
                setStatusColor(0, R.drawable.circlegreenstatus, view, drawableDone);
                setStatusColor(1, R.drawable.circlegreencheckstatus, view, drawableDone);
                setStatusColor(2, R.drawable.gray_circle, view, drawable);
                break;
            case 2:
                setStatusColor(0, R.drawable.circlegreenstatus, view, drawableDone);
                setStatusColor(1, R.drawable.circlegreenstatus, view, drawableDone);
                setStatusColor(2, R.drawable.circlegreencheckstatus, view, drawableDone);
                break;
        }
        return view;
    }

    private void setStatusColor(int number, int circleId, View view, ColorDrawable drawable) {
        Resources resources = getContext().getResources();
        int imageId = resources.getIdentifier("status" + number, "id", "gt.muni.chiantla");
        ImageView image = view.findViewById(imageId);
        int lineId = resources.getIdentifier("lineStatus" + number, "id", "gt.muni.chiantla");
        View line = view.findViewById(lineId);
        image.setImageResource(circleId);
        if (number > 0)
            line.setBackground(drawable);
    }
}
