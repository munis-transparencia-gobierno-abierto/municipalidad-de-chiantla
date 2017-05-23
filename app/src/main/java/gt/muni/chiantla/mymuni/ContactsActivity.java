package gt.muni.chiantla.mymuni;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;
import java.util.Locale;

import gt.muni.chiantla.DiscussionActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Actividad de la sección de contactos. Muestra información de contacto y luego una lista de contactos
 * con su información de contacto.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ContactsActivity extends RestConnectionActivity implements AdapterView.OnItemClickListener {
    private CustomListView mListView;
    /**
     * Los ids de las views en las que se colocarán los textos
     */
    private int[] VIEW_IDS = new int[]{
            R.id.schedule,
            R.id.mail,
            R.id.phone,
            R.id.facebook,
            R.id.instagram,
            R.id.twitter,
            R.id.youtube,
            R.id.website,
    };
    private int PAGE_ID = 54;
    /**
     * La cantidad de items que no son información de contactos
     */
    private int HEADER_ITEM_COUNT = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void init() {
        setCustomActionBar(R.string.contacts, true);
        setContentView(R.layout.activity_contacts);

        mListView = (CustomListView) findViewById(R.id.list);
        initScroll(mListView, findViewById(android.R.id.content));
        View view = getLayoutInflater().inflate(R.layout.section_contacts_header, null);
        mListView.addHeaderView(view, null, false);
        showTextInView();
        mListView.setOnItemClickListener(this);

        Utils.sendFirebaseEvent("MiChiantla", "Contactos", null, null,
                "Contactos", "Contactos", this);
    }

    public int[] getViewIds() {
        return VIEW_IDS;
    }

    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        if (response != null) {
            Page page = new Page(response);
            page.save(db);
            showHeaderTexts(page);
            db.addOrUpdateUpdate("pages", "Page", getPageId(), true);
            showItems(page);
        }
    }

    protected void showTextInView() {
        Integer pageId = getPageId();
        if (!db.isUpdated("pages", "Page", pageId)) {
            paths = new String[]{"page/" + pageId.toString() + "/items/"};
            connect();
        } else {
            Page page = new Page(db, pageId);
            showHeaderTexts(page);
            showItems(page);
        }
    }

    private void showHeaderTexts(Page page) {
        ArrayList<String> texts = page.getTexts(false, false);
        Utils.setTexts(getViewIds(), texts.subList(1, texts.size()), this);
        ((TextView) findViewById(R.id.address)).setText(Utils.fromHtml(texts.get(0)));
    }

    private void showItems(Page page) {
        ArrayList<PageItem> items = page.getItems();
        items = new ArrayList<>(items.subList(getHeaderItemCount(), items.size()));
        ListAdapter adapter = new ListAdapter(this, items);
        adapter.showHeaderIcon(getShowHeaderIcon());
        mListView.setAdapter(adapter);
    }

    public void goToAddress(View view) {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=") +
                android.net.Uri.encode(String.format("%s@%f,%f", getString(R.string.municipality),
                        15.35498, -91.4584797), "UTF-8");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void callNumber(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        intent.setData(Uri.parse("tel:" + phone.getText()));
        startActivity(intent);
    }

    public void sendEmail(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        TextView emailView = (TextView) view.findViewById(R.id.mail);
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
     * @param adapterView el adaptador
     * @param view la vista que fue presionada
     * @param position la posición de la vista en el adaptador
     * @param id el id de la vista presionada
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final PageItem item = (PageItem) adapterView.getItemAtPosition(position);
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
                    if (options[which].equals(ContactsActivity.this.getString(R.string.add_contact))) {
                        link = item.getLink();
                        String name = item.getName();
                        String linkName = item.getLinkName();
                        String chiantla = getString(R.string.municipality);
                        String phone = item.getPhone();
                        intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE)
                                .putExtra(ContactsContract.Intents.Insert.EMAIL, link)
                                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                .putExtra(ContactsContract.Intents.Insert.JOB_TITLE, name)
                                .putExtra(ContactsContract.Intents.Insert.COMPANY, chiantla)
                                .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                                .putExtra(ContactsContract.Intents.Insert.NAME, linkName);
                    } else if (options[which].equals(ContactsActivity.this.getString(R.string.call))) {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + item.getPhone()));
                    } else if (options[which].equals(ContactsActivity.this.getString(R.string.send_email))) {
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
    }

    public int getHeaderItemCount() {
        return HEADER_ITEM_COUNT;
    }

    public CustomListView getmListView() {
        return mListView;
    }

    public void setmListView(CustomListView mListView) {
        this.mListView = mListView;
    }

    public int getPageId() {
        return PAGE_ID;
    }

    public boolean getShowHeaderIcon() {
        return false;
    }
}
