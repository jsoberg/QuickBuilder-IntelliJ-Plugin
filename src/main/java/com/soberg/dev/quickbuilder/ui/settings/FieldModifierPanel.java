package com.soberg.dev.quickbuilder.ui.settings;

import com.intellij.ui.IdeBorderFactory;
import com.soberg.dev.quickbuilder.ui.settings.SettingsPreferences.FieldModifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

class FieldModifierPanel {

    private final SettingsPreferences preferences;

    FieldModifierPanel(SettingsPreferences preferences) {
        this.preferences = preferences;
    }

    JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(IdeBorderFactory.createTitledBorder("Builder Field Modifier"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        SettingsPreferences.State state = preferences.getState();
        addRadioButtons(panel, state);

        panel.add(Box.createHorizontalGlue());
        panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
        return panel;
    }

    private void addRadioButtons(JPanel panel, SettingsPreferences.State state) {
        ButtonGroup group = new ButtonGroup();
        for (FieldModifier modifier : FieldModifier.values()) {
            boolean selected = modifier == state.fieldModifier;
            JRadioButton button = new JRadioButton(modifier.displayName, selected);
            button.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    preferences.setFieldModifier(modifier);
                }
            });
            panel.add(button);
            group.add(button);
        }
    }
}
