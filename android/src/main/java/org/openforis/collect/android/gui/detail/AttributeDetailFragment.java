package org.openforis.collect.android.gui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.openforis.collect.R;
import org.openforis.collect.android.gui.input.AttributeInputComponent;
import org.openforis.collect.android.viewmodel.AttributeValidationError;
import org.openforis.collect.android.viewmodel.UiAttribute;
import org.openforis.collect.android.viewmodel.UiValidationError;

import java.util.HashSet;
import java.util.Set;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author Daniel Wiell
 */
public class AttributeDetailFragment extends NodeDetailFragment<UiAttribute> {
    private AttributeInputComponent inputComponent;

    public View createView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attribute_detail, container, false);
        ((TextView) rootView.findViewById(R.id.attribute_label)).setText(node().getLabel());
        TextView nodeLabel = (TextView) rootView.findViewById(R.id.attribute_label);
        nodeLabel.setText(node().getLabel());
        inputComponent = AttributeInputComponent.create(node(), getActivity());
        addInputComponentToView(rootView);
        return rootView;
    }

    protected View getDefaultFocusedView() {
        return inputComponent.getDefaultFocusedView();
    }

    public void onPause() {
        inputComponent.updateAttribute();
        super.onPause();
    }

    public void onDeselect() {
        super.onDeselect();
        if (inputComponent != null)
            inputComponent.updateAttribute();
    }

    public void onSelected() {
        super.onSelected();
        if (inputComponent != null)
            inputComponent.onSelected();
    }

    public void onAttributeChange(UiAttribute attribute) {
        if (inputComponent != null)
            inputComponent.onAttributeChange(attribute);
    }

    private void addInputComponentToView(View rootView) {
        ViewGroup attributeInputContainer = (ViewGroup) rootView.findViewById(R.id.attribute_input_container);
        View view = inputComponent.getView();
        view.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        attributeInputContainer.addView(view);
    }

    public void onValidationError(Set<UiValidationError> validationErrors) {
        UiAttribute attribute = node();
        Set<AttributeValidationError> attributeValidationErrors = new HashSet<AttributeValidationError>();
        for (UiValidationError validationError : validationErrors) {
            if (validationError instanceof AttributeValidationError) {
                AttributeValidationError attributeValidationError = (AttributeValidationError) validationError;
                if (attributeValidationError.getAttribute().equals(attribute))
                    attributeValidationErrors.add(attributeValidationError);
            }
        }
        if (!attributeValidationErrors.isEmpty())
            inputComponent.onValidationError(attributeValidationErrors);
    }
}
