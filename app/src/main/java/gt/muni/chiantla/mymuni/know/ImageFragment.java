package gt.muni.chiantla.mymuni.know;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpEntity;
import gt.muni.chiantla.AppDatabase;
import gt.muni.chiantla.R;
import gt.muni.chiantla.connections.api.AbstractRestConnection;
import gt.muni.chiantla.connections.api.RestLoaderController;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;

/**
 * Fragmento de información de la sección de conoce
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ImageFragment extends KnowFragment
        implements AbstractRestConnection.RestConnectionInterface<Uri> {
    private RestLoaderController loaderController;
    private RestImageConnection imageConnection;
    private PageItem imageItem;
    private InformationOpenHelper helper;

    public static ImageFragment newInstance(Page page) {
        ImageFragment fragment = new ImageFragment();
        fragment.page = page;
        fragment.imageItem = page.getItems().get(0);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        helper = InformationOpenHelper.getInstance(this.getActivity());

        loaderController = new RestLoaderController(getActivity());
        imageConnection = new RestImageConnection(this);

        View view = super.onCreateView(inflater, container, savedInstance);
        checkForUpdates();
        return view;
    }

    /**
     * Checks for updates before data is shown.
     */
    private void checkForUpdates() {
        if (!helper.isUpdated(Page.APP_NAME, Page.MODEL_NAME, page.getId())) {
            loaderController.addLoader();
            imageConnection.execute("page/" + page.getId() + "/image/");
        } else {
            showData();
        }
    }

    /**
     * Hace un override de {@link KnowFragment#showItems} para que la forma en la que los items
     * de la pagina se muestran sea personalizada
     *
     * @param view     la view en la que se mostraran los items
     * @param inflater el inflater para crear views
     */
    @Override
    protected void showItems(View view, LayoutInflater inflater) {
    }

    /**
     * Obtiene el layout para el fragmento
     *
     * @return el id del recurso
     */
    @Override
    public int getLayout() {
        return R.layout.fragment_image;
    }


    /**
     * Es llamado cuando se obtiene una resuesta del servidor.
     *
     * @param response La respuesta del servidor, parseada.
     */
    @Override
    public void restResponseHandler(Uri response) {
        if (response != null) {
            helper.addOrUpdateUpdate(Page.APP_NAME, Page.MODEL_NAME, page.getId(), true);
            imageItem.setContent(response.toString());
            imageItem.save(AppDatabase.getInstance(getActivity()));
            showData();
        }
        loaderController.removeLoader();
    }

    /**
     * Muestra la imagen, luego de haberla descargado
     */
    private void showData() {
        if (getActivity() != null && getView() != null) {
            Uri uri = Uri.parse(imageItem.getContent());
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), uri);
                ((ImageView) getView().findViewById(R.id.image)).setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ContentResolver getContentResolver() {
        return (getActivity() != null) ? getActivity().getContentResolver() : null;
    }

    /**
     * Conexion personalizada que descarga, y guarda, una imagen del servidor
     */
    private static class RestImageConnection extends AbstractRestConnection<Uri> {
        private ImageFragment fragment;

        private RestImageConnection(ImageFragment context) {
            super(context);
            fragment = context;
        }

        /**
         * Procesa la informacion del servidor para guardar la imagen
         *
         * @param entity la data que el servidor envio
         * @return la uri de la imagen guardada
         */
        @Override
        protected Uri processData(HttpEntity entity) {
            Uri bitmapUri = null;
            if (fragment.getActivity() != null) {
                Locale locale = fragment.getResources().getConfiguration().locale;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", locale)
                        .format(new Date());
                try {
                    Bitmap image = BitmapFactory.decodeStream(entity.getContent());
                    String bitmapPath = MediaStore.Images.Media.insertImage(
                            fragment.getContentResolver(),
                            image,
                            "image_" + timeStamp,
                            null);
                    bitmapUri = Uri.parse(bitmapPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmapUri;
        }
    }
}
