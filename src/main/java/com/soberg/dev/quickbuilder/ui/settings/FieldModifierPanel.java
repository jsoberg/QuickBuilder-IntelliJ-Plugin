package com.soberg.dev.quickbuilder.ui.settings;

import com.intellij.ui.IdeBorderFactory;

import javax.swing.*;
import java.awt.*;

class FieldModifierPanel {

    FieldModifierPanel() {
    }

    JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(IdeBorderFactory.createTitledBorder("Builder Field Modifier"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        ButtonGroup group = new ButtonGroup();
        JRadioButton privateButton = new JRadioButton("private");
        group.add(privateButton);
        panel.add(privateButton);
        JRadioButton packagePrivateButton = new JRadioButton("package-private");
        group.add(packagePrivateButton);
        panel.add(packagePrivateButton);
        JRadioButton publicButton = new JRadioButton("public");
        group.add(publicButton);
        panel.add(publicButton);

        panel.add(Box.createHorizontalGlue());
        panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
        return panel;
    }
}
