package com.horstmann.violet.workspace.sidebar.historypanel;

import com.horstmann.violet.framework.injection.resources.ResourceBundleInjector;
import com.horstmann.violet.workspace.sidebar.colortools.IColorChoiceBar;
import com.horstmann.violet.workspace.sidebar.editortools.EditorToolsPanelUI;

import java.awt.*;
import java.awt.event.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.horstmann.violet.framework.injection.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.injection.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.workspace.IWorkspace;
import com.horstmann.violet.workspace.editorpart.IEditorPart;
import com.horstmann.violet.workspace.editorpart.IEditorPartBehaviorManager;
import com.horstmann.violet.workspace.editorpart.behavior.CutCopyPasteBehavior;
import com.horstmann.violet.workspace.editorpart.behavior.UndoRedoCompoundBehavior;
import com.horstmann.violet.workspace.sidebar.ISideBarElement;
import com.horstmann.violet.workspace.sidebar.SideBar;

/**
 * Created by Marek on 06.12.2016.
 */
@ResourceBundleBean(resourceReference = SideBar.class)
public class HistoryPanel extends JPanel implements ISideBarElement {

    public HistoryPanel() {

        ResourceBundleInjector.getInjector().inject(this);
        this.ui= new HistoryPanelUI(this);
        add(scrollPane);
        list.setSize(100,50);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setSize(150,100);
        add(scrollPane);
        list.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {

                if (mouseEvent.getClickCount() == 2) {
                    int selectedIndex = list.getSelectedIndex();
                    UndoRedoCompoundBehavior undoRedoBehavior = getUndoRedoBehavior();
                    for (int i=0; i<list.getModel().getSize();i++){
                        if (i >= selectedIndex){
                            if (undoRedoBehavior != null) {
                                undoRedoBehavior.undo();
                            }
                        }
                    }
                    int modelListSize=list.getModel().getSize();
                    for (int i=0; i<modelListSize;i++){
                        if (i >= selectedIndex){
                            model.remove(i);
                            i--;
                            modelListSize--;
                        }
                    }
                }
            }
        });
    }
    private UndoRedoCompoundBehavior getUndoRedoBehavior() {
        IEditorPart activeEditorPart = workspace.getEditorPart();
        IEditorPartBehaviorManager behaviorManager = activeEditorPart.getBehaviorManager();
        List<UndoRedoCompoundBehavior> found = behaviorManager.getBehaviors(UndoRedoCompoundBehavior.class);
        if (found.size() != 1) {
            return null;
        }
        return found.get(0);
    }
    public static void addEvent(String event){
        model.addElement(event);
        redoModel.addElement(event);
    }
    public static void removeEvent(){
        if( model.getSize()>0)
            model.remove(model.getSize()-1);
    }
    public static void redoEvent(){
        addEvent(redoModel.get(redoModel.getSize()-1).toString());
    }
    public void install(IWorkspace workspace)
    {
        this.workspace = workspace;
    }
    public Component getAWTComponent()
    {
        return this;
    }

    public JList getList() { return this.list; }

    public String getTitle()
    {
        return this.title;
    }

    private IWorkspace workspace;

    static DefaultListModel model = new DefaultListModel();
    static DefaultListModel redoModel = new DefaultListModel();

    private JList<String> list = new JList( model );

    JScrollPane scrollPane = new JScrollPane(list);

    @ResourceBundleBean(key = "title.historypanel.text")
    private String title;

    @ResourceBundleBean(key = "paste")
    private JButton bPaste;

}