package com.horstmann.violet.application.autosave;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Timer;

import com.horstmann.violet.application.gui.MainFrame;
import com.horstmann.violet.framework.file.GraphFile;
import com.horstmann.violet.framework.file.IFile;
import com.horstmann.violet.framework.file.LocalFile;
import com.horstmann.violet.framework.file.persistence.IFilePersistenceService;
import com.horstmann.violet.framework.file.persistence.IFileReader;
import com.horstmann.violet.framework.file.persistence.JFileReader;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.BeanInjector;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.InjectedBean;
import com.horstmann.violet.framework.file.IGraphFile;
import com.horstmann.violet.workspace.IWorkspace;
import com.horstmann.violet.workspace.Workspace;

/**
 * Violet's auto save
 * 
 * @author Pawel Majka
 * @author Marta Mrugalska
 */
public class AutoSave implements ActionListener {

	private MainFrame mainFrame;
	private Timer saveTimer;
	private int saveInterval;
	private boolean autoSaveEnabled;
	private String autoSaveDirectory;

	@InjectedBean
	private IFilePersistenceService filePersistenceService;
	
	/**
	* Constructor Autosave
	*  @param mainFrame where is attached this menu
	*/
;	public AutoSave(MainFrame mainFrame)
	{
		BeanInjector.getInjector().inject(this);
		AutosaveSettings settings = new AutosaveSettings();
		
		if (settings.isEnableAutosave()) {
			saveInterval = settings.getAutosaveInterval();
			autoSaveDirectory = settings.getAutosavePath();
			autoSaveEnabled = settings.isEnableAutosave();
			
			if (mainFrame != null)
			{
				this.mainFrame = mainFrame;
				if (createVioletDirectory())
				{
					openAutoSaveProjects();
					initializeTimer();
				}
			}
		}
	}

	/**
	 * Create Violet directory
	 * @return true if path was created
	*/
	private boolean createVioletDirectory()
	{
		File directory = new File(autoSaveDirectory);
		boolean result = directory.isDirectory();
		if(!result)
		{
			result = directory.mkdir();
		}
		return result;
	}

	/**
	 * Open autosave project
	*/
	private void openAutoSaveProjects()
	{
		File directory = new File(autoSaveDirectory);
		if (directory.isDirectory())
		{
			File[] files = directory.listFiles();
			if (files.length == 0)
				return;

			for (File file: files)
			{
				try {
					IFile autoSaveFile = new LocalFile(file);
					IFileReader readFile = new JFileReader(file);
					InputStream in = readFile.getInputStream();
					if (in != null)
					{
						IGraphFile graphFile = new GraphFile(autoSaveFile);
					
						IWorkspace workspace = new Workspace(graphFile);
						mainFrame.addWorkspace(workspace);
					
						file.delete();
					}
				} catch (IOException e) {
					file.delete();
				} catch (Exception e) {
					file.delete();
				}
			}
		}
	}

	/**
	 * Initialize timer
	*/
	private void initializeTimer()
	{
		saveTimer = new Timer(saveInterval, (ActionListener) this);
		saveTimer.setInitialDelay(0);
		saveTimer.start();
	}

	/**
	 * Action Performed
	 *  @param action event
	*/
	@Override
	public void actionPerformed(ActionEvent e) {
			for (IWorkspace workspace: mainFrame.getWorkspaceList())
		{
			IGraphFile graphFile = workspace.getGraphFile();
			if (autoSaveEnabled && graphFile.isSaveRequired())
			{
				graphFile.autoSave(autoSaveDirectory);
			}
		}
	}
}
