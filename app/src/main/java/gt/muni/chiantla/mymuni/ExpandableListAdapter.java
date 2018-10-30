package gt.muni.chiantla.mymuni;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;

/**
 * Adaptador para las leyes de la actividad de acordion.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerData;
    private List<String> listData;
    private boolean law;

    public ExpandableListAdapter(List<String> headerData,
                                 List<String> listData, Context context, boolean law) {
        this.headerData = headerData;
        this.listData = listData;
        this.context = context;
        this.law = law;
    }

    public ExpandableListAdapter(List<String> headerData,
                                 List<String> listData, Context context) {
        this.headerData = headerData;
        this.listData = listData;
        this.context = context;
        this.law = false;
    }

    @Override
    public int getGroupCount() {
        return headerData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public String getGroup(int i) {
        return headerData.get(i);
    }

    @Override
    public String getChild(int i, int i1) {
        return listData.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean expanded, View view, ViewGroup viewGroup) {
        String header = getGroup(i);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_group, null);
        if (i == 0) {
            view.setBackground(context.getResources().getDrawable(R.drawable.upper_radius));
        } else if (!expanded && i == headerData.size() - 1) {
            view.setBackground(context.getResources().getDrawable(R.drawable.lower_radius));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        int drawableResource = (expanded) ? R.drawable.flecha_pequena___arriba :
                R.drawable.flecha_pequena___anajo_negro;
        Drawable resource = context.getResources().getDrawable(drawableResource);
        ((ImageView) view.findViewById(R.id.expandable_icon)).setImageDrawable(resource);
        TextView labelHeader = view.findViewById(R.id.groupLabel);
        labelHeader.setText(header);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String text = getChild(i, i1);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }
        if (i != headerData.size() - 1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            view.setBackground(context.getResources().getDrawable(R.drawable.lower_radius));
        }
        TextView label = view.findViewById(R.id.listLabel);
        label.setText(Utils.fromHtml(text));
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
