package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.eclipsesource.json.JsonArray;

import gt.muni.chiantla.InformationFragment;
import gt.muni.chiantla.R;
import gt.muni.chiantla.animations.WidthAnimation;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.content.Income;

/**
 * Actividad que se encarga de cambiar y motrar cada uno de los fragmentos del presupuesto.
 * Se encarga de las animaciones cuando se cambia de un fragmento a otro. Crea los objetos
 * que contienen la información del presupuesto (income y expense).
 * @author Ludiverse
 * @author Innerlemonade
 * @see Income
 * @see Expense
 */
public class BudgetActivity extends RestConnectionActivity {
    private FragmentManager fragmentManager;

    // Filtros de ingresos
    private long incomeTypeId;
    private long incomeClassId;
    private String incomeClassName;

    // Filtros de gastos
    private boolean expensesProject;
    private long expensesProgramId;
    private String expensesProgramName;
    private long expensesProjectId;
    private boolean expenses;
    private boolean noActivity;

    // Posicion y views para animación de progreso
    private ImageView selectedIncomeCircle;
    private ImageView selectedExpensesCircle;
    private float incomeCirclePosition;
    private float expensesCirclePosition;
    private int lineWidth;
    
    // Contenedor de views para animación de progreso
    private View incomeProgress;
    private View expensesProgress;
    private View fragmentContainer;

    private int fragmentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.budget, true);
        setContentView(R.layout.activity_budget);

        incomeProgress = findViewById(R.id.progressIncome);
        selectedIncomeCircle = (ImageView) incomeProgress.findViewById(R.id.enabled);
        expensesProgress = findViewById(R.id.progressExpenses);
        selectedExpensesCircle = (ImageView) expensesProgress.findViewById(R.id.enabled);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        fragmentManager = getSupportFragmentManager();
        showTextInView();
    }

    /**
     * Crea los objetos para gastos e ingresos.
     * @param response Respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        if (response != null) {
            if (response.size() > 0)
                if (response.get(0).asObject().get("auxiliary_cod") != null) {
                    db.truncate(Income.TABLE);
                    Income.updateInstance(response, db);
                    db.addOrUpdateUpdate(Income.APP_NAME, Income.MODEL_NAME, 0, true);
                } else {
                    db.truncate(Expense.TABLE);
                    Expense.updateInstance(response, db);
                    db.addOrUpdateUpdate(Expense.APP_NAME, Expense.MODEL_NAME, 0, true);
                }
        }
        informationFragment();
    }

    /**
     * Actualiza datos antes de mostrar el primer fragmento.
     */
    protected void showTextInView() {
        boolean incomeUpdated = db.isUpdated(Income.APP_NAME, Income.MODEL_NAME, 0);
        boolean expensesUpdated = db.isUpdated(Expense.APP_NAME, Expense.MODEL_NAME, 0);
        if (!incomeUpdated) {
            paths = new String[]{"income/"};
            connect();
        } else {
            Income.createInstance(db);
        }
        if (!expensesUpdated) {
            paths = new String[]{"expenses/"};
            connect();
        } else {
            Expense.createInstance(db);
        }
        informationFragment();
    }

    /**
     * Muestra el primer fragmento
     */
    private void informationFragment() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        createFragment(BudgetInfoFragment.newInstance(), false, false);
        fragmentCount = 1;
    }

    /**
     * Wrapper de {@link gt.muni.chiantla.budget.BudgetActivity#goToNext(Bundle, View)} .
     * @param view la view que fue presionada.
     */
    public void goToNext(View view) {
        goToNext(null, view);
    }

    /**
     * Muestra el fragmento correspondiente a la view presionada.
     * @param bundle información extra enviada al presionar la view
     * @param view la view que fue presionada
     */
    public void goToNext(Bundle bundle, View view) {
        int id = view.getId();
        switch (id) {
            case R.id.budgetButton:
                createFragment(BudgetFragment.newInstance(), true, false);
                break;
            case R.id.incomeButton:
                showIncomeProgress();
                createFragment(IncomeFragment.newInstance(), true, false);
                expenses = false;
                break;
            case R.id.own_income_button:
                incomeTypeId = Income.COD_OWN_INCOME;
                createFragment(IncomeTypeFragment.newInstance(incomeTypeId));
                break;
            case R.id.other_income_button:
                incomeTypeId = 0;
                createFragment(IncomeTypeFragment.newInstance(incomeTypeId));
                break;
            case R.id.classButton:
                classFragment(bundle);
                break;
            case R.id.sectionButton:
                sectionFragment(bundle);
                break;
            case R.id.expensesButton:
                showExpensesProgress();
                createFragment(ExpensesFragment.newInstance(), true, false);
                expenses = true;
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
        fragmentCount ++;
    }

    /**
     * Muestra el fragmento de un Gasto final
     * @param bundle
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
     * @param bundle
     */
    private void projectFragment(Bundle bundle) {
        expensesProjectId = bundle.getLong("projectId");
        if (expensesProject) {
            String expensesProjectName = bundle.getString("projectName");
            createFragment(ExpensesActivitiesFragment.newInstance(
                    expensesProgramId,
                    expensesProjectId,
                    expensesProjectName
            ));
        } else {
            String[] strings = bundle.getStringArray("project");
            createFragment(ExpensesProjectFragment.newInstance(
                    expensesProject,
                    expensesProjectId,
                    expensesProgramId,
                    expensesProgramName,
                    strings
            ));
        }
    }

    /**
     * Muestra el fragmento de un programa de gastos
     * @param bundle
     */
    private void programFragment(Bundle bundle) {
        expensesProgramId = bundle.getLong("programId");
        expensesProgramName = bundle.getString("programName");
        createFragment(ExpensesProgramFragment.newInstance(
                expensesProject,
                expensesProgramId,
                expensesProgramName
        ));
    }

    /**
     * Muestra el fragmento de una sección de ingresos
     * @param bundle
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
     * @param bundle
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
     * Muestra la barra de progreso de ingresos.
     */
    private void showIncomeProgress() {
        showProgress(incomeProgress);
    }

    /**
     * Oculta la barra de progreso de ingresos
     */
    private void hideIncomeProgress() {
        hideProgress(incomeProgress);
    }

    /**
     * Muestra la barra de progreso de gastos
     */
    private void showExpensesProgress() {
        showProgress(expensesProgress);
        incomeProgress.setVisibility(View.INVISIBLE);
    }

    /**
     * Oculta la barra de progreso de gastos
     */
    private void hideExpensesProgress() {
        hideProgress(expensesProgress);
        incomeProgress.setVisibility(View.GONE);
    }

    /**
     * Oculta la barra de progreso enviada.
     * @param view la barra de progreso a ocultar.
     */
    private void hideProgress(View view) {
        // Quita el padding para que cuando la animación termine, las views se coloquen en
        // la misma posición que en donde terminó la animación
        fragmentContainer.setPadding(fragmentContainer.getPaddingLeft(),
                0,fragmentContainer.getPaddingRight(), fragmentContainer.getPaddingBottom());
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.collapse);
        view.startAnimation(animation);
        int height = view.getHeight();

        TranslateAnimation moveTransition = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, height,
                Animation.ABSOLUTE, 0);
        moveTransition.setDuration(200);
        moveTransition.setFillAfter(true);
        fragmentContainer.startAnimation(moveTransition);
    }

    /**
     * Oculta el icono de actividades de la barra de progreso.
     */
    private void hideActivities() {
        noActivity = true;

        View text = expensesProgress.findViewById(R.id.text2);
        View circle = expensesProgress.findViewById(R.id.state2);
        View line = expensesProgress.findViewById(R.id.line2);

        WidthAnimation cricleScale = new WidthAnimation(circle, 0);
        cricleScale.setDuration(200);

        WidthAnimation textScale = new WidthAnimation(text, 0);
        textScale.setDuration(200);

        WidthAnimation lineScale = new WidthAnimation(line, 0);
        lineScale.setDuration(200);

        circle.startAnimation(cricleScale);
        text.startAnimation(textScale);
        line.startAnimation(lineScale);
    }

    /**
     * Muestra el icono de actividades en la barra de progreso.
     */
    private void showActivities() {
        noActivity = false;

        View shownText = expensesProgress.findViewById(R.id.text3);
        View shownCircle = expensesProgress.findViewById(R.id.state3);
        View shownLine = expensesProgress.findViewById(R.id.line3);

        View text = expensesProgress.findViewById(R.id.text2);
        View circle = expensesProgress.findViewById(R.id.state2);
        View line = expensesProgress.findViewById(R.id.line2);

        WidthAnimation circleScale = new WidthAnimation(circle, shownCircle.getWidth());
        circleScale.setDuration(200);

        WidthAnimation textScale = new WidthAnimation(text, shownText.getWidth());
        textScale.setDuration(200);

        WidthAnimation lineScale = new WidthAnimation(line, shownLine.getWidth());
        lineScale.setDuration(200);

        circle.startAnimation(circleScale);
        text.startAnimation(textScale);
        line.startAnimation(lineScale);
    }

    /**
     * Muestra la barra de progreso enviada.
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
                        height,fragmentContainer.getPaddingRight(), fragmentContainer.getPaddingBottom());
                fragmentContainer.clearAnimation();
            }
        });
        fragmentContainer.startAnimation(moveTransition);
    }

    /**
     * Muestra un fragmento que tiene animación y que debe de animar la barra de progreso.
     * Wrapper de {@link gt.muni.chiantla.budget.BudgetActivity#createFragment(Fragment, boolean, boolean)}
     * @param fragment el fragmento que será mostrado
     */
    private void createFragment(Fragment fragment) {
        createFragment(fragment, true, true);
    }

    /**
     * Muestra un fragmento.
     * @param fragment el fragmento a mostrar
     * @param hasAnimation
     * @param animateProgress
     */
    private void createFragment(Fragment fragment, boolean hasAnimation, boolean animateProgress) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (hasAnimation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        if (animateProgress)
            moveProgress(false);
    }

    /**
     * Anima la barra de progreso.
     * @param back indica si debe animar hacia adelante o hacia atrás
     */
    private void moveProgress(boolean back) {
        float circlePosition;
        View circle;
        int backCount = fragmentCount - 3;
        if (noActivity && backCount > 1)
            backCount += 1;
        int position = getResources().getIdentifier(
                "state" + backCount,
                "id",
                getApplicationContext().getPackageName()
        );
        int lineNumber;
        if (back)
            lineNumber = backCount - 4;
        else
            lineNumber = backCount;
        int line = getResources().getIdentifier(
                "line" + lineNumber,
                "id",
                getApplicationContext().getPackageName()
        );
        int textNumber;
        if (back)
            if (noActivity && backCount == 3)
                textNumber = backCount - 2;
            else
                textNumber = backCount - 1;
        else
            textNumber = backCount;
        int text = getResources().getIdentifier(
                "text" + textNumber,
                "id",
                getApplicationContext().getPackageName()
        );
        ImageView lastCircle;
        View lastLine;
        View lastText;
        if (!expenses) {
            circlePosition = incomeCirclePosition;
            circle = selectedIncomeCircle;
            lastCircle = (ImageView) incomeProgress.findViewById(position);
            lastLine = incomeProgress.findViewById(line);
            lastText = incomeProgress.findViewById(text);
        } else {
            circlePosition = expensesCirclePosition;
            circle = selectedExpensesCircle;
            lastCircle = (ImageView) expensesProgress.findViewById(position);
            lastLine = expensesProgress.findViewById(line);
            lastText = expensesProgress.findViewById(text);
        }
        if (lineWidth == 0 && lastLine != null)
            lineWidth = lastLine.getWidth();
        if (circlePosition == 0)
            circlePosition = circle.getLeft();
        AlphaAnimation alphaAnimation;
        if (back) {
            lastCircle.setImageResource(R.mipmap.circulo_normal);
            alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        } else {
            lastCircle.setImageResource(R.mipmap.circulo_inhabilitado);
            alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
        }
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(200);

        AnimationSet animation = new AnimationSet(false);
        animation.setFillAfter(true);

        ScaleAnimation scale = new ScaleAnimation(1, 0.5f, 1, 0.5f);
        scale.setDuration(50);

        TranslateAnimation centerTransition = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0.25f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0.25f);
        centerTransition.setDuration(50);

        int distance = lineWidth;
        if (back)
            distance *= -1;
        float endX = getFinalXPosition(distance, circle, circlePosition);
        TranslateAnimation transition = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, circlePosition,
                Animation.RELATIVE_TO_PARENT, endX,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        transition.setDuration(200);
        transition.setAnimationListener(getCircleEndListener(circle, endX));

        animation.addAnimation(scale);
        animation.addAnimation(centerTransition);
        animation.addAnimation(transition);

        if (!expenses)
            incomeCirclePosition = endX;
        else
            expensesCirclePosition = endX;

        circle.startAnimation(animation);
        lastText.startAnimation(alphaAnimation);
        if (lastLine != null)
            lastLine.startAnimation(alphaAnimation);
    }

    /**
     * Obtiene la posición final del circulo que muestra la posición actual.
     * @param distance La distancia que el círculo fue movido.
     * @param selectedCircle El marcador de la posición actual.
     * @param circlePosition La posición actual del circulo.
     * @return La posición final del círculo.
     */
    private float getFinalXPosition(int distance, View selectedCircle, float circlePosition) {
        int circleWidth = selectedCircle.getWidth();
        if (distance < 0)
            circleWidth *= -1;
        float parentWidth = ((View) selectedCircle.getParent()).getWidth();
        double fullDistance = distance + circleWidth * 1.0f;
        if (noActivity && fragmentCount == 3)
            parentWidth -= fullDistance;
        float proportion = (float) fullDistance / parentWidth;
        return circlePosition + proportion;
    }

    /**
     * Crea un nuevo {@link android.view.animation.Animation.AnimationListener} que agranda
     * el circulo que marca la posición final
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
     * Muestra u oculta la barra de progreso y el ícono de actividades dependiendo de
     * la posición en la que se encuentra el usuario.
     */
    @Override
    public void onBackPressed() {
        switch (fragmentCount) {
            case 4:
                if (noActivity)
                    showActivities();
            default:
                moveProgress(true);
                break;
            case 3:
                if (!expenses)
                    hideIncomeProgress();
                else
                    hideExpensesProgress();
                break;
            case 0:
            case 1:
            case 2:
                break;
        }
        fragmentManager.popBackStack();
        if (fragmentCount == 1) {
            super.onBackPressed();
        }
        fragmentCount --;
    }

    /**
     * Muestra el popup con información de la sección, si presiona el ícono.
     * @see {@link android.app.Activity#onOptionsItemSelected(MenuItem)}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.about_info:
                fragmentManager = getSupportFragmentManager();
                informationFragment = InformationFragment.newInstance(
                        R.string.budget_info_title,
                        R.string.budget_info_content
                );
                informationFragment.show(fragmentManager, "dialog");
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    /**
     * @see {@link android.app.Activity#onCreateOptionsMenu(Menu)}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        return true;
    }
}
