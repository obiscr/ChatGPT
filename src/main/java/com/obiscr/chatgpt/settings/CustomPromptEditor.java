// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.obiscr.chatgpt.settings;

import com.intellij.application.options.pathMacros.PathMacroConfigurable;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.io.IOException;

public class CustomPromptEditor extends DialogWrapper {
    private JTextField myKeyField;
    private JPanel myPanel;
    private ExpandableTextField myValueField;
    private final Validator myValidator;

    public interface Validator {
        boolean checkName(String name);
        boolean isOK(String name, String value);
    }

    public CustomPromptEditor(@NlsContexts.DialogTitle String title, String key, String value, Validator validator) {
        super(true);
        setTitle(title);
        myValidator = validator;
        myKeyField.setText(key);
        DocumentListener documentListener = new DocumentAdapter() {
            @Override
            public void textChanged(@NotNull DocumentEvent event) {
                updateControls();
            }
        };
        myKeyField.getDocument().addDocumentListener(documentListener);
        myValueField.setText(value);
        myValueField.setFont(JBUI.Fonts.label());
        init();
        updateControls();
    }

    public void setMacroNameEditable(boolean isEditable) {
        myKeyField.setEditable(isEditable);
    }

    private void updateControls() {
        final boolean isNameOK = myValidator.checkName(myKeyField.getText());
        getOKAction().setEnabled(isNameOK);
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return myKeyField;
    }

    @Override
    protected String getHelpId() {
        return PathMacroConfigurable.HELP_ID;
    }

    @Override
    protected void doOKAction() {
        if (!myValidator.isOK(getKey(), getValue())) return;
        super.doOKAction();
    }

    public String getKey() {
        return myKeyField.getText().trim();
    }

    public String getValue() {
        return myValueField.getText().trim();
    }

    @Override
    protected JComponent createNorthPanel() {
        return myPanel;
    }

    @Override
    protected JComponent createCenterPanel() {
        return null;
    }

    @Override
    protected void doHelpAction() {
        // TODO Open customize url
        super.doHelpAction();
    }
}
