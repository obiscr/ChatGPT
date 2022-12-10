// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.obiscr.chatgpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Wuzi
 */
public class ChatGPTSettingsPanel implements Configurable, Disposable {
  private JPanel myMainPanel;
  private JBRadioButton defaultChoice;
  private JBRadioButton officialChoice;
  private JBRadioButton customizeChoice;
  private JBTextField customizeUrlField;
  private JTextField accessTokenArea;
  private JPanel defaultOptions;
  private JPanel officialOptions;
  private JPanel customizeOptions;
  private JBLabel defaultIntroduce;
  private JPanel officialIntroducePanel;
  private JBLabel officialIntroduce;


  public ChatGPTSettingsPanel() {
    init();
  }

  private void init() {
    register(defaultChoice, SettingConfiguration.SettingURLType.DEFAULT);
    register(officialChoice, SettingConfiguration.SettingURLType.OFFICIAL);
    register(customizeChoice, SettingConfiguration.SettingURLType.CUSTOMIZE);

    ItemListener connectionTypeChangedListener = e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        enableOptions(e.getSource());
      }
    };
    defaultChoice.addItemListener(connectionTypeChangedListener);
    officialChoice.addItemListener(connectionTypeChangedListener);
    customizeChoice.addItemListener(connectionTypeChangedListener);

    enableOptions(customizeChoice);

    customizeUrlField.getEmptyText().setText("Set your own server url");
  }


  private void enableOptions(Object source) {
    UIUtil.setEnabled(defaultOptions, defaultChoice.equals(source), true);
    UIUtil.setEnabled(officialOptions, officialChoice.equals(source), true);
    UIUtil.setEnabled(customizeOptions, customizeChoice.equals(source), true);
  }

  @Override
  public void reset() {
    SettingsState state = SettingsState.getInstance();

    setUrlChoice(state.urlType);
    accessTokenArea.setText(state.accessToken);
    customizeUrlField.setText(state.customizeUrl);
  }

  @Override
  public @Nullable JComponent createComponent() {
    return myMainPanel;
  }

  @Override
  public boolean isModified() {
    SettingsState state = SettingsState.getInstance();

    return !state.urlType.equals(getUrlChoice()) ||
           !StringUtil.equals(state.accessToken, accessTokenArea.getText()) ||
           !StringUtil.equals(state.customizeUrl, customizeUrlField.getText());
  }

  @Override
  public void apply() {
    SettingsState state = SettingsState.getInstance();

    state.urlType = getUrlChoice();
    state.accessToken = accessTokenArea.getText();
    state.customizeUrl = customizeUrlField.getText();
  }

  @Override
  public void dispose() {
  }

  private void setUrlChoice(@NotNull SettingConfiguration.SettingURLType value) {
    setSelected(defaultChoice, value);
    setSelected(officialChoice, value);
    setSelected(customizeChoice, value);
  }

  @NotNull
  private SettingConfiguration.SettingURLType getUrlChoice() {
    JBRadioButton selected = defaultChoice.isSelected()
                             ? defaultChoice
                             : officialChoice.isSelected()
                               ? officialChoice
                               : customizeChoice.isSelected() ? customizeChoice : null;

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
  public @NlsContexts.ConfigurableName String getDisplayName() {
    return ChatGPTBundle.message("setting.menu.text");
  }
}
