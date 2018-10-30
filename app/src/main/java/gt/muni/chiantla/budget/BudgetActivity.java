package gt.muni.chiantla.budget;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;

import java.util.Calendar;

import gt.muni.chiantla.R;
import gt.muni.chiantla.TutorialFragment;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.animations.WidthAnimation;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.content.Income;
import gt.muni.chiantla.notifications.NewNotificationActivity;

/**
 * Actividad que se encarga de cambiar y motrar cada uno de los fragmentos del presupuesto.
 * Se encarga de las animaciones cuando se cambia de un fragmento a otro. Crea los objetos
 * que contienen la información del presupuesto (income y expense).
 *
 * @author Ludiverse
 * @author Innerlemonade
 * @see Income
 * @see Expense
 */
@BindingMethods({
        @BindingMethod(type = android.widget.ProgressBar.class,
                attribute = "android:progressBackgroundTint",
                method = "setProgressBackgroundTintList"),
})
public class BudgetActivity extends RestConnectionActivity implements PopupMenu
        .OnMenuItemClickListener {
    private final int SHARE_IMAGE = 0;
    private final int SHOW_NOTIFICATION = 1;
    private final int STORAGE_PERMISSION = 0;
    private FragmentManager fragmentManager;
    private String year;

    // Filtros de ingresos
    private String incomeTypeId;
    private long incomeClassId;
    private String incomeClassName;

    // Filtros de gastos
    private boolean expensesProject;
    private long expensesProgramId;
    private String expensesProgramName;
    private long expensesProjectId;
    private boolean expenses;

    private View incomeProgress;
    private View expensesProgress;

    private View fragmentContainer;

    private BudgetAnimation animation;

    // Guarda la vista de la cual se esta mostrando el menu
    private View currentMenu;

    private boolean incomeUpdated;
    private boolean expensesUpdated;
    private int permissionMethod;
    private View permissionClickedView;

    /**
     * Binding Adapter that sets the text size in SP
     *
     * @param view the TextView that will change its text size
     * @param size an int that is the new size in SP
     */
    @BindingAdapter("android:textSize")
    public static void setTextSize(TextView view, int size) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) size);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_budget);
        createOptionsMenu = true;

        String currentYear = Calendar.getInstance().get(Calendar.YEAR) + "";
        if (getIntent().getExtras() != null)
            year = getIntent().getExtras().getString("year", currentYear);
        else
            year = currentYear;
        animation = new BudgetAnimation();
        fragmentContainer = findViewById(R.id.fragmentContainer);
        fragmentManager = getSupportFragmentManager();
        incomeProgress = findViewById(R.id.progressIncome);
        expensesProgress = findViewById(R.id.progressExpenses);
        hideProgress(expensesProgress, false);
        hideProgress(incomeProgress, false);
        showTextInView();
    }

    /**
     * Crea los objetos para gastos e ingresos.
     *
     * @param response Respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        if (response != null) {
            if (response.size() > 0) {
                int currentYear = Integer.parseInt(year);
                if (response.get(0).asObject().get("auxiliary_cod") != null) {
                    db.delete(Income.TABLE, Income.KEY_YEAR, year);
                    Income.updateInstance(response, db, currentYear);
                    db.addOrUpdateUpdate(Income.APP_NAME, Income.MODEL_NAME, currentYear, true);
                    incomeUpdated = true;
                } else {
                    db.delete(Expense.TABLE, Expense.KEY_YEAR, year);
                    Expense.updateInstance(response, db, currentYear);
                    db.addOrUpdateUpdate(Expense.APP_NAME, Expense.MODEL_NAME, currentYear, true);
                    expensesUpdated = true;
                }
            }
        } else {
            if (!incomeUpdated) incomeUpdated = true;
            else if (!expensesUpdated) expensesUpdated = true;
        }
        if (incomeUpdated && expensesUpdated)
            createFragment(BudgetFragment.newInstance(), false, false);
    }

    /**
     * Actualiza datos antes de mostrar el primer fragmento.
     */
    protected void showTextInView() {
        int currentYear = Integer.parseInt(year);
        incomeUpdated = db.isUpdated(Income.APP_NAME, Income.MODEL_NAME, currentYear);
        expensesUpdated = db.isUpdated(Expense.APP_NAME, Expense.MODEL_NAME, currentYear);
        if (!incomeUpdated) {
            paths = new String[]{"income/" + year + "/"};
            connect();
        } else {
            Income.createInstance(db, currentYear);
        }
        if (!expensesUpdated) {
            paths = new String[]{"expenses/" + year + "/"};
            connect();
        } else {
            Expense.createInstance(db, currentYear);
        }
        if (incomeUpdated && expensesUpdated)
            createFragment(BudgetFragment.newInstance(), false, false);
    }

    /**
     * Wrapper de {@link gt.muni.chiantla.budget.BudgetActivity#goToNext(Bundle, View)} .
     *
     * @param view la view que fue presionada.
     */
    public void goToNext(View view) {
        goToNext(null, view);
    }

    /**
     * Muestra el fragmento correspondiente a la view presionada.
     *
     * @param bundle información extra enviada al presionar la view
     * @param view   la view que fue presionada
     */
    public void goToNext(Bundle bundle, View view) {
        int id = view.getId();
        switch (id) {
            case R.id.incomeButton:
                showIncomeProgress();
                createFragment(IncomeFragment.newInstance(), true);
                expenses = false;
                showIncomeProgress();
                break;
            case R.id.own_income_button:
                incomeTypeId = Income.OWN_INCOME;
                createFragment(IncomeTypeFragment.newInstance(incomeTypeId));
                break;
            case R.id.other_income_button:
                incomeTypeId = null;
                createFragment(IncomeTypeFragment.newInstance(incomeTypeId));
                break;
            case R.id.classButton:
                classFragment(bundle);
                break;
            case R.id.sectionButton:
                sectionFragment(bundle);
                break;
            case R.id.expensesButton:
                createFragment(ExpensesFragment.newInstance(), true);
                expenses = true;
                showExpensesProgress();
                break;
            case R.id.projects_button:
                expensesProject = true;
                createFragment(ExpensesProjectsFragment.newInstance(expensesProject));
                break;
            case R.id.not_projects_button:
                expensesProject = false;
                hideActivities();
                createFragment(ExpensesProjectsFragment.newInstance(expensesProject));
                break;
            case R.id.programButton:
                programFragment(bundle);
                break;
            case R.id.projectButton:
                projectFragment(bundle);
                break;
            case R.id.activityButton:
                expenseItemFragment(bundle);
                break;
        }
    }

    /**
     * Muestra el fragmento de un Gasto final
     *
     * @param bundle parametros del gasto
     */
    private void expenseItemFragment(Bundle bundle) {
        long expensesActivityId = bundle.getLong("activityId");
        String[] strings = bundle.getStringArray("activity");
        createFragment(ExpensesProjectFragment.newInstance(
                expensesProject,
                expensesActivityId,
                expensesProjectId,
                expensesProgramId,
                expensesProgramName,
                strings
        ));
    }

    /**
     * Muestra un fragmento de un proyecto o una actividad, dependiendo del tipo de gasto
     *
     * @param bundle parametros del proyecto o actividad
     */
    private void projectFragment(Bundle bundle) {
        expensesProjectId = bundle.getLong("projectId");
        String expensesProjectName = bundle.getString("projectName");
        createFragment(ExpensesActivitiesFragment.newInstance(
                expensesProject,
                expensesProgramId,
                expensesProjectId,
                expensesProjectName
        ));
    }

    /**
     * Muestra el fragmento de un programa de gastos
     *
     * @param bundle parametros del programa
     */
    private void programFragment(Bundle bundle) {
        expensesProgramId = bundle.getLong("programId");
        expensesProgramName = bundle.getString("programName");
        if (expensesProject) {
            createFragment(ExpensesProgramFragment.newInstance(
                    true,
                    expensesProgramId,
                    expensesProgramName
            ));
        } else {
            createFragment(ExpensesActivitiesFragment.newInstance(
                    false,
                    expensesProgramId,
                    0,
                    expensesProgramName
            ));
        }
    }

    /**
     * Muestra el fragmento de una sección de ingresos
     *
     * @param bundle parametros de la seccion
     */
    private void sectionFragment(Bundle bundle) {
        long incomeSectionId = bundle.getLong("sectionId");
        String incomeSectionName = bundle.getString("sectionName");
        String incomeSectionValue = bundle.getString("sectionValue");
        createFragment(IncomeSectionFragment.newInstance(
                incomeTypeId,
                incomeClassId,
                incomeClassName,
                incomeSectionId,
                incomeSectionName,
                incomeSectionValue
        ));
    }

    /**
     * Muestra el fragmento de una clase de ingreso
     *
     * @param bundle parametros de la clase
     */
    private void classFragment(Bundle bundle) {
        incomeClassId = bundle.getLong("classId");
        incomeClassName = bundle.getString("className");
        createFragment(IncomeClassFragment.newInstance(
                incomeTypeId,
                incomeClassId,
                incomeClassName
        ));
    }

    /**
     * Oculta la barra de progreso enviada.
     *
     * @param view    la barra de progreso a ocultar.
     * @param animate si la barra de progreso debe ser animada
     */
    private void hideProgress(View view, boolean animate) {
        // Quita el padding para que cuando la animación termine, las views se coloquen en
        // la misma posición que en donde terminó la animación
        fragmentContainer.setPadding(fragmentContainer.getPaddingLeft(),
                0, fragmentContainer.getPaddingRight(), fragmentContainer.getPaddingBottom());
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.collapse);
        if (!animate) animation.setDuration(0);
        view.startAnimation(animation);
        int height = view.getHeight();

        TranslateAnimation moveTransition = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, height,
                Animation.ABSOLUTE, 0);
        if (animate) moveTransition.setDuration(200);
        moveTransition.setFillAfter(true);
        fragmentContainer.startAnimation(moveTransition);
    }

    /**
     * Oculta el icono de actividades de la barra de progreso.
     */
    public void hideActivities() {
        animation.hideStep(2);
    }

    /**
     * Muestra el icono de actividades en la barra de progreso.
     */
    public void showActivities() {
        animation.showStep(2);
    }

    /**
     * Muestra la barra de progreso enviada.
     *
     * @param view la barra de progreso a mostrar.
     */
    private void showProgress(final View view) {
        view.setVisibility(View.VISIBLE);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.expand);
        view.startAnimation(animation);
        final int height = view.getHeight();

        TranslateAnimation moveTransition = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, height);
        moveTransition.setDuration(200);
        moveTransition.setFillAfter(true);
        moveTransition.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // Agrega padding para que cuando la animación termine, las views se coloquen en
                // la misma posición que en donde terminó la animación
                fragmentContainer.setPadding(fragmentContainer.getPaddingLeft(),
                        height, fragmentContainer.getPaddingRight(), fragmentContainer
                                .getPaddingBottom());
                fragmentContainer.clearAnimation();
            }
        });
        fragmentContainer.startAnimation(moveTransition);
    }

    /**
     * Muestra un fragmento que tiene animación. Wrapper de
     * {@link gt.muni.chiantla.budget.BudgetActivity#createFragment(Fragment, boolean)}
     *
     * @param fragment el fragmento que será mostrado
     */
    private void createFragment(Fragment fragment) {
        createFragment(fragment, true);
    }

    /**
     * Muestra un fragmento, agregandolo al backstack.
     *
     * @param fragment     el fragmento a mostrar
     * @param hasAnimation si el fragmento se mostrara con una transicion
     */
    private void createFragment(Fragment fragment, boolean hasAnimation) {
        createFragment(fragment, hasAnimation, true);
    }

    /**
     * Muestra un fragmento.
     *
     * @param fragment     el fragmento a mostrar
     * @param hasAnimation si el fragmento se mostrara con una transicion
     */
    private void createFragment(Fragment fragment, boolean hasAnimation,
                                boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (hasAnimation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        if (addToBackStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        nextTutorial();
    }

    /**
     * Muestra la actividad de FAQ, dependiendo de la vista guardada
     */
    public void showFAQ() {
        String name = (String) ((TextView) this.currentMenu.findViewById(R.id.card_name)).getText();
        Intent intent = new Intent(this, BudgetFAQActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    /**
     * Checks
     *
     * @param methodToCall si se compartira una imagen o se llenara un reporte
     */
    public void checkForPermission(int methodToCall) {
        checkForPermission(methodToCall, null);
    }

    /**
     * Revisa permisos y luego llama a un metodo
     *
     * @param methodToCall si se compartira una imagen o se llenara un reporte
     * @param clickedView  La view que fue presionada
     */
    public void checkForPermission(int methodToCall, View clickedView) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                this.permissionMethod = methodToCall;
                this.permissionClickedView = clickedView;
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION);
                return;
            }
        }
        callAfterPermission(methodToCall, clickedView);
    }

    /**
     * Metodo que se llama cuando el usuario responde a la solicitud de permisos
     *
     * @param requestCode  el tipo de solicitud
     * @param permissions  los permisos
     * @param grantResults las respuestas
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION:
                if (grantResults.length != 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callAfterPermission(this.permissionMethod, this.permissionClickedView);
                }
                break;
        }
    }

    /**
     * El metodo que es llamado si el usuario dio permiso (en algun momento)
     *
     * @param permissionMethod      si se compartira una imagen o se llenara un reporte
     * @param permissionClickedView la view que fue presionada
     */
    private void callAfterPermission(int permissionMethod, View permissionClickedView) {
        switch (permissionMethod) {
            case SHARE_IMAGE:
                this.shareImage(permissionClickedView);
                break;
            case SHOW_NOTIFICATION:
                this.showNotificationActivity();
                break;
        }
    }

    /**
     * Genera la imagen para compartir
     *
     * @param view el boton presionado
     */
    public void shareImage(View view) {
        this.currentMenu = (View) view.getParent().getParent();
        Bitmap bitmap = generateImage(this.currentMenu);
        TextView name = this.currentMenu.findViewById(R.id.card_name);
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                name.getText() + "", null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        sendIntent.setType("image/*");
        startActivity(sendIntent);
    }

    /**
     * Sets the theme. Changes the action bar and background color
     *
     * @param resId the new color
     */
    @Override
    public void setTheme(int resId) {
        super.setTheme(resId);
        int primaryColor = getThemeAttr(R.attr.colorPrimary);
        changeActionBarColor(primaryColor);
        if (incomeProgress != null)
            incomeProgress.setBackgroundColor(primaryColor);
        if (expensesProgress != null)
            expensesProgress.setBackgroundColor(primaryColor);
    }

    /**
     * Changes the ActionBar color
     *
     * @param resourceColor the new color
     */
    private void changeActionBarColor(int resourceColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(resourceColor);
        }

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(resourceColor));
    }

    /**
     * Gets an attribute of the current theme
     *
     * @param attrId the id of the attribute to return
     * @return the attribute
     */
    private int getThemeAttr(int attrId) {
        final TypedValue value = new TypedValue();
        getTheme().resolveAttribute(attrId, value, true);
        return value.data;
    }

    /**
     * Shows the popup menu when info icon is clicked
     *
     * @param view the info icon view
     */
    public void showBudgetPopupMenu(View view) {
        this.currentMenu = (View) view.getParent().getParent();
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.setGravity(Gravity.END);
        popup.inflate(R.menu.budget_info_menu);
        popup.show();
    }

    /**
     * Shows the new notification activity, pre-filled with the budget item information
     */
    private void showNotificationActivity() {
        TextView name = this.currentMenu.findViewById(R.id.card_name);
        Bitmap bitmap = generateImage(this.currentMenu);
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                name.getText() + "", null);
        Uri bitmapUri = Uri.parse(bitmapPath);

        String cardName = (String) name.getText();
        Intent intent = new Intent(this, NewNotificationActivity.class);
        intent.putExtra("name", cardName);
        intent.putExtra("image", bitmapUri.toString());
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    /**
     * Generates an image from a budget item
     *
     * @param view the card of the budget item
     */
    private Bitmap generateImage(View view) {

        View imageParent = getLayoutInflater().inflate(R.layout.section_budget_image, null);
        this.setImageVariables(imageParent);

        ImageView partialContainer = imageParent.findViewById(R.id.partialContainer);
        this.toggleCardItemsForImage(view, View.GONE);
        Bitmap partialImage = Utils.generateImage(this, view, true);
        partialContainer.setImageBitmap(partialImage);
        this.toggleCardItemsForImage(view, View.VISIBLE);

        return Utils.generateImage(this, imageParent, false);
    }

    /**
     * Sets the visibility for items of the card that do not appear in the generated images
     *
     * @param card       the card
     * @param visibility the new visibility
     */
    private void toggleCardItemsForImage(View card, int visibility) {
        card.findViewById(R.id.shareButton).setVisibility(visibility);
        card.findViewById(R.id.infoButton).setVisibility(visibility);
        card.findViewById(R.id.nextButton).setVisibility(visibility);
    }

    /**
     * Sets the variables for the generated images
     *
     * @param view the view that will be converted to image
     */
    private void setImageVariables(View view) {
        TextView typeText = view.findViewById(R.id.budgetType);
        View mainCard = view.findViewById(R.id.mainCard);
        if (expenses) {
            typeText.setText(R.string.expenses);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mainCard.setBackgroundTintList(getResources().getColorStateList(R.color
                        .colorExpensesPrimary));
            }
        } else {
            typeText.setText(R.string.income);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mainCard.setBackgroundTintList(getResources().getColorStateList(R.color
                        .colorIncomePrimary));
            }
        }
    }

    /**
     * Sets the listeners for cards. Couldnt set the listeners in xml because the cards are inside
     * a ListView
     *
     * @param view the view to set listeners for.
     */
    protected void setViewListeners(View view) {
        ImageView shareButton = view.findViewById(R.id.shareButton);
        ImageView infoButton = view.findViewById(R.id.infoButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View clickedView) {
                checkForPermission(SHARE_IMAGE, clickedView);
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View clickedView) {
                showBudgetPopupMenu(clickedView);
            }
        });
    }

    /**
     * Listener for the popup menu
     *
     * @param item the clicked item
     * @return true if the listener was handled
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.get_info:
                showFAQ();
                return true;
            case R.id.ask_question:
                checkForPermission(SHOW_NOTIFICATION);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    /**
     * Muestra la barra de progreso de ingresos.
     */
    public void showIncomeProgress() {
        showProgress(incomeProgress);
        animation.setProgress(incomeProgress);
    }

    /**
     * Oculta la barra de progreso de ingresos
     */
    public void hideIncomeProgress() {
        hideProgress(incomeProgress, true);
        animation.unsetProgress();
    }

    /**
     * Muestra la barra de progreso de gastos
     */
    public void showExpensesProgress() {
        showProgress(expensesProgress);
        animation.setProgress(expensesProgress);
    }

    /**
     * Oculta la barra de progreso de gastos
     */
    public void hideExpensesProgress() {
        hideProgress(expensesProgress, true);
        animation.unsetProgress();
    }

    /**
     * Mueve la barra de progreso a un paso
     *
     * @param stepNumber el paso al que se movera
     */
    public void moveProgress(int stepNumber) {
        animation.moveProgress(stepNumber);
    }

    /**
     * Obtiene el id del recurso array del tutorial a mostrar en esta actividad
     *
     * @return el id del tutorial
     */
    @Override
    protected Integer getTutorialResourceId() {
        return R.array.tutorial_budget;
    }

    /**
     * Obtiene la cantidad de tutoriales a mostrar en la actividad
     *
     * @return la cantidad de tutoriales
     */
    @Override
    protected Integer getTutorialCount() {
        return 3;
    }

    /**
     * Obtiene el nombre del setting en donde se guarda si ya se realizo el tutorial
     *
     * @return el nombre del setting
     */
    @Override
    protected String getTutorialSettingName() {
        return "BudgetTutorial";
    }

    /**
     * Obtiene el tutorial que se debe mostrar
     *
     * @return el tutorial a mostrar
     */
    @Override
    protected View getCurrentTutorialView() {
        switch (currentTutorial) {
            case 0:
                return null;
            case 1:
                return findViewById(R.id.shareButton);
            case 2:
                return findViewById(R.id.infoButton);
        }
        return null;
    }

    /**
     * Obtiene la posicion de la flecha del tutorial actual
     *
     * @return la posicion de la flecha del tutorial
     */
    @Override
    protected Integer getCurrentTutorialArrowPosition() {
        switch (currentTutorial) {
            case 0:
                return TutorialFragment.NO_ARROW;
            case 1:
                return TutorialFragment.ARROW_TOP_RIGHT;
            case 2:
                return TutorialFragment.ARROW_TOP_RIGHT;
        }
        return null;
    }


    private class BudgetAnimation {
        private ImageView currentCircle;
        private TextView currentText;
        private View currentLine;
        private ImageView progressIndicator;
        private TextView subtitle;

        private ViewGroup currentCirclesContainer;
        private ViewGroup currentTextsContainer;
        private float currentIndicatorPosition;
        private int lineWidth;

        private Integer currentStep;

        /**
         * Inicializa una barra de progreso
         *
         * @param currentProgress la barra de progreso
         */
        public void setProgress(View currentProgress) {
            progressIndicator = currentProgress.findViewById(R.id.enabled);
            currentIndicatorPosition = progressIndicator.getLeft();
            currentCirclesContainer = currentProgress.findViewById(R.id.circles);
            currentTextsContainer = currentProgress.findViewById(R.id.texts);
            lineWidth = currentCirclesContainer.findViewById(R.id.line0).getWidth();
            subtitle = currentProgress.findViewById(R.id.sectionTitle);
            setCurrentStep(0, true);
        }

        /**
         * Reinicia el progreso actual
         */
        void unsetProgress() {
            progressIndicator = null;
            currentStep = null;
            currentIndicatorPosition = 0;
        }

        /**
         * Anima la barra de progreso
         *
         * @param stepNumber el paso al que se movera
         */
        void moveProgress(int stepNumber) {
            if (progressIndicator != null) {
                boolean forward = currentStep <= (stepNumber * 2);
                if (!forward && currentStep != 0) {
                    setCurrentStep(stepNumber * 2, false);
                }

                if (stepNumber != 0 || !forward) {
                    float distanceMoved = getXDistance(lineWidth, currentCircle);
                    if (!forward) distanceMoved *= -1;
                    float newIndicatorPosition = currentIndicatorPosition + distanceMoved;

                    AnimationSet animation = buildCircleAnimation(progressIndicator,
                            currentIndicatorPosition, newIndicatorPosition);
                    float newAlpha = (forward) ? 1 : 0.5f;

                    progressIndicator.startAnimation(animation);
                    if (currentLine != null) currentLine.setAlpha(newAlpha);

                    if (forward) {
                        currentCircle.setImageResource(R.drawable.circulo_normal);
                        setCurrentStep(stepNumber * 2, true);
                        currentText.setAlpha(newAlpha);
                    } else {
                        currentCircle.setImageResource(R.drawable.circulo_inhabilitado);
                    }
                    currentIndicatorPosition = newIndicatorPosition;

                    currentText.setTextColor(getResources().getColor(R.color.textColor));

                    TextView prevText = getPrevText(forward);
                    if (prevText != null) {
                        prevText.setTextColor(getResources().getColor(R.color.white));
                        if (!forward) {
                            prevText.setAlpha(newAlpha);
                        }
                    }
                }
                changeSubtitle();
            }
        }

        /**
         * Obtiene el texto del paso anterior
         *
         * @param forward si el progreso se esta moviendo hacia adelante
         * @return El view que contiene el texto del paso anterior
         */
        private TextView getPrevText(boolean forward) {
            int textNumber = currentStep / 2;
            int prevTextNumber;
            if (forward) {
                prevTextNumber = textNumber - 1;
            } else {
                prevTextNumber = textNumber + 1;
            }
            TextView prevText = (TextView) currentTextsContainer.getChildAt(prevTextNumber);
            if (prevText != null && prevText.getVisibility() == View.GONE) {
                if (forward)
                    prevText = (TextView) currentTextsContainer.getChildAt(prevTextNumber - 1);
                else
                    prevText = (TextView) currentTextsContainer.getChildAt(prevTextNumber + 1);
            }
            return prevText;
        }

        /**
         * Cambia el subtitulo del presupuesto (ie. "Proyecto", "Obra"...)
         */
        private void changeSubtitle() {
            subtitle.setText(currentText.getText());
        }

        private void setCurrentStep(int currentStep, boolean forward) {
            if (this.currentStep == null || this.currentStep != currentStep) {
                setStepVariables(currentStep, forward);
                this.currentStep = currentStep;
            }
        }

        /**
         * Coloca las variables para un paso
         *
         * @param currentStep el paso actual
         * @param forward     si el progreso se esta haciendo hacia adelante
         */
        private void setStepVariables(int currentStep, boolean forward) {
            currentCircle = (ImageView) currentCirclesContainer.getChildAt(currentStep + 1);
            currentLine = currentCirclesContainer.getChildAt(currentStep + 2);
            currentText = (TextView) currentTextsContainer.getChildAt(currentStep / 2);
            if (currentCircle != null && currentCircle.getVisibility() == View.GONE) {
                if (currentStep != this.currentStep) {
                    setCurrentStep(currentStep + 2, forward);
                }
            }
        }

        /**
         * Crea la animacion para el circulo de progreso
         *
         * @param circle el circulo al que se movera el indicador
         * @param from   la posicion desde la cual se movera el indicador
         * @param to     la posicion hasta la cual se movera el indicador
         * @return el set de animaciones a realizar
         */
        private AnimationSet buildCircleAnimation(View circle, float from, float to) {
            TranslateAnimation transition = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, from,
                    Animation.RELATIVE_TO_PARENT, to,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);
            transition.setDuration(200);
            transition.setAnimationListener(getCircleEndListener(circle, to));

            ScaleAnimation scale = new ScaleAnimation(1, 0.5f, 1, 0.5f);
            scale.setDuration(50);

            TranslateAnimation centerTransition = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0.25f,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0.25f);
            centerTransition.setDuration(50);

            AnimationSet animation = new AnimationSet(false);
            animation.setFillAfter(true);
            animation.addAnimation(scale);
            animation.addAnimation(centerTransition);
            animation.addAnimation(transition);

            return animation;
        }

        /**
         * Obtiene la posición final del circulo que muestra la posición actual.
         *
         * @param distance       La distancia que el círculo fue movido.
         * @param selectedCircle El marcador de la posición actual.
         * @return La posición final del círculo.
         */
        private float getXDistance(int distance, View selectedCircle) {
            int circleWidth = selectedCircle.getWidth();
            if (distance < 0)
                circleWidth *= -1;
            float parentWidth = ((View) selectedCircle.getParent()).getWidth();
            double fullDistance = distance + circleWidth * 1.0;
            float proportion = (float) fullDistance / parentWidth;
            return proportion;
        }

        /**
         * Crea un nuevo {@link android.view.animation.Animation.AnimationListener} que agranda
         * el circulo que marca la posición final
         *
         * @param selectedCircle el view del circulo de la posición actual
         * @param circlePosition la ubicación del circulo
         * @return El {@link android.view.animation.Animation.AnimationListener} .
         */
        private Animation.AnimationListener getCircleEndListener(final View selectedCircle,
                                                                 final float circlePosition) {
            Animation.AnimationListener listener = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    AnimationSet animation = new AnimationSet(false);
                    animation.setFillAfter(true);
                    ScaleAnimation scale = new ScaleAnimation(0.5f, 1, 0.5f, 1);
                    scale.setDuration(50);
                    TranslateAnimation transition = new TranslateAnimation(
                            Animation.RELATIVE_TO_PARENT, circlePosition,
                            Animation.RELATIVE_TO_PARENT, circlePosition,
                            Animation.RELATIVE_TO_SELF, 0.25f,
                            Animation.RELATIVE_TO_SELF, 0);
                    transition.setDuration(50);
                    animation.addAnimation(scale);
                    animation.addAnimation(transition);
                    selectedCircle.startAnimation(animation);
                }
            };
            return listener;
        }

        /**
         * Oculta un paso en la barra de progreso
         *
         * @param stepNumber el paso a ocultar. Nota: en el caso de esta aplicacion siembre sera 2,
         *                   se deja el parametro para flexibilidad.
         */
        void hideStep(int stepNumber) {
            int circleNumber = stepNumber * 2 + 1;
            View text = currentCirclesContainer.getChildAt(circleNumber);
            View circle = currentCirclesContainer.getChildAt(circleNumber + 1);
            View line = currentTextsContainer.getChildAt(stepNumber);

            WidthAnimation circleScale = new WidthAnimation(circle, 0);
            circleScale.setDuration(200);
            WidthAnimation textScale = new WidthAnimation(text, 0);
            textScale.setDuration(200);
            WidthAnimation lineScale = new WidthAnimation(line, 0);
            lineScale.setDuration(200);

            circle.startAnimation(circleScale);
            text.startAnimation(textScale);
            line.startAnimation(lineScale);
            circle.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
            line.setVisibility(View.GONE);

            currentIndicatorPosition = 0;
        }

        /**
         * Muestra un paso en la barra de progreso
         *
         * @param stepNumber el paso a mostrar. Nota: en el caso de esta aplicacion siembre sera 2,
         *                   se deja el parametro para flexibilidad.
         */
        void showStep(int stepNumber) {
            int circleNumber = stepNumber * 2 + 1;
            View shownText = currentCirclesContainer.getChildAt(circleNumber + 2);
            View shownCircle = currentCirclesContainer.getChildAt(circleNumber + 3);
            View shownLine = currentTextsContainer.getChildAt(stepNumber + 1);

            View text = currentCirclesContainer.getChildAt(circleNumber);
            View circle = currentCirclesContainer.getChildAt(circleNumber + 1);
            View line = currentTextsContainer.getChildAt(stepNumber);

            WidthAnimation circleScale = new WidthAnimation(circle, shownCircle.getWidth());
            circleScale.setDuration(200);
            WidthAnimation textScale = new WidthAnimation(text, shownText.getWidth());
            textScale.setDuration(200);
            WidthAnimation lineScale = new WidthAnimation(line, shownLine.getWidth());
            lineScale.setDuration(200);

            circle.startAnimation(circleScale);
            text.startAnimation(textScale);
            line.startAnimation(lineScale);
            circle.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
    }

}