package com.horstmann.violet.application.autosave;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Violet's autosave settings test
 * @author Marta Mrugalska
 */

public class AutosaveSettingsTest {

	AutosaveSettings autosaveSettings;

	@Test
	public void testIsLoadingDialog() {
		boolean expectedResult = false;
		autosaveSettings = new AutosaveSettings();
		boolean result = autosaveSettings.isLoadingDialog();
		assertEquals(expectedResult, result);
	}

	@Test
	public void testIsEnableAutosave() {
		boolean expectedResult = false;
		autosaveSettings = new AutosaveSettings();
		autosaveSettings.setEnableAutosave(expectedResult);
		boolean result = autosaveSettings.isEnableAutosave();
		assertEquals(expectedResult, result);
	}

	@Test
	public void testSetIsEnableAutosave() {
		boolean isEnableAutosave = false;
		autosaveSettings = new AutosaveSettings();
		autosaveSettings.setEnableAutosave(isEnableAutosave);
		assertEquals(isEnableAutosave, autosaveSettings.isEnableAutosave());
	}

	@Test
	public void testGetAutosaveInterval() {
		int expectedResult = 0;
		autosaveSettings = new AutosaveSettings();
		autosaveSettings.setAutosaveInterval(expectedResult);
		int result = autosaveSettings.getAutosaveInterval();
		assertEquals(expectedResult, result);
	}

	@Test
	public void testSetAutosaveInterval() {
		int autoSaveInterval = 1;
		autosaveSettings = new AutosaveSettings();
		autosaveSettings.setAutosaveInterval(autoSaveInterval);
		assertEquals(autoSaveInterval, autosaveSettings.getAutosaveInterval());
	}

	@Test
	public void testGetAutosavePath() {
		String expectedResult = "c:\\ff";
		autosaveSettings = new AutosaveSettings();
		autosaveSettings.setAutosavePath(expectedResult);
		String result = autosaveSettings.getAutosavePath();
		assertEquals(expectedResult, result);
	}

	@Test
	public void testSetAutosavePath() {
		String autoSavePath = "c:\\";
		autosaveSettings = new AutosaveSettings();
		autosaveSettings.setAutosavePath(autoSavePath);
		assertEquals(autoSavePath, autosaveSettings.getAutosavePath());
	}

}
