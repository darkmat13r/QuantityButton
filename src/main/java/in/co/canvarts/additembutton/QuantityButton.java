package in.co.canvarts.additembutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by avinash.kumawat on 8/26/2016.
 */
public class QuantityButton extends LinearLayout implements Animation.AnimationListener, View.OnClickListener {


    private static final String TAG = QuantityButton.class.getSimpleName();
    private int widgetColor;
    private int maxQty;
    private static final int DEFAULT_MAX_QTY = 5;
    private ImageButton btnAddQty;
    private ImageButton btnSubQty;
    private TextView qtyCount;

    private int qty = 0;
    private int initialQty;
    private int defaultColor;
    private int disabledColor;
    private ValueChangeListener mListner;

    public QuantityButton(Context context) {
        super(context);
    }

    public QuantityButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public QuantityButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public QuantityButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(isInEditMode()){
            init(attrs);
        }
    }

    private void init(AttributeSet attrs) {

        inflate(getContext(), R.layout.quantity_add_button, this);
        TypedArray ref = getContext().obtainStyledAttributes(attrs, R.styleable.QuantityButton);

        widgetColor = ref.getColor(R.styleable.QuantityButton_color, -1);
        maxQty = ref.getInt(R.styleable.QuantityButton_maxQty, -1);
        initialQty = ref.getInt(R.styleable.QuantityButton_initialQty, -1);
        ref.recycle();
        defaultColor = getContext().getResources().getColor(R.color.material_grey_900);
        disabledColor = getContext().getResources().getColor(R.color.material_grey_600);

        btnAddQty = (ImageButton) findViewById(R.id.btn_add);
        btnSubQty = (ImageButton) findViewById(R.id.btn_sub);
        qtyCount = (TextView) findViewById(R.id.qty_count);
        btnAddQty.setOnClickListener(this);
        btnSubQty.setOnClickListener(this);

        postSetup();


    }


    public void addValueChangeListner(ValueChangeListener mListner) {
        this.mListner = mListner;
    }

    private void postSetup() {
        if (initialQty > 0) {
            qty = initialQty;
        }
        if (maxQty < 0) {
            maxQty = DEFAULT_MAX_QTY;
        }
        if (widgetColor == -1) {

            widgetColor = defaultColor;
        }
        qtyCount.setTextColor(widgetColor);
        setColors();


    }
    public void removeListener(){
        if(mListner != null){
            mListner = null;
        }
    }

    private void setColors() {


        if (qty == 0) {
            btnSubQty.setEnabled(false);
            btnSubQty.setColorFilter(disabledColor);
            btnAddQty.setColorFilter(widgetColor);
        }
        if (maxQty == qty) {
            btnAddQty.setEnabled(false);
            btnAddQty.setColorFilter(disabledColor);
            btnSubQty.setColorFilter(widgetColor);
        }
        if (qty > 0 && qty < maxQty) {
            btnSubQty.setEnabled(true);
            btnAddQty.setColorFilter(widgetColor);
            btnAddQty.setEnabled(true);
            btnSubQty.setColorFilter(widgetColor);
        }
        qtyCount.setTextColor(widgetColor);
        animText();
    }

    private void animText() {
        qtyCount.setText(String.valueOf(qty));
    }


    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        if (this.qty != qty && qty >= 0) {
            this.qty = qty;
            setColors();
        }
    }

    public int getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(int maxQty) {
        if (maxQty != this.maxQty && maxQty > 0) {
            this.maxQty = maxQty;
            invalidate();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onClick(View view) {
        if (view == btnAddQty) {
            qty++;
            setColors();
            if(mListner !=null){
                mListner.onValueAdd(qty);
            }
        } else if (view == btnSubQty) {
            qty--;
            setColors();
            if(mListner !=null){
                mListner.onValueRemove(qty);
            }
        }

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        SavedState savedState = (SavedState) state;
        qty = savedState.qty;
        maxQty = savedState.maxQty;
        widgetColor = savedState.widgetColor;
        setColors();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.qty =qty;
        state.maxQty = maxQty;
        state.widgetColor = widgetColor;
        return state;
    }

    public static class SavedState extends BaseSavedState{
        private int maxQty;
        private int qty;
        private int widgetColor;

        protected SavedState(Parcel in) {
            super(in);
            maxQty = in.readInt();
            qty = in.readInt();
            widgetColor = in.readInt();
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(maxQty);
            parcel.writeInt(qty);
            parcel.writeInt(widgetColor);
        }
    }

    public interface ValueChangeListener {
        void onValueRemove(int newValue);
        void onValueAdd(int newValue);
    }
}
