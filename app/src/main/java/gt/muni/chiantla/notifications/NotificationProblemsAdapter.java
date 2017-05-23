package gt.muni.chiantla.notifications;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import gt.muni.chiantla.R;

/**
 * Adaptador para los tipos de problema
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NotificationProblemsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] problemTypes;

    public NotificationProblemsAdapter(Context applicationContext, String[] problemTypes) {
        this.context = applicationContext;
        this.problemTypes = problemTypes;
        inflater = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return problemTypes.length;
    }

    @Override
    public Object getItem(int position) {
        return problemTypes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position == 0){ // Muestra el header
            convertView = inflater.inflate(R.layout.notification_spinner_title, null);
            parent.setPadding(0,0,0,0);
        }
        else {
            convertView = inflater.inflate(R.layout.notification_spinner, null);
        }
        TextView problems = (TextView) convertView.findViewById(R.id.textSpinner1);
        problems.setText(problemTypes[position]);
        if(position == problemTypes.length - 1) {
            convertView.findViewById(R.id.lineSpinner).setVisibility(View.INVISIBLE);
        }


        return convertView;
    }

    @Override
    public boolean isEnabled(int position){
        if(position == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
