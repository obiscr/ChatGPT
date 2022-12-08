package com.obiscr.chatgpt.settings;

import com.intellij.find.SearchTextArea;
import com.intellij.ui.components.*;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Wuzi
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class SettingsComponent {

  private final JPanel myMainPanel;
  private final JBTextArea accessTokenArea = new JBTextArea();
  private final JBCheckBox myIdeaUserStatus = new JBCheckBox("Do you use IntelliJ IDEA? ");

  public SettingsComponent() {
    accessTokenArea.setFont(UIUtil.getLabelFont());
    accessTokenArea.setLineWrap(true);
    JBScrollPane scrollPane = new JBScrollPane(accessTokenArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    myMainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(new JBLabel("Access token: "), scrollPane, 1, false)
            //.addComponent(myIdeaUserStatus, 1)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
  }

  public JPanel getPanel() {
    return myMainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return accessTokenArea;
  }

  @NotNull
  public String getAccessToken() {
    return accessTokenArea.getText();
  }

  public void setAccessToken(@NotNull String newText) {
    accessTokenArea.setText(newText);
  }

  public boolean getIdeaUserStatus() {
    return myIdeaUserStatus.isSelected();
  }

  public void setIdeaUserStatus(boolean newStatus) {
    myIdeaUserStatus.setSelected(newStatus);
  }

}
