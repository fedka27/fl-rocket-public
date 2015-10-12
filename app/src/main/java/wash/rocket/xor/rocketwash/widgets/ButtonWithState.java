package wash.rocket.xor.rocketwash.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Button;

import wash.rocket.xor.rocketwash.R;

public class ButtonWithState extends Button {

    private static final int[] STATE_SELECT = {R.attr.selected};
    private boolean mSelected;

    private Handler mHandler = new Handler();

    public ButtonWithState(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ButtonWithState(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ButtonWithState(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ButtonWithState(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setDuplicateParentStateEnabled(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ButtonState, defStyleAttr, defStyleRes);
        mSelected = a.getBoolean(R.styleable.ButtonState_selected, false);
        a.recycle();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isSelected()) {
            mergeDrawableStates(drawableState, STATE_SELECT);
        }
        return drawableState;
    }

    public void setSelected(boolean value) {
        mSelected = value;
        //refreshDrawableState();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshDrawableState();
            }
        });
    }

    public boolean isSelected() {
        return mSelected;
    }
}
