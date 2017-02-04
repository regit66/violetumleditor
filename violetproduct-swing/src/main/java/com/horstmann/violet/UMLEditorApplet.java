/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

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

package com.horstmann.violet;

import com.horstmann.violet.application.gui.MainFrame;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.BeanInjector;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.InjectedBean;
import com.horstmann.violet.framework.plugin.PluginLoader;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A program for editing UML diagrams.
 */
public class UMLEditorApplet extends JApplet {

    /*
     * Applet entry point (non-Javadoc)
     * 
     * @see java.applet.Applet#init()
     */
    public void init() {
        initBeanFactory();
        BeanInjector.getInjector().inject(this);
        createAppletWorkspace();
    }

    private void initBeanFactory() {
        new UMLEditor().init();
    }

    /**
     * Creates workspace when application works as an applet. It contains :<br>
     * + plugins loading + GUI theme management + launging argments to open diagram<br>
     */
    private void createAppletWorkspace() {
        installPlugins();
        final MainFrame mainFrame = new MainFrame();
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosed(final WindowEvent e) {
                System.out.println("editor closed");
            }
        });
        setContentPane(mainFrame.getContentPane());
        setJMenuBar(mainFrame.getJMenuBar());
    }

    /**
     * Install plugins
     */
    private void installPlugins() {
        this.pluginLoader.installPlugins();
    }

    @InjectedBean
    private PluginLoader pluginLoader;

}