// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.obiscr.chatgpt.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.core.TokenManager;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Wuzi
 */
public class ChatGPTSettingsPanel implements Configurable, Disposable {
    private JPanel myMainPanel;
    private JBRadioButton officialChoice;
    private JBRadioButton customizeChoice;
    private JBTextField customizeUrlField;
    private JPanel officialOptions;
    private JPanel customizeOptions;
    private JPanel urlTitledBorderBox;
    private JPanel connectionTitledBorderBox;
    private JBTextField emailField;
    private JBTextField passwordField;
    private JPanel proxyTitledBorderBox;
    private JEditorPane accessTokenField;
    private JTextField expireTimeField;
    private JButton loginButton;
    private JPanel officialIntroducePanel;
    private JPanel customizeIntroducePanel;



    public ChatGPTSettingsPanel() {
        init();
    }

    private void init() {
        register(officialChoice, SettingConfiguration.SettingURLType.OFFICIAL);
        register(customizeChoice, SettingConfiguration.SettingURLType.CUSTOMIZE);
        ItemListener connectionTypeChangedListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableUrlOptions(e.getSource());
            }
        };
        officialChoice.addItemListener(connectionTypeChangedListener);
        customizeChoice.addItemListener(connectionTypeChangedListener);
        enableUrlOptions(customizeChoice);
        officialChoice.setEnabled(true);

        customizeUrlField.getEmptyText().setText(ChatGPTBundle.message("ui.setting.url.customize.url.empty_text"));
        emailField.getEmptyText().setText("Your OpenAI account");
        passwordField.getEmptyText().setText("Your OpenAI password");

        accessTokenField.setBorder(JBUI.Borders.customLine(JBColor.border(),1));
        expireTimeField.setEnabled(false);
        expireTimeField.setEditable(false);

        loginButton.addActionListener(e -> {
            if (StringUtil.isEmpty(emailField.getText()) || StringUtil.isEmpty(passwordField.getText())) {
                MessageDialogBuilder.yesNo("No login details provided!", "To login, the email and password are required, " +
                        "please configure it at first.")
                        .yesText("Got it")
                        .noText("Close").ask(myMainPanel);
                return;
            }

            String result = TokenManager.getInstance().refreshToken(emailField.getText(), passwordField.getText());
            if (!"success".equals(result)) {
                MessageDialogBuilder.yesNo("Login failed!", result + " Please check idea.log for more details.")
                        .yesText("Got it")
                        .noText("Close").ask(myMainPanel);
                return;
            }

            OpenAISettingsState state = OpenAISettingsState.getInstance();
            accessTokenField.setText(state.accessToken);
            expireTimeField.setText(state.expireTime);
            OpenAISettingsState.getInstance().reload();
            myMainPanel.updateUI();
            apply();
        });
    }


    private void enableUrlOptions(Object source) {
        UIUtil.setEnabled(officialOptions, officialChoice.equals(source), true);
        UIUtil.setEnabled(customizeOptions, customizeChoice.equals(source), true);
    }

    @Override
    public void reset() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        setUrlChoice(state.urlType);
        emailField.setText(state.email);
        passwordField.setText(state.password);
        accessTokenField.setText(state.accessToken);
        expireTimeField.setText(state.expireTime);

        customizeUrlField.setText(state.customizeUrl);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        return !state.urlType.equals(getUrlChoice()) ||
                !StringUtil.equals(state.customizeUrl, customizeUrlField.getText()) ||
                !StringUtil.equals(state.email, emailField.getText()) ||
                !StringUtil.equals(state.password, passwordField.getText()) ||
                !StringUtil.equals(state.accessToken, accessTokenField.getText()) ||
                !StringUtil.equals(state.expireTime, expireTimeField.getText());
    }

    @Override
    public void apply() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        state.urlType = getUrlChoice();
        state.customizeUrl = customizeUrlField.getText();
        state.email = emailField.getText();
        state.password = passwordField.getText();
        state.accessToken = accessTokenField.getText();
        state.expireTime = expireTimeField.getText();
    }

    @Override
    public void dispose() {
    }

    private void setUrlChoice(@NotNull SettingConfiguration.SettingURLType value) {
        setSelected(officialChoice, value);
        setSelected(customizeChoice, value);
    }

    @NotNull
    private SettingConfiguration.SettingURLType getUrlChoice() {
        JBRadioButton selected = officialChoice.isSelected()
                ? officialChoice
                : customizeChoice.isSelected()
                ? customizeChoice : null;

        assert selected != null;

        return (SettingConfiguration.SettingURLType)selected.getClientProperty("value");
    }

    private static void register(@NotNull JBRadioButton choice, @NotNull SettingConfiguration.SettingURLType value) {
        choice.putClientProperty("value", value);
    }

    private static void setSelected(@NotNull JBRadioButton choice, @NotNull SettingConfiguration.SettingURLType value) {
        choice.setSelected(value.equals(choice.getClientProperty("value")));
    }

    @Override
    public String getDisplayName() {
        return ChatGPTBundle.message("ui.setting.menu.text");
    }

    private void createUIComponents() {
        urlTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator(ChatGPTBundle.message("ui.setting.url.title"));
        urlTitledBorderBox.add(tsUrl,BorderLayout.CENTER);

        connectionTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsConnection = new TitledSeparator(ChatGPTBundle.message("ui.setting.connection.title"));
        connectionTitledBorderBox.add(tsConnection,BorderLayout.CENTER);

        proxyTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsProxy = new TitledSeparator("Proxy Settings");
        proxyTitledBorderBox.add(tsProxy,BorderLayout.CENTER);
    }
}
