
package com.obiscr.chatgpt.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Wuzi
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "com.obiscr.chatgpt.settings.SettingsState",
        storages = @Storage("ChatGPTSettingsPlugin.xml")
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

  public String accessToken = "";
  public String customizeUrl = "";
  public String cloudFlareUrl = "";
  public SettingConfiguration.SettingURLType urlType =
          SettingConfiguration.SettingURLType.DEFAULT;
  public String readTimeout = "10000";
  public String connectionTimeout = "10000";

  public static SettingsState getInstance() {
    return ApplicationManager.getApplication().getService(SettingsState.class);
  }

  @Nullable
  @Override
  public SettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull SettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public String getAccessToken() {
    return accessToken;
  }
}
