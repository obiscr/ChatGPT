// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.obiscr.chatgpt.settings;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.*;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.AccessTokenDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Wuzi
 */
public class ChatGPTSettingsPanel implements Configurable, Disposable {
    private JPanel myMainPanel;
    private JBTextField customizeUrlField;
    private JPanel tokenTitledBorderBox;
    private ExpandableTextField accessTokenField;
    private JTextField expireTimeField;
    private JButton loginButton;
    private JPanel modelTitledBorderBox;
    private JComboBox<String> comboCombobox;
    private ActionLink unofficialHelpLabel;
    private JCheckBox enableCustomizeChatGPTUrlCheckBox;
    private JPanel urlTitledBorderBox;
    private JPanel customizeServerOptions;


    public ChatGPTSettingsPanel() {
        init();
    }

    private void init() {
        customizeUrlField.getEmptyText().setText(ChatGPTBundle.message("ui.setting.url.customize.url.empty_text"));
        accessTokenField.getEmptyText().setText("Click Get Token Button to get access token");

        expireTimeField.setEnabled(false);
        expireTimeField.setEditable(false);

        ItemListener proxyTypeChangedListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableCustomizeServerOptions(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                enableCustomizeServerOptions(false);
            }
        };
        enableCustomizeChatGPTUrlCheckBox.addItemListener(proxyTypeChangedListener);
        enableCustomizeServerOptions(false);

        loginButton.addActionListener(e -> {
            new AccessTokenDialog(null, this).show();
        });
    }

    public ExpandableTextField getAccessTokenField() {
        return accessTokenField;
    }

    public JTextField getExpireTimeField() {
        return expireTimeField;
    }

    @Override
    public void reset() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        accessTokenField.setText(state.accessToken);
        expireTimeField.setText(state.expireTime);
        enableCustomizeChatGPTUrlCheckBox.setSelected(state.enableCustomizeChatGPTUrl);
        customizeUrlField.setText(state.customizeUrl);
        comboCombobox.setSelectedItem(state.chatGptModel);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        return !StringUtil.equals(state.customizeUrl, customizeUrlField.getText()) ||
                !StringUtil.equals(state.accessToken, accessTokenField.getText()) ||
                !StringUtil.equals(state.expireTime, expireTimeField.getText()) ||
                !state.chatGptModel.equals(comboCombobox.getSelectedItem()) ||
                !state.enableCustomizeChatGPTUrl == enableCustomizeChatGPTUrlCheckBox.isSelected()
                ;
    }

    @Override
    public void apply() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        state.customizeUrl = customizeUrlField.getText();
        state.accessToken = accessTokenField.getText();
        state.expireTime = expireTimeField.getText();
        state.chatGptModel = comboCombobox.getSelectedItem().toString();
        state.enableCustomizeChatGPTUrl = enableCustomizeChatGPTUrlCheckBox.isSelected();
    }

    @Override
    public void dispose() {
    }


    @Override
    public String getDisplayName() {
        return ChatGPTBundle.message("ui.setting.menu.text");
    }

    private void enableCustomizeServerOptions(boolean enabled) {
        UIUtil.setEnabled(customizeServerOptions, enabled, true);
    }

    private void createUIComponents() {
        tokenTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator("Access Token Settings");
        tokenTitledBorderBox.add(tsUrl,BorderLayout.CENTER);

        modelTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator mdModel = new TitledSeparator("Model Settings");
        modelTitledBorderBox.add(mdModel,BorderLayout.CENTER);

        urlTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator mdUrl = new TitledSeparator("Server Settings");
        urlTitledBorderBox.add(mdUrl,BorderLayout.CENTER);

        unofficialHelpLabel = new ActionLink("Safety instructions about server", new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                BrowserUtil.browse("https://chatgpt.en.obiscr.com/settings/chatgpt-settings/#unofficial-unstable");
            }
        });
    }
}
