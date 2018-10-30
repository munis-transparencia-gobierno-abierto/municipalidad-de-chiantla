package gt.muni.chiantla.mymuni;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.DiscussionActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;

/**
 * Actividad de la sección de contactos. Muestra información de contacto y luego una lista de
 * contactos
 * con su información de contacto.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ContactsActivity extends CustomActivity implements ExpandableListView
        .OnChildClickListener {
    private ExpandableListView mListView;
    private Page page;
    private ListAdapter adapter;

    private TextView emailView;
    private TextView phoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = (Page) getIntent().getSerializableExtra("page");
        createOptionsMenu = true;
        init();
    }

    protected void init() {
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_contacts);
        ((TextView) findViewById(R.id.sectionTitle)).setText(page.getName());
        mListView = findViewById(R.id.list);
        showTextInView();
        mListView.setOnChildClickListener(this);

        Utils.sendFirebaseEvent("MiChiantla", "Contactos", null, null,
                "Contactos", "Contactos", this);
    }

    protected void showTextInView() {
        showItems();
    }

    private void showItems() {
        List<PageItem> items = page.getItems();
        items.add(0, null); // placeholder for header
        adapter = new ListAdapter(this, items);
        mListView.setAdapter(adapter);
    }

    public void mainActions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_action));
        final ArrayList<String> options = new ArrayList<>();
        if (phoneView.getText().length() != 0) {
            options.add(getString(R.string.call));
        }
        if (emailView.getText().length() != 0) {
            options.add(getString(R.string.send_email));
        }
        String[] optionsArray = new String[options.size()];
        options.toArray(optionsArray);
        builder.setItems(optionsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getString(R.string.call).equals(options.get(which))) {
                    callNumber();
                } else if (getString(R.string.send_email).equals(options.get(which))) {
                    sendEmail();
                }
            }
        });
        builder.show();
    }


    public void callNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneView.getText()));
        startActivity(intent);
    }

    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String email = emailView.getText().toString();
        intent.setData(Uri.parse("mailto:" + email));
        String subject = getString(R.string.email_subject);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(intent);
    }

    public void goToDiscussion(View view) {
        Intent intent = new Intent(this, DiscussionActivity.class);
        startActivity(intent);
    }

    /**
     * Muestra un diálogo con opciones cuando se presiona uno de los contactos. El diálogo muestra
     * las opciones depediendo de que información tengan los contactos.
     *
     * @param adapterView el adaptador
     * @param view        la vista que fue presionada
     * @param id          el id de la vista presionada
     */
    @Override
    public boolean onChildClick(ExpandableListView adapterView, View view, int groupPosition, int
            childPosition, long id) {
        final PageItem item = adapter.getGroup(groupPosition);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_action));
        String[] optionsBuilder = null;
        if (item.getLink() != null && item.getPhone() != null) {
            optionsBuilder = new String[]{
                    getString(R.string.add_contact),
                    getString(R.string.call),
                    getString(R.string.send_email)
            };
        } else if (item.getLink() != null) {
            optionsBuilder = new String[]{
                    getString(R.string.add_contact),
                    getString(R.string.send_email)
            };
        } else if (item.getPhone() != null) {
            optionsBuilder = new String[]{
                    getString(R.string.add_contact),
                    getString(R.string.call)
            };
        }
        final String[] options = optionsBuilder;
        if (options != null) {
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String link;
                    Intent intent = null;
                    if (options[which].equals(ContactsActivity.this.getString(R.string
                            .add_contact))) {
                        link = item.getLink();
                        String name = item.getName();
                        String linkName = item.getLinkName();
                        String chiantla = getString(R.string.municipality);
                        String phone = item.getPhone();
                        intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE)
                                .putExtra(ContactsContract.Intents.Insert.EMAIL, link)
                                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                                        ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                .putExtra(ContactsContract.Intents.Insert.JOB_TITLE, name)
                                .putExtra(ContactsContract.Intents.Insert.COMPANY, chiantla)
                                .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                                .putExtra(ContactsContract.Intents.Insert.NAME, linkName);
                    } else if (options[which].equals(ContactsActivity.this.getString(R.string
                            .call))) {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + item.getPhone()));
                    } else if (options[which].equals(ContactsActivity.this.getString(R.string
                            .send_email))) {
                        link = item.getLink();
                        intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + link));
                        String subject = getString(R.string.email_subject);
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{link});
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    }
                    startActivity(intent);
                }
            });
            builder.show();
        }
        return true;
    }

    private class ListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<PageItem> objects;
        private View headerView;

        private ListAdapter(Context context, List<PageItem> objects) {
            this.context = context;
            this.objects = objects;
        }

        @Override
        public int getGroupCount() {
            return objects.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return 1;
        }

        @Override
        public PageItem getGroup(int i) {
            return objects.get(i);
        }

        @Override
        public PageItem getChild(int i, int i1) {
            return objects.get(i);
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
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @Override
        public View getChildView(int i, int i1, boolean isLast, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                PageItem item = getGroup(i);
                view = inflater.inflate(R.layout.section_contact_content, viewGroup, false);
                TextView text = view.findViewById(R.id.content);
                if (item != null && item.getContent() != null && !item.getContent().equals("")) {
                    text.setVisibility(View.VISIBLE);
                    String parsedText = removeTags(item.getContent());
                    searchLinks(parsedText, text, item);
                    View image = view.findViewById(R.id.callButton);
                    if (item.getLink() != null || item.getPhone() != null) {
                        image.setVisibility(View.VISIBLE);
                    } else {
                        image.setVisibility(View.GONE);
                    }
                    if (i != objects.size() - 1) {
                        view.setBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        view.setBackground(getResources().getDrawable(R.drawable.lower_radius));
                    }
                } else {
                    text.setVisibility(View.GONE);
                }
            }
            return view;
        }

        /**
         * Elimina las tags de html extra
         *
         * @param content el contenido del que se eliminaran las tags
         * @return el contenido sin las tags
         */
        private String removeTags(String content) {
            if (content != null)
                content = removeTag(content, "<!--", "-->");
            if (content != null)
                content = removeTag(content, "<style>", "</style>");
            return content;
        }

        /**
         * Elimina una tag de html
         *
         * @param content el contenido del que se eliminaran la tag
         * @param tag     la tag a eliminar
         * @param endTag  la tag que cierra
         * @return el contenido sin la tag
         */
        private String removeTag(String content, String tag, String endTag) {
            int start = content.lastIndexOf(tag);
            while (start > -1) {
                int end = content.lastIndexOf(endTag);
                String newContent = content.substring(0, start);
                newContent += content.substring(end + endTag.length(), content.length());
                content = newContent;
                start = content.lastIndexOf(tag);
            }
            return content;
        }

        /**
         * Muestra el contenido de cada contacto.
         *
         * @see BaseAdapter#getView(int, View, ViewGroup)
         */
        @Override
        public View getGroupView(int i, boolean expanded, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                if (i == 0) {
                    if (headerView == null) {
                        headerView = inflater.inflate(R.layout.section_contacts_header,
                                viewGroup, false);
                        emailView = headerView.findViewById(R.id.main_email);
                        phoneView = headerView.findViewById(R.id.main_phone);
                        setHeaderContent(headerView, inflater);
                    }
                    view = headerView;
                    LinearLayout container = view.findViewById(R.id.insertPoint);
                    View button = view.findViewById(R.id.nextButton);
                    if (expanded) {
                        container.setVisibility(View.VISIBLE);
                        button.setRotation(270);
                    } else {
                        container.setVisibility(View.GONE);
                        button.setRotation(90);
                    }
                } else {
                    PageItem item = getGroup(i);
                    String content = item.getContent();
                    view = getNormalView(content, inflater, viewGroup, expanded);
                    TextView text = view.findViewById(R.id.name);
                    text.setText(item.getName());
                    if (i == 1) {
                        view.setBackground(getResources().getDrawable(R.drawable.upper_radius));
                    } else if (!expanded && i == objects.size() - 1) {
                        view.setBackground(getResources().getDrawable(R.drawable.lower_radius));
                    } else {
                        view.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }
            }
            return view;
        }

        /**
         * Obtiene una view normal para un contacto. ie. no una view de header
         *
         * @param content   el contenido
         * @param inflater  el inflater para crear views
         * @param viewGroup el contenedor de la view
         * @param expanded  si se encuentra expandido
         * @return la view
         */
        private View getNormalView(String content, LayoutInflater inflater, ViewGroup viewGroup,
                                   boolean expanded) {
            if (content != null && !content.equals("")) {
                View view = inflater.inflate(R.layout.section_contact_header, viewGroup, false);
                int drawableResource = (expanded) ? R.drawable.flecha_pequena___arriba : R
                        .drawable.flecha_pequena___anajo_negro;
                Drawable resource = getResources().getDrawable(drawableResource);
                ((ImageView) view.findViewById(R.id.expandable_icon)).setImageDrawable(resource);
                return view;
            } else return inflater.inflate(R.layout.section_contact_sub_header, viewGroup, false);
        }

        /**
         * Coloca un contenido de la card de header, el contenido varia dependiendo de si es
         * un tipo especial o no.
         *
         * @param view     la view que contiene el contenido
         * @param inflater el inflater para crear nuevas views
         */
        private void setHeaderContent(View view, LayoutInflater inflater) {
            LinearLayout container = view.findViewById(R.id.insertPoint);
            for (PageItem item : page.getExtraItems()) {
                switch (item.getName()) {
                    case "ADDRESS":
                        TextView addressView = view.findViewById(R.id.address);
                        addressView.setText(Utils.fromHtml(removeTags(item.getContent())));
                        break;
                    case "PHONE":
                        view.findViewById(R.id.phone_container).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.callButton).setVisibility(View.VISIBLE);
                        phoneView.setText(Utils.fromHtml(removeTags(item.getContent())));
                        break;
                    case "EMAIL":
                        String content = removeTags(item.getContent());
                        if (content != null) {
                            view.findViewById(R.id.email_container).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.callButton).setVisibility(View.VISIBLE);
                            emailView.setText(Utils.fromHtml(content));
                        }
                        break;
                    default:
                        View itemView = inflater.inflate(R.layout.section_contacts_header_item,
                                container, false);
                        ((TextView) itemView.findViewById(R.id.name)).setText(item.getName());
                        ((TextView) itemView.findViewById(R.id.content)).setText(item.getContent());
                        switch (item.getName().toLowerCase()) {
                            case "correo electrónico":
                                itemView.setId(R.id.email);
                                break;
                            case "phone":
                                itemView.setId(R.id.phone);
                                break;
                        }
                        if (!item.getName().equals("Horario")) {
                            container.addView(itemView);
                        } else {
                            ((LinearLayout) view.findViewById(R.id.address_container)).addView
                                    (itemView);
                        }
                        break;
                }
            }
        }

        /**
         * Busca dentro del contendio los links de teléfono o correo. También quita los tags de html
         * antes de colocar dentro del view.
         *
         * @param content el contenido
         * @param text    el view donde se colocará el contenido ya parseado
         * @param item    el item actual
         */
        private void searchLinks(String content, TextView text, PageItem item) {
            String href = "href=\"";
            int start = content.indexOf(href);
            if (start > -1) {
                start += href.length();
                saveLink(start, content, item);
                start = content.indexOf(href, start);
                if (start > 0) {
                    start += href.length();
                    saveLink(start, content, item);
                }
                String name = getItenName(content);
                item.setLinkName(name);
                content = content.replaceAll("<\\/a>|<a[^>]*>", "");
            }
            text.setText(Utils.fromHtml(content));
        }

        /**
         * Busca en el contenido para encontrar el nombre del contacto.
         *
         * @param content el contendio
         * @return el nombre del contacto, un string vacio si no lo encuentra.
         */
        private String getItenName(String content) {
            String name = "";
            int start = content.indexOf("<br/>");
            if (start == -1) start = content.indexOf("<br>");
            start += 5;
            int end = content.indexOf("<br/>", start);
            if (end == -1) end = content.indexOf("<br>", start);
            if (end != -1 && start != -1) {
                name = content.substring(start, end);
            } else {
                start = content.indexOf("<strong>");
                end = content.indexOf("</strong>", start);
                if (end != -1 && start != -1) name = content.substring(start, end);
            }
            return name;
        }

        /**
         * Hace set de un teléfono o de un link
         *
         * @param start   el lugar donde se empieza el link
         * @param content el texto donde se buscará el link
         * @param item    el item donde se guardará el link
         */
        private void saveLink(int start, String content, PageItem item) {
            int end = content.indexOf('"', start);
            String linkText = content.substring(start, end);
            String firstChar = linkText.charAt(0) + "";
            if (firstChar.equals("+") || firstChar.matches("[0-9]"))
                item.setPhone(linkText);
            else {
                item.setLink(linkText);
            }
        }

        public Context getContext() {
            return context;
        }

        public List<PageItem> getObjects() {
            return objects;
        }
    }
}
