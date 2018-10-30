package gt.muni.chiantla.connections.api;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.eclipsesource.json.JsonArray;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClients;

/**
 * Una conexion abstracta al API
 *
 * @param <E> el tipo de respuesta
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class AbstractRestConnection<E> extends AsyncTask<String, Void, E> {
    /**
     * URL del servidor.
     */
    private static final String SERVER_URL = "SERVER URL";
    private static final int TIMEOUT = 10;
    private static final String TOKEN = "SERVER TOKEN";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private RestConnectionInterface<E> context;
    /**
     * Información que será enviada al servidor. Son ignorados si
     * {@link RestConnection#multipart} es true.
     */
    private List<NameValuePair> parametersList;
    /**
     * Keys de los archivos que serán enviados al servidor. Son ignorados si
     * {@link RestConnection#multipart} es false.
     */
    private String[] fileKeys;
    /**
     * Uri de los archivos que serán enviados al servidor. Son ignorados si
     * {@link RestConnection#multipart} es false.
     */
    private String[] files;
    /**
     * Información de los parámetros que serán enviados al servidor. Son ignorados si
     * {@link RestConnection#multipart} es false.
     */
    private String[] parameters;
    /**
     * Keys de la información que será enviada al servidor. Son ignorados si
     * {@link RestConnection#multipart} es false.
     */
    private String[] parameterKeys;
    /**
     * Si se enviaran archivos o no.
     */
    private boolean multipart;

    public AbstractRestConnection(RestConnectionInterface<E> context) {
        super();
        this.context = context;
        parametersList = null;
        fileKeys = null;
        files = null;
        parameters = null;
        parameterKeys = null;
    }

    /**
     * Se conecta a la url deseada, enviando los archivos y parámetros deseados. Luego hace parse
     * crea un {@link JsonArray} con el JSON que devuelve el servidor.
     *
     * @param strings las urls a donde se realizarán las conexiones
     * @return Un {@link JsonArray} que tiene la respuesta del servidor.
     */
    @Override
    public E doInBackground(String... strings) {
        E data = null;
        if (strings != null) {
            String string = strings[0];
            CloseableHttpResponse response = connect(string);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                data = processData(entity);
            }
            try {
                if (response != null) response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * Metodo que procesa la data que el servidor devuelve para que sea del tipo {@link E}
     *
     * @param entity la data que el servidor envio
     * @return La data procesada
     */
    protected abstract E processData(HttpEntity entity);

    public void setParameters(List<NameValuePair> parameters) {
        this.parametersList = parameters;
    }

    public void setMultipart(boolean multipart, String[] parameters, String[] parameterKeys,
                             String[] files, String[] fileKeys) {
        this.multipart = multipart;
        this.fileKeys = fileKeys;
        this.files = files;
        this.parameters = parameters;
        this.parameterKeys = parameterKeys;
    }

    /**
     * Se conecta al servidor con los parametros previamente colocados.
     *
     * @param url la url a la que se conectara
     * @return una {@link CloseableHttpResponse}, que es la respuesta del servidor
     */
    protected CloseableHttpResponse connect(String url) {
        CloseableHttpClient httpClient = buildHttpClient();
        final HttpPost post = new HttpPost(SERVER_URL + url);
        post.setHeader("Authorization", "Token " + TOKEN);
        if (parametersList != null) {
            try {
                post.setEntity(new UrlEncodedFormEntity(parametersList, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (multipart) {
            post.setEntity(buildMultipartEntity());
        }
        return setTimeoutHandler(post, httpClient);
    }

    /**
     * Crea el {@link CloseableHttpClient} con los parametros necesarios
     *
     * @return el {@link CloseableHttpClient} que ejecutara la conexion
     */
    private CloseableHttpClient buildHttpClient() {
        HttpClientBuilder builder = HttpClients.custom();
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT * 1000)
                .setConnectionRequestTimeout(TIMEOUT * 1000)
                .setSocketTimeout(TIMEOUT * 1000)
                .build();
        builder.setDefaultRequestConfig(config);
        return builder.build();
    }

    /**
     * Crea una {@link HttpEntity} para enviar archivos
     *
     * @return la {@link HttpEntity}
     */
    private HttpEntity buildMultipartEntity() {
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        for (int i = 0; i < fileKeys.length; i++) {
            String fileUri = files[i];
            if (!fileUri.startsWith("file://") && !fileUri.startsWith("content://"))
                fileUri = "file://" + fileUri;
            Uri uri = Uri.parse(fileUri);
            InputStream is = null;
            try {
                is = context.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            entity = entity.addBinaryBody(fileKeys[i], stream.toByteArray(),
                    ContentType.APPLICATION_OCTET_STREAM, "notificationImage.jpeg");
        }
        for (int i = 0; i < parameterKeys.length; i++) {
            if (parameterKeys[i] != null && parameters[i] != null)
                entity = entity.addTextBody(parameterKeys[i], parameters[i], ContentType
                        .APPLICATION_JSON);
        }
        return entity.build();
    }

    /**
     * Crea un handler que cerrara la conexion si el tiempo indicado pasa
     *
     * @param post       la conexion que sera cerrada
     * @param httpClient el cliente que ejecutara la conexion
     * @return la respuesta del servidor
     */
    private CloseableHttpResponse setTimeoutHandler(final HttpPost post,
                                                    CloseableHttpClient httpClient) {
        try {
            // Si el request se tarda más de 20 segundos, abortar.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    post.abort();
                }
            }, 20000);
            CloseableHttpResponse response = httpClient.execute(post);
            handler.removeCallbacksAndMessages(null);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Llama a {@link RestConnectionInterface#restResponseHandler(E)} con el
     * {@link JsonArray} que tiene la respuesta del servidor.
     *
     * @param response la respuesta del servidor, ya parseada.
     */
    @Override
    protected void onPostExecute(E response) {
        try {
            context.restResponseHandler(response);
        } catch (java.lang.IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interface para conectarse al servidor.
     *
     * @author Ludiverse
     * @author Innerlemonade
     */
    public interface RestConnectionInterface<E> {
        /**
         * Es llamado cuando se obtiene una resuesta del servidor.
         *
         * @param response La respuesta del servidor, parseada.
         */
        void restResponseHandler(E response);

        ContentResolver getContentResolver();
    }

}
