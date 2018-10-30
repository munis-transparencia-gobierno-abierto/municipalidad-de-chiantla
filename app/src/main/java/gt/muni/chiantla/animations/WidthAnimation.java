package gt.muni.chiantla.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Animación de ancho para una View. La animación se hace sobre la view (es permanente).
 * Código derivado de <a href="http://stackoverflow.com/a/17200611">Stack Overflow</a>
 *
 * @author Ludiverse
 * @author Innerlemonade
 * @see Animation
 */
public class WidthAnimation extends Animation {
    private int mWidth;
    private int mStartWidth;
    private View mView;

    public WidthAnimation(View view, int width) {
        mView = view;
        mWidth = width;
        mStartWidth = view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int currentAnimWidth = (int) ((mWidth - mStartWidth) * interpolatedTime);
        int newWidth = mStartWidth + currentAnimWidth;
        mView.getLayoutParams().width = newWidth;
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
