package gt.muni.chiantla.mymuni;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import gt.muni.chiantla.R;

/**
 * Adaptador para las leyes de la sección de leyes.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerData;
    private List<String> listData;

    public ExpandableListAdapter(List<String> headerData,
                                 List<String> listData, Context context) {
        this.headerData = headerData;
        this.listData = listData;
        this.context = context;
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
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String header =  getGroup(i);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (i == 0)
            view = inflater.inflate(R.layout.law_blue, null);
        else
            view = inflater.inflate(R.layout.list_group, null);
        TextView labelHeader = (TextView) view.findViewById(R.id.groupLabel);
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
        TextView label = (TextView) view.findViewById(R.id.listLabel);
        label.setText(text);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
