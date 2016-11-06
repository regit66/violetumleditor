package com.horstmann.violet.product.diagram.common.edge;

import java.beans.PropertyDescriptor;
import java.util.List;

/**
 * TODO javadoc
 * This ...
 *
 * @author Adrian Bobrowski <adrian071993@gmail.com>
 * @date 22.02.2016
 */
class ArrowheadEdgeBeanInfo extends LineEdgeBeanInfo
{

    public ArrowheadEdgeBeanInfo()
    {
        super(ArrowheadEdge.class);
    }

    ArrowheadEdgeBeanInfo(final Class<?> beanClass)
    {
        super(beanClass);
    }

    @Override
    protected List<PropertyDescriptor> createPropertyDescriptorList()
    {
        final List<PropertyDescriptor> propertyDescriptorList = super.createPropertyDescriptorList();

        if (displayStartArrowhead)
        {
            propertyDescriptorList
                    .add(createPropertyDescriptor(START_ARROWHEAD_VAR_NAME, START_ARROWHEAD_LABEL_KEY, START_ARROWHEAD_PRIORITY));
        }
        if (displayEndArrowhead)
        {
            propertyDescriptorList
                    .add(createPropertyDescriptor(END_ARROWHEAD_VAR_NAME, END_ARROWHEAD_LABEL_KEY, END_ARROWHEAD_PRIORITY));
        }
        return propertyDescriptorList;
    }

    protected boolean displayStartArrowhead = true;
    protected boolean displayEndArrowhead = true;

    private static final String START_ARROWHEAD_VAR_NAME = "startArrowheadChoiceList";
    private static final String END_ARROWHEAD_VAR_NAME = "endArrowheadChoiceList";
    private static final String START_ARROWHEAD_LABEL_KEY = "arrowhead.start";
    private static final String END_ARROWHEAD_LABEL_KEY = "arrowhead.end";
    private static final int START_ARROWHEAD_PRIORITY = 0;
    private static final int END_ARROWHEAD_PRIORITY = 10;
}
