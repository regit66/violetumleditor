package com.horstmann.violet.product.diagram.classes.node;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.horstmann.violet.framework.graphics.Separator;
import com.horstmann.violet.framework.graphics.content.*;
import com.horstmann.violet.framework.graphics.content.VerticalLayout;
import com.horstmann.violet.framework.graphics.shape.ContentInsideRectangle;
import com.horstmann.violet.product.diagram.abstracts.node.IRenameableNode;
import com.horstmann.violet.product.diagram.classes.ClassDiagramConstant;
import com.horstmann.violet.product.diagram.common.node.ColorableNodeWithMethodsInfo;
import com.horstmann.violet.product.diagram.property.text.decorator.*;
import com.horstmann.violet.product.diagram.common.node.ColorableNode;
import com.horstmann.violet.product.diagram.property.text.LineText;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.property.text.MultiLineText;
import com.horstmann.violet.product.diagram.property.text.SingleLineText;

/**
 * A class node in a class diagram.
 */
public class ClassNode extends ColorableNodeWithMethodsInfo implements IRenameableNode
{
	/**
     * Construct a class node with a default size
     */
    public ClassNode()
    {
        super();
        name = new SingleLineText(NAME_CONVERTER);
        name.setAlignment(LineText.CENTER);
        attributes = new MultiLineText(SIGNATURE_CONVERTER);
        createContentStructure();
    }

    protected ClassNode(ClassNode node) throws CloneNotSupportedException
    {
        super(node);
        name = node.name.clone();
        attributes = node.attributes.clone();
        methods = node.methods.clone();
        createContentStructure();
    }

    @Override
    protected void beforeReconstruction()
    {
        super.beforeReconstruction();

        if(null == name)
        {
            name = new SingleLineText();
        }
        if(null == attributes)
        {
            attributes = new MultiLineText();
        }
        if(null == methods)
        {
            methods = new MultiLineText();
        }
        name.reconstruction(NAME_CONVERTER);
        attributes.reconstruction(SIGNATURE_CONVERTER);
        methods.reconstruction(SIGNATURE_CONVERTER);
        name.setAlignment(LineText.CENTER);
    }

    @Override
    protected INode copy() throws CloneNotSupportedException
    {
        return new ClassNode(this);
    }

    @Override
    protected void createContentStructure()
    {
        TextContent nameContent = new TextContent(name);
        nameContent.setMinHeight(MIN_NAME_HEIGHT);
        nameContent.setMinWidth(MIN_WIDTH);
        TextContent attributesContent = new TextContent(attributes);
        TextContent methodsContent = new TextContent(methods);

        VerticalLayout verticalGroupContent = new VerticalLayout();
        verticalGroupContent.add(nameContent);
        verticalGroupContent.add(attributesContent);
        verticalGroupContent.add(methodsContent);
        separator = new Separator.LineSeparator(getBorderColor());
        verticalGroupContent.setSeparator(separator);

        ContentInsideShape contentInsideShape = new ContentInsideRectangle(verticalGroupContent);

        setBorder(new ContentBorder(contentInsideShape, getBorderColor()));
        setBackground(new ContentBackground(getBorder(), getBackgroundColor()));
        setContent(getBackground());

        setTextColor(super.getTextColor());
    }

    @Override
    public void setBorderColor(Color borderColor)
    {
        if(null != separator)
        {
            separator.setColor(borderColor);
        }
        super.setBorderColor(borderColor);
    }

    @Override
    public void setTextColor(Color textColor)
    {
        name.setTextColor(textColor);
        attributes.setTextColor(textColor);
        methods.setTextColor(textColor);
        super.setTextColor(textColor);
    }

    @Override
    public String getToolTip()
    {
        return ClassDiagramConstant.CLASS_DIAGRAM_RESOURCE.getString("tooltip.class_node");
    }

    @Override
    public void replaceNodeOccurrences(String oldValue, String newValue) {
        super.replaceNodeOccurrences(oldValue, newValue);
        replaceNodeOccurrencesInAttributes(oldValue, newValue);
    }

    /**
     * Sets the attributes property value.
     *
     * @param newValue the attributes of this class
     */
    public void setAttributes(LineText newValue)
    {
        attributes.setText(newValue);
    }

    /**
     * Gets the attributes property value.
     *
     * @return the attributes of this class
     */
    public LineText getAttributes()
    {
        return attributes;
    }

    /**
     * Replaces class name occurrences in attributes
     * @param oldValue old class name
     * @param newValue new class name
     */
    private void replaceNodeOccurrencesInAttributes(String oldValue, String newValue)
    {
        if (!getAttributes().toString().isEmpty()) {
            MultiLineText renamedAttributes = new MultiLineText();
            renamedAttributes.setText(renameAttributes(oldValue, newValue));
            setAttributes(renamedAttributes);
        }
    }

    /**
     * Finds all of oldValue class occurrences in attributes and replaces it with newValue
     * @param oldValue old class name
     * @param newValue new class name
     * @return attributes with renamed classes
     */
    private String renameAttributes(String oldValue, String newValue) {
        ArrayList<String> attributes = new ArrayList<String>(Arrays.asList(getAttributes().toEdit().split("\n")));
        StringBuilder renamedAttributes = new StringBuilder();
        Pattern pattern = Pattern.compile(".*:\\s*(" + oldValue + ")\\s*$");

        String separator = "";
        for(String attribute : attributes) {
            StringBuffer attributeToRename = new StringBuffer(attribute);
            Matcher matcher = pattern.matcher(attribute);
            renamedAttributes.append(
                    separator + (matcher.matches()
                            ? attributeToRename.replace(matcher.start(1), matcher.end(1), newValue)
                            : attribute)
            );
            separator = "\n";
        }

        return renamedAttributes.toString();
    }

    private MultiLineText attributes;

    private transient Separator separator;

    private static final int MIN_NAME_HEIGHT = 45;
    private static final int MIN_WIDTH = 100;
    private static final String ABSTRACT = "«abstract»";

    private static final List<String> STEREOTYPES = Arrays.asList(
            "«Utility»",
            "«Type»",
            "«Metaclass»",
            "«ImplementationClass»",
            "«Focus»",
            "«Entity»",
            "«Control»",
            "«Boundary»",
            "«Auxiliary»",
            ABSTRACT
    );

    private static final LineText.Converter NAME_CONVERTER = new LineText.Converter()
    {
        @Override
        public OneLineText toLineString(String text)
        {
            OneLineText controlText = new OneLineText(text);
            OneLineText lineString = new LargeSizeDecorator(controlText);

            if(controlText.contains(ABSTRACT))
            {
                lineString = new ItalicsDecorator(lineString);
            }

            for(String stereotype : STEREOTYPES)
            {
                if(controlText.contains(stereotype))
                {
                    lineString = new PrefixDecorator(new RemoveSentenceDecorator(
                            lineString, stereotype), String.format("<center>%s</center>", stereotype)
                    );
                }
            }

            return lineString;
        }
    };
}
