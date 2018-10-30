package gt.muni.chiantla.notifications;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;

/**
 * Fragmento que permite seleccionar una imagen o tomar una foto para una nueva notificacion
 */
public class NotificationProblemImageFragment extends NotRequiredStepFragment implements View
        .OnClickListener {
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PHOTO = 2;
    private final int FILES_PERMISSION = 1;
    private Uri fileUri;
    private ImageView selectedImage;

    public static NotificationProblemImageFragment createFragment(Uri imageUri) {
        NotificationProblemImageFragment instance = new NotificationProblemImageFragment();
        instance.fileUri = imageUri;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_problem_image, container,
                false);
        selectedImage = view.findViewById(R.id.image);
        if (fileUri != null) setPhotoBitmap();

        view.findViewById(R.id.photo_button).setOnClickListener(this);
        view.findViewById(R.id.camera_button).setOnClickListener(this);

        return view;
    }

    /**
     * Valida si los datos requeridos fueron llenados
     *
     * @return si los datos requeridos fueron llenados
     */
    public boolean validate() {
        return !IS_REQUIRED || fileUri != null;
    }

    /**
     * Le envia a la actividad la informacion llenada
     *
     * @param activity la actividad en donde se colocan los datos
     */
    public void getData(NewNotificationActivity activity) {
        activity.setFileUri(fileUri);
    }

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = Utils.createImageFile(getActivity(),
                    getString(R.string.notification_image_name));
            fileUri = Uri.fromFile(photoFile);
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        getResources().getString(R.string.fileprovider),
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        }
    }

    public void selectImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_button:
                openCamera();
                break;
            case R.id.photo_button:
                selectImages();
                break;
        }
    }

    /**
     * Handler que se llama luego de haber tomado una foto o seleccionado una imagen
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    fileUri = Utils.getAbsoluteUri(getActivity(), data.getData());
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission
                                            .READ_EXTERNAL_STORAGE},
                                    FILES_PERMISSION);
                        } else {
                            setGalleryBitmap();
                        }
                    } else {
                        setGalleryBitmap();
                    }
                }
                break;
            case TAKE_PHOTO:
                setPhotoBitmap();
                break;
        }
    }

    private void setPhotoBitmap() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                    .getContentResolver(), fileUri);
            setSelectedImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGalleryBitmap() {
        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.toString());
        setSelectedImage(bitmap);
    }

    private void setSelectedImage(Bitmap bitmap) {
        selectedImage.setImageBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FILES_PERMISSION:
                setGalleryBitmap();
                break;
        }
    }

    public void removeImage(View view) {
        /*selectedImageWrap.setVisibility(View.GONE);
        fileUri = null;*/
    }
}
