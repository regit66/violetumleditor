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

/**
 * A program for editing UML diagrams.
 */
public class UMLEditorWebStart {

    /**
     * Standalone application entry point
     *
     * @param args (could contains file to open)
     */
    public static void main(final String[] args) {
        new UMLEditorWebStart();
    }

    /**
     * Default constructor
     */
    private UMLEditorWebStart() {
        initBeanFactory();
        BeanInjector.getInjector().inject(this);
        createWebstartWorkspace();
    }

    private void initBeanFactory() {
        new UMLEditor().init();
    }

    /**
     * Creates workspace when application works from java web start. It contains :<br> + plugins loading + GUI theme management
     */
    private void createWebstartWorkspace() {
        installPlugins();
        final MainFrame mainFrame = new MainFrame();
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setVisible(true);
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