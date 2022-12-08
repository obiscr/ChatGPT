package com.obiscr.chatgpt.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Wuzi
 * Provides controller functionality for application settings.
 */
public class SettingsConfigurable implements Configurable {

  private SettingsComponent mySettingsComponent;

  // A default constructor with no arguments is required because this implementation
  // is registered as an applicationConfigurable EP

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "ChatGPT";
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return mySettingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    mySettingsComponent = new SettingsComponent();
    return mySettingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    SettingsState settings = SettingsState.getInstance();
    boolean modified = !mySettingsComponent.getAccessToken().equals(settings.accessToken);
    modified |= mySettingsComponent.getIdeaUserStatus() != settings.ideaStatus;
    return modified;
  }

  @Override
  public void apply() {
    SettingsState settings = SettingsState.getInstance();
    settings.accessToken = mySettingsComponent.getAccessToken();
    settings.ideaStatus = mySettingsComponent.getIdeaUserStatus();
  }

  @Override
  public void reset() {
    SettingsState settings = SettingsState.getInstance();
    mySettingsComponent.setAccessToken(settings.accessToken);
    mySettingsComponent.setIdeaUserStatus(settings.ideaStatus);
  }

  @Override
  public void disposeUIResources() {
    mySettingsComponent = null;
  }

}
