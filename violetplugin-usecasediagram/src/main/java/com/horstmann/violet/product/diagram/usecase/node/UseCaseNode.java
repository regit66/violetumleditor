/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.product.diagram.usecase.node;

import com.horstmann.violet.framework.dialog.IRevertableProperties;
import com.horstmann.violet.framework.graphics.content.ContentBackground;
import com.horstmann.violet.framework.graphics.content.ContentBorder;
import com.horstmann.violet.framework.graphics.content.ContentInsideShape;
import com.horstmann.violet.framework.graphics.content.TextContent;
import com.horstmann.violet.framework.graphics.shape.ContentInsideEllipse;
import com.horstmann.violet.framework.util.MementoCaretaker;
import com.horstmann.violet.framework.util.OneStringMemento;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.common.node.ColorableNode;
import com.horstmann.violet.product.diagram.property.text.LineText;
import com.horstmann.violet.product.diagram.property.text.SingleLineText;
import com.horstmann.violet.product.diagram.usecase.UseCaseDiagramConstant;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A use case node_old in a use case diagram.
 */
public class UseCaseNode extends ColorableNode implements IRevertableProperties
{
    /**
     * Construct a use case node_old with a default size
     */
    public UseCaseNode()
    {
        super();

        name = new SingleLineText();
        name.setAlignment(LineText.CENTER);
        createContentStructure();
    }

    protected UseCaseNode(UseCaseNode node) throws CloneNotSupportedException
    {
        super(node);
        name = node.name.clone();
        createContentStructure();
    }

    @Override
    protected void beforeReconstruction()
    {
        super.beforeReconstruction();

        name.reconstruction();
        name.setAlignment(LineText.CENTER);
    }

    @Override
    protected INode copy() throws CloneNotSupportedException
    {
        return new UseCaseNode(this);
    }

    @Override
    protected void createContentStructure()
    {
        TextContent nameContent = new TextContent(name);
        nameContent.setMinHeight(DEFAULT_HEIGHT);
        nameContent.setMinWidth(DEFAULT_WIDTH);

        ContentInsideShape contentInsideShape = new ContentInsideEllipse(nameContent);

        setBorder(new ContentBorder(contentInsideShape, getBorderColor()));
        setBackground(new ContentBackground(getBorder(), getBackgroundColor()));
        setContent(getBackground());

        setTextColor(super.getTextColor());
    }

    @Override
    public void setTextColor(Color textColor)
    {
        name.setTextColor(textColor);
        super.setTextColor(textColor);
    }

    @Override
    public String getToolTip()
    {
        return UseCaseDiagramConstant.USE_CASE_DIAGRAM_RESOURCE.getString("tooltip.use_case_node");
    }

    @Override
    public Point2D getConnectionPoint(IEdge edge)
    {
        // if use case node_old is atatched to an actor node_old, we force connection point to cardianl points
        if (edge.getStartNode().getClass().isAssignableFrom(ActorNode.class) || edge.getEndNode().getClass().isAssignableFrom(ActorNode.class))
        {

        }
        return super.getConnectionPoint(edge);
    }


    /**
     * Sets the name property value.
     * 
     * @param newValue the new use case name
     */
    public void setName(SingleLineText newValue)
    {
        name.setText(newValue.toEdit());
    }

    /**
     * Gets the name property value.
     *
     */
    public LineText getName()
    {
        return name;
    }

    private final MementoCaretaker<OneStringMemento> caretaker = new MementoCaretaker<OneStringMemento>();

    @Override
    public void beforeUpdate()
    {
        caretaker.save(new OneStringMemento(name.toString()));
    }

    @Override
    public void revertUpdate()
    {
        name.setText(caretaker.load().getValue());
    }    
        
    @Override
    public LineText getAttributes() {
        return null;
    }

    @Override
    public LineText getMethods() {
        return null;
    }


    private SingleLineText name;

    private static int DEFAULT_WIDTH = 60;
    private static int DEFAULT_HEIGHT = 20;
}
