package com.horstmann.violet.workspace.sidebar.historypanel;


import com.horstmann.violet.framework.theme.ThemeManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class HistoryPanelUI extends PanelUI {

    public HistoryPanelUI(com.horstmann.violet.workspace.sidebar.historypanel.HistoryPanel historyPanel) {
        this.historyPanel = historyPanel;
    }

    @Override
    public void installUI(JComponent c) {
        c.removeAll();
        this.historyPanel.setBackground(ThemeManager.getInstance().getTheme().getSidebarElementBackgroundColor());
        this.historyPanel.add(getHistoryPanel());
    }

    private JPanel getHistoryPanel() {
        if (this.panel == null) {
            this.panel = new JPanel();
            this.panel.setOpaque(true);
            this.panel.setBorder(new EmptyBorder(0, 0, 0, 0));
            this.panel.setPreferredSize(new Dimension(215, 100));
            FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 10, 10);
            this.panel.setLayout(layout);
        }
        return this.historyPanel;
    }
    private JPanel panel;
    private HistoryPanel historyPanel;
}