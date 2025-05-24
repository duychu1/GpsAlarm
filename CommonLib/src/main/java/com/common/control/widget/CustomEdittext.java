package com.common.control.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class CustomEdittext extends AppCompatEditText {
    public CustomEdittext(@NonNull Context context) {
        super(context);
    }

    public CustomEdittext(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEdittext(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private OnHideKeyboardListener mOnHideKeyboardListener;

    public void setOnHideKeyboardListener(OnHideKeyboardListener mOnHideKeyboardListener) {
        this.mOnHideKeyboardListener = mOnHideKeyboardListener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnHideKeyboardListener != null) {
                mOnHideKeyboardListener.onHideKeyboard();
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public interface OnHideKeyboardListener {
        void onHideKeyboard();
    }
}
