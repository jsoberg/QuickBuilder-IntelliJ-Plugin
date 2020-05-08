package com.soberg.dev.quickbuilder.ui.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class QuickBuilderSettingsPanel implements Configurable {

    private final SettingsPreferences preferences;
    private final FieldModifierPanel fieldModifierPanel;

    public QuickBuilderSettingsPanel(Project project) {
        this.preferences = ServiceManager.getService(project, SettingsPreferences.class);
        this.fieldModifierPanel = new FieldModifierPanel(preferences);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "QuickBuilder";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel panel = createMainPanel();
        GridBagConstraints constraints = createGridBagConstraints();
        // Builder field modifiers
        panel.add(fieldModifierPanel.createPanel(), constraints);

        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        addFillerPanel(panel, constraints);
        return panel;
    }

    @NotNull
    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        return panel;
    }

    @NotNull
    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        return constraints;
    }

    private void addFillerPanel(JPanel parent, GridBagConstraints constraints) {
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        parent.add(filler, constraints);
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {
    }
}
