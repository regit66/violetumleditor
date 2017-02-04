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

package com.horstmann.violet.application.menu;

import com.horstmann.violet.application.ApplicationStopper;
import com.horstmann.violet.application.gui.MainFrame;
import com.horstmann.violet.framework.dialog.DialogFactory;
import com.horstmann.violet.framework.file.GraphFile;
import com.horstmann.violet.framework.file.IFile;
import com.horstmann.violet.framework.file.IGraphFile;
import com.horstmann.violet.framework.file.chooser.IFileChooserService;
import com.horstmann.violet.framework.file.export.FileExportService;
import com.horstmann.violet.framework.file.naming.ExtensionFilter;
import com.horstmann.violet.framework.file.naming.FileNamingService;
import com.horstmann.violet.framework.file.persistence.IFilePersistenceService;
import com.horstmann.violet.framework.file.persistence.IFileReader;
import com.horstmann.violet.framework.file.persistence.IFileWriter;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.BeanInjector;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.InjectedBean;
import com.horstmann.violet.framework.injection.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.injection.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.plugin.IDiagramPlugin;
import com.horstmann.violet.framework.plugin.PluginRegistry;
import com.horstmann.violet.framework.userpreferences.UserPreferencesService;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.workspace.IWorkspace;
import com.horstmann.violet.workspace.Workspace;
import com.thoughtworks.xstream.io.StreamException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

/**
 * Represents the file menu on the editor frame
 *
 * @author Alexandre de Pellegrin
 * @author Narin
 */
@ResourceBundleBean(resourceReference = MenuFactory.class)
public class FileMenu extends JMenu {

    /**
     * Default constructor for current class.
     *
     * @param mainFrame
     */
    @ResourceBundleBean(key = "file")
    public FileMenu(final MainFrame mainFrame) {
        ResourceBundleInjector.getInjector().inject(this);
        BeanInjector.getInjector().inject(this);
        this.mainFrame = mainFrame;
        createMenu();
        addWindowsClosingListener();
    }

    /**
     * This function returns a 'new file' menu.
     *
     * @return 'new file' menu
     */
    public JMenu getFileNewMenu() {
        return this.fileNewMenu;
    }

    /**
     * This function returns a recently opened file menu.
     *
     * @return recently opened file menu
     */
    public JMenu getFileRecentMenu() {
        return this.fileRecentMenu;
    }

    public IWorkspace getIWorkspace() {
        return mainFrame.getActiveWorkspace();
    }

    /**
     * This function initializes the menu.
     */
    private void createMenu() {
        initFileNewMenu();
        initFileOpenItem();
        initFileCloseItem();
        initFileRecentMenu();
        initFileSaveItem();
        initFileSaveAsItem();
        initFileExportMenu();
        initFilePrintItem();
        initFileExitItem();
        this.add(this.fileNewMenu);
        this.add(this.fileOpenItem);
        this.add(this.fileCloseItem);
        this.add(this.fileRecentMenu);
        this.add(this.fileSaveItem);
        this.add(this.fileSaveAsItem);
        this.add(this.fileExportMenu);
        this.add(this.filePrintItem);
        this.add(this.fileExitItem);
    }

    /**
     * Add frame listener to detect closing request
     */
    private void addWindowsClosingListener() {
        this.mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent event) {
                stopper.exitProgram(mainFrame);
            }
        });
    }

    /**
     * Init exit menu entry
     */
    private void initFileExitItem() {
        this.fileExitItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                stopper.exitProgram(mainFrame);
            }
        });
        if (this.fileChooserService == null) {
            this.fileExitItem.setEnabled(false);
        }
    }

    /**
     * Init export submenu
     */
    private void initFileExportMenu() {
        initFileExportToImageItem();
        initFileExportToClipboardItem();
        initFileExportToPdfItem();
        this.fileExportMenu.add(this.fileExportToImageItem);
        this.fileExportMenu.add(this.fileExportToClipBoardItem);
        this.fileExportMenu.add(this.fileExportToPdfItem);

        if (this.fileChooserService == null) {
            this.fileExportMenu.setEnabled(false);
        }
    }

    /**
     * Init export to clipboard menu entry
     */
    private void initFileExportToClipboardItem() {
        this.fileExportToClipBoardItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final IWorkspace workspace = getIWorkspace();

                if (workspace != null) {
                    workspace.getGraphFile().exportToClipboard();
                }
            }
        });
    }

    //- - - - - - INIT FILE EXPORT TO IMAGE ITEM - - - - - -

    /**
     * Init export to image menu entry
     */
    private void initFileExportToImageItem() {
        this.fileExportToImageItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                final IWorkspace workspace = getIWorkspace();

                if (workspace != null) {
                    tryExportImageFromWorkspace(workspace);
                }
            }
        });
    }

    /**
     * Try export image from the workspace.
     *
     * @param workspace
     */
    private void tryExportImageFromWorkspace(final IWorkspace workspace) {
        try {
            final ExtensionFilter[] exportFilters = fileNamingService.getImageExtensionFilters();
            final IFileWriter fileSaver = fileChooserService.chooseAndGetFileWriter(exportFilters);
            final OutputStream out = fileSaver.getOutputStream();

            if (out != null) {
                exportImageFromWorkspace(fileSaver, exportFilters, workspace, out);
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Export image from workspace.
     *
     * @param fileSaver
     * @param exportFilters
     * @param workspace
     * @param out
     * @throws IOException
     */
    private void exportImageFromWorkspace(final IFileWriter fileSaver, final ExtensionFilter[] exportFilters, final IWorkspace workspace, final OutputStream out) throws IOException {
        final String filename = fileSaver.getFileDefinition().getFilename();
        String extension = "";
        for (final ExtensionFilter exportFilter : exportFilters) {
            extension = exportFilter.getExtension();
            if (filename.toLowerCase().endsWith(extension.toLowerCase())) {
                workspace.getGraphFile().exportImage(out, extension.replace(".", ""));
                break;
            }
        }
    }

    /**
     *
     */
    private void initFileExportToPdfItem() {
        this.fileExportToPdfItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final IWorkspace workspace = getIWorkspace();
                if (workspace != null) {
                    try {
                        final ExtensionFilter extensionFilter = fileNamingService.getPdfExtensionFilter();
                        final IFileWriter fileSaver = fileChooserService.chooseAndGetFileWriter(extensionFilter);
                        final OutputStream out = fileSaver.getOutputStream();
                        if (out == null) {
                            throw new IOException("Unable to get output stream for extension " + extensionFilter.getExtension());
                        }
                        final String filename = fileSaver.getFileDefinition().getFilename();
                        workspace.getGraphFile().exportToPdf(out);
                    } catch (final IOException ioException) {
                        final String message = MessageFormat.format(fileExportErrorMessage, ioException.getMessage());
                        JOptionPane.showMessageDialog(null, message, fileExportError, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    /**
     * Init 'save as' menu entry
     */
    private void initFileSaveAsItem() {
        this.fileSaveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final IWorkspace workspace = getIWorkspace();
                if (workspace != null) {
                    final IGraphFile graphFile = workspace.getGraphFile();
                    graphFile.saveToNewLocation();
                    userPreferencesService.addRecentFile(graphFile);
                }
            }
        });
        if (this.fileChooserService == null) {
            this.fileSaveAsItem.setEnabled(false);
        }
    }

    /**
     * Init save menu entry
     */
    private void initFileSaveItem() {
        this.fileSaveItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final IWorkspace workspace = mainFrame.getActiveWorkspace();
                if (workspace != null) {
                    final IGraphFile graphFile = workspace.getGraphFile();
                    graphFile.save();
                    userPreferencesService.addRecentFile(graphFile);
                }
            }
        });
        if (this.fileChooserService == null || (this.fileChooserService != null && this.fileChooserService.isWebStart())) {
            this.fileSaveItem.setEnabled(false);
        }
    }

    /**
     * Init print menu entry
     */
    private void initFilePrintItem() {
        this.filePrintItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final IWorkspace workspace = getIWorkspace();

                if (workspace != null) {
                    workspace.getGraphFile().exportToPrinter();
                }
            }
        });
        if (this.fileChooserService == null) {
            this.filePrintItem.setEnabled(false);
        }
    }

    /**
     * Init close menu entry
     */
    private void initFileCloseItem() {
        this.fileCloseItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                IWorkspace workspace = null;
                try {
                    workspace = getIWorkspace();
                } catch (final RuntimeException runtimeException) {
                    // If no diagram is opened, close app
                    stopper.exitProgram(mainFrame);
                }

                if (workspace != null) {
                    fileCloseAction(workspace);
                }
            }
        });
    }

    /**
     * @param workspace
     */
    private void fileCloseAction(final IWorkspace workspace) {
        final IGraphFile graphFile = workspace.getGraphFile();
        final List<IWorkspace> workspaceList = mainFrame.getWorkspaceList();
        boolean willBeRemoved = true;
        graphFile.removeBackup();

        if (graphFile.isSaveRequired()) {
            final int result = configureJOptionPane();
            if (result == JOptionPane.YES_OPTION) {
                final String filename = graphFile.getFilename();
                if (filename == null) {
                    graphFile.saveToNewLocation();
                    userPreferencesService.addRecentFile(graphFile);
                    willBeRemoved = false;
                } else {
                    graphFile.save();
                    willBeRemoved = false;
                }
            }
        }
        if (workspaceList.size() == 0) {
            mainFrame.requestFocus();
            willBeRemoved = false;
        }
        if (willBeRemoved) {
            removeOpenedFile(workspace, graphFile);
        }
    }

    private void removeOpenedFile(final IWorkspace workspace, final IGraphFile graphFile) {
        mainFrame.removeWorkspace(workspace);
        userPreferencesService.removeOpenedFile(graphFile);
    }

    /**
     * @return
     */
    private int configureJOptionPane() {
        final JOptionPane optionPane = new JOptionPane();
        int result = JOptionPane.CANCEL_OPTION;
        optionPane.setMessage(dialogCloseMessage);
        optionPane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
        optionPane.setIcon(dialogCloseIcon);
        dialogFactory.showDialog(optionPane, dialogCloseTitle, true);

        if (!JOptionPane.UNINITIALIZED_VALUE.equals(optionPane.getValue())) {
            result = ((Integer) optionPane.getValue()).intValue();
        }
        return result;
    }

    /**
     * Init open menu entry
     */
    private void initFileOpenItem() {
        this.fileOpenItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                try {
                    final IFileReader fileOpener = fileChooserService.chooseAndGetFileReader(fileNamingService.getFileFilters());
                    if (fileOpener != null) {
                        final IGraphFile graphFile = new GraphFile(fileOpener.getFileDefinition());
                        mainFrame.addWorkspace(new Workspace(graphFile));
                        userPreferencesService.addOpenedFile(graphFile);
                        userPreferencesService.addRecentFile(graphFile);
                    }
                } catch (final StreamException streamexception) {
                    dialogFactory.showErrorDialog(dialogOpenFileIncompatibilityMessage + " : " + streamexception.getMessage());
                } catch (final IOException ioexception) {
                    dialogFactory.showErrorDialog(dialogOpenFileErrorMessage + " : " + ioexception.getMessage());
                }
            }
        });
        if (this.fileChooserService == null) {
            this.fileOpenItem.setEnabled(false);
        }
    }

    /**
     * Init new menu entry
     */
    public void initFileNewMenu() {
        final List<IDiagramPlugin> diagramPlugins = this.pluginRegistry.getDiagramPlugins();
        final SortedMap<String, SortedSet<IDiagramPlugin>> diagramPluginsSortedByCategory = sortByCategories(diagramPlugins);
        populateMenuEntry(diagramPluginsSortedByCategory);
    }

    /**
     * @param diagramPluginsSortedByCategory
     */
    private void populateMenuEntry(final SortedMap<String, SortedSet<IDiagramPlugin>> diagramPluginsSortedByCategory) {
        SortedSet<IDiagramPlugin> diagramPluginsByCategory = null;
        JMenu categorySubMenu = null;
        Dimension preferredSize = null;

        for (final String aCategory : diagramPluginsSortedByCategory.keySet()) {
            categorySubMenu = new JMenu(aCategory.replaceFirst("[0-9]*\\.", ""));
            preferredSize = categorySubMenu.getPreferredSize();
            preferredSize = new Dimension((int) preferredSize.getWidth() + 30, (int) preferredSize.getHeight());
            categorySubMenu.setPreferredSize(preferredSize);
            fileNewMenu.add(categorySubMenu);
            diagramPluginsByCategory = diagramPluginsSortedByCategory.get(aCategory);
            helpMePlease(diagramPluginsByCategory, categorySubMenu);
        }
    }

    /**
     * This function create File Menu (category submenu).
     *
     * @param diagramPluginsByCategory
     * @param categorySubMenu
     */
    private void helpMePlease(final SortedSet<IDiagramPlugin> diagramPluginsByCategory, final JMenu categorySubMenu) {
        String name = "";
        JMenuItem item = null;
        ImageIcon sampleDiagramImage = null;

        for (final IDiagramPlugin aDiagramPlugin : diagramPluginsByCategory) {
            name = aDiagramPlugin.getName().replaceFirst("[0-9]*\\.", "");
            item = new JMenuItem(name);
            sampleDiagramImage = getSampleDiagramImage(aDiagramPlugin);
            if (sampleDiagramImage != null) {
                item.setRolloverIcon(sampleDiagramImage);
            }
            final String finalName = name;
            item.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent event) {
                    final IWorkspace diagramPanel = new Workspace(new GraphFile(aDiagramPlugin.getGraphClass()));
                    diagramPanel.setTitle(unsavedPrefix + " " + finalName.toLowerCase());
                    mainFrame.addWorkspace(diagramPanel);
                }
            });
            categorySubMenu.add(item);
        }
    }

    /**
     * This function sorts diagram plugins by categories (and names wheen categories are equal).
     *
     * @param diagramPlugins List of IDiagramPlugins
     * @return sorted map (contains IDiagramPlugins)
     */
    private SortedMap<String, SortedSet<IDiagramPlugin>> sortByCategories(final List<IDiagramPlugin> diagramPlugins) {
        final SortedMap<String, SortedSet<IDiagramPlugin>> diagramPluginsSortedByCategory = new TreeMap<String, SortedSet<IDiagramPlugin>>();
        SortedSet<IDiagramPlugin> newSortedSet = null;
        String category = "";
        for (final IDiagramPlugin aDiagramPlugin : diagramPlugins) {
            category = aDiagramPlugin.getCategory();
            if (!diagramPluginsSortedByCategory.containsKey(category)) {
                newSortedSet = new TreeSet<IDiagramPlugin>(new Comparator<IDiagramPlugin>() {
                    @Override
                    public int compare(final IDiagramPlugin first, final IDiagramPlugin second) {
                        return first.getName().compareTo(second.getName());
                    }
                });
                diagramPluginsSortedByCategory.put(category, newSortedSet);
            }
            diagramPluginsSortedByCategory.get(category).add(aDiagramPlugin);
        }
        return diagramPluginsSortedByCategory;
    }


    /**
     * Init recent menu entry
     */
    public void initFileRecentMenu() {
        refreshFileRecentMenu();
        this.addFocusListener(new FocusListener() {
            public void focusGained(final FocusEvent e) {
                refreshFileRecentMenu();
            }

            public void focusLost(final FocusEvent e) {
            }
        });
        if (this.fileChooserService == null || (this.fileChooserService != null && this.fileChooserService.isWebStart())) {
            this.fileRecentMenu.setEnabled(false);
        }
    }

    /**
     * Updates file recent menu
     */
    private void refreshFileRecentMenu() {
        fileRecentMenu.removeAll();
        for (final IFile aFile : userPreferencesService.getRecentFiles()) {
            final JMenuItem item = new JMenuItem(aFile.getFilename());
            fileRecentMenu.add(item);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent event) {
                    try {
                        mainFrame.addWorkspace(new Workspace(new GraphFile(aFile)));
                        userPreferencesService.addOpenedFile(aFile);
                        userPreferencesService.addRecentFile(aFile);
                    } catch (final IOException ioException) {
                        dialogFactory.showErrorDialog(dialogOpenFileErrorMessage + " : " + ioException.getMessage());
                        userPreferencesService.removeOpenedFile(aFile);
                    }
                }
            });
        }
    }

    /**
     * This function returns an image exported from the sample diagram file attached to each plugin or null if no one exists.
     *
     * @param diagramPlugin
     * @return an image exported from the sample diagram file attached to each plugin or null if no one exists
     * @throws IOException
     */
    private ImageIcon getSampleDiagramImage(final IDiagramPlugin diagramPlugin) {
        ImageIcon result = null;
        try {
            final InputStream resourceAsStream = getClass().getResourceAsStream("/" + diagramPlugin.getSampleFilePath());
            if (resourceAsStream != null) {
                final IGraph graph = this.filePersistenceService.read(resourceAsStream);
                final JLabel label = new JLabel();
                Dimension size = null;
                BufferedImage image2 = null;
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
                label.setIcon(new ImageIcon(FileExportService.getImage(graph)));
                label.setSize(new Dimension(600, 550));
                label.setBackground(Color.WHITE);
                label.setOpaque(true);
                size = label.getSize();
                image2 = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
                label.paint(image2.createGraphics());
                result = new ImageIcon(image2);
            }
        } catch (final IOException ioException) {
            dialogFactory.showErrorDialog(dialogOpenFileErrorMessage + " : " + ioException.getMessage());
        }
        return result;
    }

    /**
     * The file chooser to use with with menu
     */
    @InjectedBean
    private IFileChooserService fileChooserService;

    /**
     * Application stopper
     */
    private final ApplicationStopper stopper = new ApplicationStopper();

    /**
     * Plugin registry
     */
    @InjectedBean
    private PluginRegistry pluginRegistry;

    /**
     * DialogBox handler
     */
    @InjectedBean
    private DialogFactory dialogFactory;

    /**
     * Access to user preferences
     */
    @InjectedBean
    private UserPreferencesService userPreferencesService;

    /**
     * File services
     */
    @InjectedBean
    private FileNamingService fileNamingService;

    /**
     * Service to convert IGraph to XML content (and XML to IGraph of course)
     */
    @InjectedBean
    private IFilePersistenceService filePersistenceService;

    /**
     * Application main frame
     */
    private final MainFrame mainFrame;

    @ResourceBundleBean(key = "file.new")
    private JMenu fileNewMenu;

    @ResourceBundleBean(key = "file.open")
    private JMenuItem fileOpenItem;

    @ResourceBundleBean(key = "file.recent")
    private JMenu fileRecentMenu;

    @ResourceBundleBean(key = "file.close")
    private JMenuItem fileCloseItem;

    @ResourceBundleBean(key = "file.save")
    private JMenuItem fileSaveItem;

    @ResourceBundleBean(key = "file.save_as")
    private JMenuItem fileSaveAsItem;

    @ResourceBundleBean(key = "file.export_to_pdf")
    private JMenuItem fileExportToPdfItem;

    @ResourceBundleBean(key = "file.export_to_image")
    private JMenuItem fileExportToImageItem;

    @ResourceBundleBean(key = "file.export_to_clipboard")
    private JMenuItem fileExportToClipBoardItem;

    @ResourceBundleBean(key = "file.export_to_java")
    private JMenuItem fileExportToJavaItem;

    @ResourceBundleBean(key = "file.export_to_python")
    private JMenuItem fileExportToPythonItem;

    @ResourceBundleBean(key = "file.export")
    private JMenu fileExportMenu;

    @ResourceBundleBean(key = "file.print")
    private JMenuItem filePrintItem;

    @ResourceBundleBean(key = "file.exit")
    private JMenuItem fileExitItem;

    @ResourceBundleBean(key = "dialog.close.title")
    private String dialogCloseTitle;

    @ResourceBundleBean(key = "dialog.close.ok")
    private String dialogCloseMessage;

    @ResourceBundleBean(key = "dialog.close.icon")
    private ImageIcon dialogCloseIcon;

    @ResourceBundleBean(key = "dialog.open_file_failed.text")
    private String dialogOpenFileErrorMessage;

    @ResourceBundleBean(key = "dialog.open_file_content_incompatibility.text")
    private String dialogOpenFileIncompatibilityMessage;

    @ResourceBundleBean(key = "workspace.unsaved_prefix")
    private String unsavedPrefix;

    @ResourceBundleBean(key = "file.export.error")
    private String fileExportError;

    @ResourceBundleBean(key = "file.export.error.message")
    private String fileExportErrorMessage;

}
