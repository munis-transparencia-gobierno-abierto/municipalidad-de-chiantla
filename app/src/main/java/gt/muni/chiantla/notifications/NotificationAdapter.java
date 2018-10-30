package gt.muni.chiantla.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gt.muni.chiantla.R;
import gt.muni.chiantla.content.Notification;

/**
 * Adaptador para las notificaciones.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class NotificationAdapter extends BaseAdapter {
    private List<Notification> notifications;
    private Context context;

    public NotificationAdapter(Collection<Notification> notifications, Context context) {
        this.notifications = new ArrayList<>(notifications);
        this.context = context;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Notification getItem(int i) {
        return notifications.get(i);
    }

    @Override
    public long getItemId(int i) {
        return notifications.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Notification notification = getItem(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.section_notification, null);
        }
        ((TextView) view.findViewById(R.id.problem_type)).setText(notification.getProblemType());
        ((TextView) view.findViewById(R.id.address)).setText(notification.getAddress());
        ((TextView) view.findViewById(R.id.date)).setText(notification.getDate());
        ((TextView) view.findViewById(R.id.status)).setText(notification.getStatus());
        return view;
    }
}
