package gt.muni.chiantla.budget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;

/**
 * Adaptador de proyectos de gastos.
 * @author Ludiverse
 * @author Innerlemonade
 * @see android.widget.ExpandableListAdapter
 */
public class ProjectsAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String[]> headerData;
    private HashMap<Integer, ArrayList<ArrayList>> listData;

    public ProjectsAdapter(Context context, ArrayList<String[]> headerData, HashMap<Integer, ArrayList<ArrayList>> listData) {
        this.context = context;
        this.headerData = headerData;
        this.listData = listData;
    }

    @Override
    public int getGroupCount() {
        return headerData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listData.get(i).size();
    }

    @Override
    public String[] getGroup(int i) {
        return headerData.get(i);
    }

    @Override
    public ArrayList getChild(int i, int i1) {
        return listData.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return Long.parseLong(headerData.get(i)[0]);
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
        String[] strings = getGroup(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.section_expenses_item_header, null);
        }
        int[] ids = new int[]{R.id.name, R.id.value};
        String[] viewStrings = new String[]{strings[1], strings[2]};
        Utils.setTexts(ids, viewStrings, view);
        ((ExpandableListView) viewGroup).expandGroup(i);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ArrayList<String[]> list = getChild(i, i1);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.section_expenses_item, null);
        String[] strings = list.get(0);
        int[] ids = new int[]{R.id.name, R.id.value};
        String[] viewStrings = new String[]{strings[1], strings[2]};
        Utils.setTexts(ids, viewStrings, view);
        for (int j = 1; j < list.size(); j++) {
            strings = list.get(j);
            View innerView = inflater.inflate(R.layout.section_expenses_sub_item, null);
            viewStrings = new String[]{strings[1], strings[2]};
            Utils.setTexts(ids, viewStrings, innerView);
            ViewGroup group = (ViewGroup) view.findViewById(R.id.insertPoint);
            group.addView(innerView);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
