package org.openforis.collect.android.attributeconverter;

import org.openforis.collect.android.viewmodel.*;
import org.openforis.collect.android.viewmodelmanager.NodeDto;
import org.openforis.idm.metamodel.*;
import org.openforis.idm.model.Attribute;
import org.openforis.idm.model.Value;

/**
 * @author Daniel Wiell
 */
@SuppressWarnings("unchecked")
public abstract class AttributeConverter<T extends Attribute, U extends UiAttribute> {

    protected abstract U uiAttribute(Definition definition, T attribute);

    protected abstract U uiAttribute(NodeDto nodeDto, Definition definition);

    protected abstract NodeDto dto(U uiAttribute);

    protected abstract Value value(U uiAttribute);

    protected abstract T attribute(U uiAttribute, NodeDefinition definition);

    protected NodeDto createDto(UiAttribute uiAttribute) {
        NodeDto dto = new NodeDto();
        dto.id = uiAttribute.getId();
        dto.definitionId = uiAttribute.getDefinition().id;
        dto.parentId = uiAttribute.getParent() == null ? null : uiAttribute.getParent().getId();
        dto.surveyId = uiAttribute.getUiSurvey().getId();
        dto.recordId = uiAttribute.getUiRecord().getId();
        dto.type = NodeDto.Type.ofUiNode(uiAttribute);
        dto.recordKeyAttribute = uiAttribute.getUiRecord().isKeyAttribute(uiAttribute);
        return dto;
    }

    public static <D extends Definition> UiAttribute toUiAttribute(D definition, Attribute attribute) {
        return getConverter(attribute).uiAttribute(definition, attribute);
    }

    public static UiNode toUiAttribute(NodeDto nodeDto, Definition definition) {
        return getConverter(nodeDto.type.uiNodeClass).uiAttribute(nodeDto, definition);
    }

    public static NodeDto toDto(UiAttribute attribute) {
        NodeDto dto = getConverter(attribute.getClass()).dto(attribute);
        dto.status = attribute.getStatus().name();
        return dto;
    }

    public static Value toValue(UiAttribute uiAttribute) {
        return getConverter(uiAttribute.getClass()).value(uiAttribute);
    }

    public static Attribute toAttribute(UiAttribute uiAttribute, NodeDefinition definition) {
        Attribute attribute = getConverter(definition).attribute(uiAttribute, definition);
        attribute.setId(uiAttribute.getId());
        return attribute;
    }

    private static AttributeConverter getConverter(Attribute attribute) {
        return getConverter(attribute.getDefinition());
    }

    private static AttributeConverter getConverter(NodeDefinition definition) {
        if (definition instanceof TextAttributeDefinition)
            return new TextConverter();
        if (definition instanceof DateAttributeDefinition)
            return new DateConverter();
        if (definition instanceof TimeAttributeDefinition)
            return new TimeConverter();
        if (definition instanceof CodeAttributeDefinition)
            return new CodeConverter();
        if (definition instanceof CoordinateAttributeDefinition)
            return new CoordinateConverter();
        if (definition instanceof FileAttributeDefinition)
            return new FileConverter();
        if (definition instanceof TaxonAttributeDefinition)
            return new TaxonConverter();
        if (definition instanceof BooleanAttributeDefinition)
            return new BooleanConverter();
        if (definition instanceof NumberAttributeDefinition && ((NumberAttributeDefinition) definition).isInteger())
            return new IntegerConverter();
        if (definition instanceof NumberAttributeDefinition && ((NumberAttributeDefinition) definition).isReal())
            return new DoubleConverter();
        if (definition instanceof RangeAttributeDefinition && ((RangeAttributeDefinition) definition).isInteger())
            return new IntegerRangeConverter();
        if (definition instanceof RangeAttributeDefinition && ((RangeAttributeDefinition) definition).isReal())
            return new DoubleRangeConverter();
        throw new IllegalStateException("Unexpected attribute type: " + definition);
    }

    private static AttributeConverter getConverter(Class type) {
        if (UiTextAttribute.class.isAssignableFrom(type))
            return new TextConverter();
        if (UiDateAttribute.class.isAssignableFrom(type))
            return new DateConverter();
        if (UiTimeAttribute.class.isAssignableFrom(type))
            return new TimeConverter();
        if (UiCodeAttribute.class.isAssignableFrom(type))
            return new CodeConverter();
        if (UiCoordinateAttribute.class.isAssignableFrom(type))
            return new CoordinateConverter();
        if (UiFileAttribute.class.isAssignableFrom(type))
            return new FileConverter();
        if (UiTaxonAttribute.class.isAssignableFrom(type))
            return new TaxonConverter();
        if (UiBooleanAttribute.class.isAssignableFrom(type))
            return new BooleanConverter();
        if (UiIntegerAttribute.class.isAssignableFrom(type))
            return new IntegerConverter();
        if (UiDoubleAttribute.class.isAssignableFrom(type))
            return new DoubleConverter();
        if (UiIntegerRangeAttribute.class.isAssignableFrom(type))
            return new IntegerRangeConverter();
        if (UiDoubleRangeAttribute.class.isAssignableFrom(type))
            return new DoubleRangeConverter();
        throw new IllegalStateException("Unexpected UiAttribute type: " + type);
    }
}
