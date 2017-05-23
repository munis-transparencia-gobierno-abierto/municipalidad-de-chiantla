package gt.muni.chiantla.mymuni.development;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.DevelopmentItem;
import gt.muni.chiantla.content.Project;

/**
 * Adaptador para los proyectos de desarrollo
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
        super(context, objects);
    }

    /**
     * Muestra un proyecto. Para cada proyecto muestra el estado correcto.
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
        int color = ContextCompat.getColor(getContext(), R.color.progress_bar_background_inverted);
        ColorDrawable drawable = new ColorDrawable(color);
        int colorDone = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        ColorDrawable drawableDone = new ColorDrawable(colorDone);
        int colorNotDone = ContextCompat.getColor(getContext(), R.color.statusNotDone);
        switch (current.getState()) {
            case 0:
                setStatusColor(0, R.mipmap.circlegreencheckstatus, view, drawableDone, colorDone);
                setStatusColor(1, R.mipmap.gray_circle, view, drawable, colorNotDone);
                setStatusColor(2, R.mipmap.gray_circle, view, drawable, colorNotDone);
                break;
            case 1:
                setStatusColor(0, R.mipmap.circlegreenstatus, view, drawableDone, color);
                setStatusColor(1, R.mipmap.circlegreencheckstatus, view, drawableDone, colorDone);
                setStatusColor(2, R.mipmap.gray_circle, view, drawable, colorNotDone);
                break;
            case 2:
                setStatusColor(0, R.mipmap.circlegreenstatus, view, drawableDone, color);
                setStatusColor(1, R.mipmap.circlegreenstatus, view, drawableDone, color);
                setStatusColor(2, R.mipmap.circlegreencheckstatus, view, drawableDone, colorDone);
                break;
        }
        return view;
    }

    private void setStatusColor(int number, int circleId, View view, ColorDrawable drawable, int color) {
        Resources resources = getContext().getResources();
        int imageId = resources.getIdentifier("status" + number, "id", "gt.muni.chiantla");
        ImageView image = (ImageView) view.findViewById(imageId);
        int lineId = resources.getIdentifier("lineStatus" + number, "id", "gt.muni.chiantla");
        View line = view.findViewById(lineId);
        int textId = resources.getIdentifier("status" + number + "Text", "id", "gt.muni.chiantla");
        TextView text = (TextView) view.findViewById(textId);
        image.setImageResource(circleId);
        text.setTextColor(color);
        if (number > 0)
            line.setBackground(drawable);
    }
}
