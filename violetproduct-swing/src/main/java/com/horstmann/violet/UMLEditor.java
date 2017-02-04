package com.horstmann.violet;

import com.horstmann.violet.framework.dialog.DialogFactory;
import com.horstmann.violet.framework.dialog.DialogFactoryMode;
import com.horstmann.violet.framework.file.chooser.IFileChooserService;
import com.horstmann.violet.framework.file.chooser.JFileChooserService;
import com.horstmann.violet.framework.file.persistence.IFilePersistenceService;
import com.horstmann.violet.framework.file.persistence.XHTMLPersistenceService;
import com.horstmann.violet.framework.injection.bean.ManiocFramework;
import com.horstmann.violet.framework.theme.*;
import com.horstmann.violet.framework.userpreferences.DefaultUserPreferencesDao;
import com.horstmann.violet.framework.userpreferences.IUserPreferencesDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Narin on 09.12.2016.
 * This class help extract a method that makes init of Bean.
 */
public class UMLEditor {

    public void init() {
        ManiocFramework.BeanFactory.getFactory().register(IUserPreferencesDao.class, new DefaultUserPreferencesDao());
        createThemeManager();
        ManiocFramework.BeanFactory.getFactory().register(DialogFactory.class, new DialogFactory(DialogFactoryMode.INTERNAL));
        ManiocFramework.BeanFactory.getFactory().register(IFilePersistenceService.class, new XHTMLPersistenceService());
        ManiocFramework.BeanFactory.getFactory().register(IFileChooserService.class, new JFileChooserService());
    }

    private void createThemeManager() {
        final ThemeManager themeManager = new ThemeManager();
        final List<ITheme> themeList = new ArrayList<ITheme>();
        themeList.add(new ClassicMetalTheme());
        themeList.add(new BlueAmbianceTheme());
        themeList.add(new DarkAmbianceTheme());
        themeList.add(new DarkBlueTheme());
        themeManager.setInstalledThemes(themeList);
        themeManager.applyPreferedTheme();
        ManiocFramework.BeanFactory.getFactory().register(ThemeManager.class, themeManager);
        themeManager.applyPreferedTheme();
    }
}
