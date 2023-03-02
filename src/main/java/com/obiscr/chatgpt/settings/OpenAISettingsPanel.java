// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.obiscr.chatgpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
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
public class OpenAISettingsPanel implements Configurable, Disposable {
    private JPanel myMainPanel;
    private JPanel urlTitledBorderBox;
    private JPanel connectionTitledBorderBox;
    private JBTextField readTimeoutField;
    private JBTextField connectionTimeoutField;
    private JCheckBox enableProxyCheckBox;
    private JBTextField portField;
    private JPanel proxyTitledBorderBox;
    private JBRadioButton proxyDirectChoice;
    private JBRadioButton proxyHttpChoice;
    private JBRadioButton proxySocksChoice;
    private JTextField hostnameField;
    private JPanel proxyOptions;


    public OpenAISettingsPanel() {
        init();
    }

    private void init() {
        register(proxyDirectChoice, SettingConfiguration.SettingProxyType.DIRECT);
        register(proxyHttpChoice, SettingConfiguration.SettingProxyType.HTTP);
        register(proxySocksChoice, SettingConfiguration.SettingProxyType.SOCKS);
        ItemListener proxyTypeChangedListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableProxyOptions(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                enableProxyOptions(false);
            }
        };
        enableProxyCheckBox.addItemListener(proxyTypeChangedListener);
        enableProxyOptions(false);

        readTimeoutField.getEmptyText().setText(ChatGPTBundle.message("ui.setting.connection.read_timeout.empty_text"));
        connectionTimeoutField.getEmptyText().setText(ChatGPTBundle.message("ui.setting.connection.connection_timeout.empty_text"));
    }

    private void enableProxyOptions(boolean enabled) {
        UIUtil.setEnabled(proxyOptions, enabled, true);
    }

    @Override
    public void reset() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        readTimeoutField.setText(state.readTimeout);
        connectionTimeoutField.setText(state.connectionTimeout);

        enableProxyCheckBox.setSelected(state.enableProxy);
        setProxyChoice(state.proxyType);
        hostnameField.setText(state.proxyHostname);
        portField.setText(state.proxyPort);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        return
                !StringUtil.equals(state.readTimeout, readTimeoutField.getText()) ||
                !StringUtil.equals(state.connectionTimeout, connectionTimeoutField.getText()) ||
                !state.proxyType.equals(getProxyChoice()) ||
                !state.enableProxy == enableProxyCheckBox.isSelected() ||
                !StringUtil.equals(state.proxyHostname, hostnameField.getText()) ||
                !StringUtil.equals(state.proxyPort, portField.getText());
    }

    @Override
    public void apply() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        boolean readTimeoutIsNumber = com.obiscr.chatgpt.util.
                StringUtil.isNumber(readTimeoutField.getText());
        boolean connectionTimeoutIsNumber = com.obiscr.chatgpt.util.
                StringUtil.isNumber(connectionTimeoutField.getText());
        state.readTimeout = !readTimeoutIsNumber ? "50000" : readTimeoutField.getText();
        state.connectionTimeout = !connectionTimeoutIsNumber ? "50000" : connectionTimeoutField.getText();

        state.proxyType = getProxyChoice();
        state.enableProxy = enableProxyCheckBox.isSelected();
        state.proxyHostname = hostnameField.getText();

        boolean portIsNumber = com.obiscr.chatgpt.util.
                StringUtil.isNumber(portField.getText());
        state.proxyPort = !portIsNumber ? "0" : portField.getText();
    }

    @Override
    public void dispose() {
    }

    private void setProxyChoice(@NotNull SettingConfiguration.SettingProxyType value) {
        setSelected(proxyDirectChoice, value);
        setSelected(proxyHttpChoice, value);
        setSelected(proxySocksChoice, value);
    }

    @NotNull
    private SettingConfiguration.SettingProxyType getProxyChoice() {
        JBRadioButton selected = proxyDirectChoice.isSelected()
                ? proxyDirectChoice
                : proxyHttpChoice.isSelected()
                ? proxyHttpChoice
                : proxySocksChoice.isSelected()
                ? proxySocksChoice : null;

        if (selected == null) {
            selected = proxyDirectChoice;
        }
        assert selected != null;

        return (SettingConfiguration.SettingProxyType)selected.getClientProperty("value");
    }

    private static void register(@NotNull JBRadioButton choice, @NotNull SettingConfiguration.SettingProxyType value) {
        choice.putClientProperty("value", value);
    }

    private static void setSelected(@NotNull JBRadioButton choice, @NotNull SettingConfiguration.SettingProxyType value) {
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
