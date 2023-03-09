// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.obiscr.chatgpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Wuzi
 */
public class GPT3_35_TurboPanel implements Configurable, Disposable {
    private JPanel myMainPanel;
    private JPanel apiKeyTitledBorderBox;
    private JBTextField apiKeyField;
    private JComboBox<String> comboCombobox;
    private JPanel modelTitledBorderBox;
    private JCheckBox enableContextCheckBox;
    private JLabel contextLabel;


    public GPT3_35_TurboPanel() {
        init();
    }

    private void init() {
        apiKeyField.getEmptyText().setText("Your API Key, find it in: https://platform.openai.com/account/api-keys");
        initHelp();
    }



    @Override
    public void reset() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        apiKeyField.setText(state.apiKey);
        comboCombobox.setSelectedItem(state.gpt35Model);
        enableContextCheckBox.setSelected(state.enableContext);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        return !state.apiKey.equals(apiKeyField.getText()) ||
               !state.gpt35Model.equals(comboCombobox.getSelectedItem().toString()) ||
               !state.enableContext == enableContextCheckBox.isSelected();
    }

    @Override
    public void apply() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        state.apiKey = apiKeyField.getText();
        state.gpt35Model = comboCombobox.getSelectedItem().toString();
        state.enableContext = enableContextCheckBox.isSelected();
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getDisplayName() {
        return ChatGPTBundle.message("ui.setting.menu.text");
    }

    private void createUIComponents() {
        apiKeyTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator("API Key Settings");
        apiKeyTitledBorderBox.add(tsUrl,BorderLayout.CENTER);

        modelTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator mdUrl = new TitledSeparator("Model Settings");
        modelTitledBorderBox.add(mdUrl,BorderLayout.CENTER);
    }

    private void initHelp() {
        contextLabel.setFont(JBUI.Fonts.smallFont());
        contextLabel.setForeground(UIUtil.getContextHelpForeground());
    }
}
