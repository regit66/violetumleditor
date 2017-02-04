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
import com.horstmann.violet.application.gui.SplashScreen;
import com.horstmann.violet.framework.file.GraphFile;
import com.horstmann.violet.framework.file.IFile;
import com.horstmann.violet.framework.file.LocalFile;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.BeanFactory;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.BeanInjector;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.InjectedBean;
import com.horstmann.violet.framework.plugin.PluginLoader;
import com.horstmann.violet.framework.userpreferences.UserPreferencesService;
import com.horstmann.violet.framework.util.VersionChecker;
import com.horstmann.violet.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A program for editing UML diagrams.
 */
public class UMLEditorApplication {

    /**
     * Standalone application entry point
     *
     * @param args (could contains file to open)
     */
    public static void main(final String[] args) {
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if ("-reset".equals(arg)) {
                initBeanFactory();
                final UserPreferencesService service = BeanFactory.getFactory().getBean(UserPreferencesService.class);
                service.reset();
                System.out.println("User preferences reset done.");
            }
            if ("-english".equals(arg)) {
                Locale.setDefault(Locale.ENGLISH);
                System.out.println("Language forced to english.");
            }
            if ("-help".equals(arg) || "-?".equals(arg)) {
                System.out.println("Violet UML Editor command line help. Options are :");
                System.out.println("-reset to reset user preferences,");
                System.out.println("-english to force language to english.");
                return;
            }
        }
        new UMLEditorApplication(args);
    }

    /**
     * Default constructor
     *
     * @param filesToOpen
     */
    private UMLEditorApplication(final String[] filesToOpen) {
        initBeanFactory();
        BeanInjector.getInjector().inject(this);
        createDefaultWorkspace(filesToOpen);
    }

    private static void initBeanFactory() {
        new UMLEditor().init();
    }

    /**
     * Creates workspace when application works as a standalone one. It contains :<br>
     * + plugins loading + GUI theme management + a spash screen<br>
     * + jvm checking<br>
     * + command line args<br>
     * + last workspace restore<br>
     */
    private void createDefaultWorkspace(final String[] filesToOpen) {
        final MainFrame mainFrame = new MainFrame();
        final SplashScreen splashScreen = new SplashScreen();
        this.pluginLoader.installPlugins();
        this.versionChecker.checkJavaVersion();

        splashScreen.setVisible(true);
        setMainframe(mainFrame);
        SplashScreen.displayOverEditor(mainFrame, 1000);
        createLastSessionFilesMenu(filesToOpen, mainFrame, this.userPreferencesService.getOpenedFilesDuringLastSession());
        activeMainframe(mainFrame, this.userPreferencesService.getActiveDiagramFile());
        hiddenSplashScreen(splashScreen);
    }

    /**
     * Sets mainframe: its size and an extended state.
     *
     * @param mainFrame
     */
    private void setMainframe(final MainFrame mainFrame) {
        mainFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Sets the splash screen as invisible.
     *
     * @param splashScreen
     */
    private void hiddenSplashScreen(final SplashScreen splashScreen) {
        splashScreen.setVisible(false);
        splashScreen.dispose();
    }

    /**
     * Actives the main frame.
     *
     * @param mainFrame
     * @param activeFile
     */
    private void activeMainframe(final MainFrame mainFrame, final IFile activeFile) {
        mainFrame.setActiveWorkspace(activeFile);
        mainFrame.setVisible(true);
    }


    /**
     * Creates a list with files from the last session.
     *
     * @param filesToOpen
     * @param mainFrame
     * @param lastSessionFiles
     */
    private void createLastSessionFilesMenu(final String[] filesToOpen, final MainFrame mainFrame, final List<IFile> lastSessionFiles) {
        addFilesToList(filesToOpen, lastSessionFiles);
        addLastSessionFilesGraphical(mainFrame, lastSessionFiles);
    }

    /**
     * Adds files to open.
     *
     * @param filesToOpen
     * @param fullList
     */
    private void addFilesToList(final String[] filesToOpen, final List<IFile> fullList) {
        for (final String fileToOpen : filesToOpen) {
            try {
                fullList.add(new LocalFile(new File(fileToOpen)));
            } catch (final IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    /**
     * @param mainFrame
     * @param lastSessionFiles
     */
    private void addLastSessionFilesGraphical(final MainFrame mainFrame, final List<IFile> lastSessionFiles) {
        for (final IFile file : lastSessionFiles) {
            try {
                mainFrame.addWorkspace(new Workspace(new GraphFile(file)));
            } catch (final IOException ioexception) {
                userPreferencesService.removeOpenedFile(file);
            }
        }
    }


    @InjectedBean
    private VersionChecker versionChecker;

    @InjectedBean
    private PluginLoader pluginLoader;

    @InjectedBean
    private UserPreferencesService userPreferencesService;

}