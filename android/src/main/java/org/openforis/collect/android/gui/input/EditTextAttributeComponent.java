package org.openforis.collect.android.gui.input;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;
import org.openforis.collect.android.SurveyService;
import org.openforis.collect.android.viewmodel.UiAttribute;
import org.openforis.collect.android.viewmodel.UiValidationError;

import java.util.Set;

/**
 * @author Daniel Wiell
 */
public abstract class EditTextAttributeComponent<T extends UiAttribute> extends AttributeComponent<T> {
    private final EditText editText;

    protected EditTextAttributeComponent(T attribute, SurveyService surveyService, Context context) {
        super(attribute, surveyService, context);
        editText = createEditText();
    }

    protected abstract boolean hasChanged(String newValue);

    protected abstract String attributeValue();

    protected abstract void updateAttributeValue(String newValue);

    protected abstract void onEditTextCreated(EditText input);

    protected TextView errorMessageContainerView() {
        return editText;
    }

    protected final boolean updateAttributeIfChanged() {
        String newValue = getEditTextString();
        if (StringUtils.isEmpty(newValue)) newValue = null;
        if (hasChanged(newValue)) {
            updateAttributeValue(newValue);
            return true;
        }
        return false;
    }

    protected View toInputView() {
        return editText;
    }

    public final View getDefaultFocusedView() {
        return editText;
    }

    private String getEditTextString() {
        return editText.getText() == null ? null : editText.getText().toString();
    }

    private EditText createEditText() {
        EditText editText = new EditText(context);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    saveNode();
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    saveNode();
                return false;
            }
        });
        editText.setText(attributeValue());
        editText.setSingleLine();
        onEditTextCreated(editText);
        return editText;
    }
}