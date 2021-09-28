package com.yiadom.dispatch.view.widget;

import android.content.Context;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * A {@linkplain ConstraintLayout} that is {@linkplain Checkable}
 */
public class CheckableConstraintLayout extends ConstraintLayout implements Checkable {
    private boolean _checked = false;

    public CheckableConstraintLayout(@NonNull Context context) {
        super(context);
    }

    @Override
    public boolean isChecked() {
        return _checked;
    }

    @Override
    public void setChecked(boolean checked) {
        _checked = checked;
        setActivated(checked);
    }

    @Override
    public void toggle() {
        _checked = !_checked;
        setActivated(_checked);
    }
}
