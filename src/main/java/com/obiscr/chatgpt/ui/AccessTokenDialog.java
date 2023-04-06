package com.obiscr.chatgpt.ui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.CommonBundle;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.TipDialog;
import com.intellij.ide.util.TipPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.icons.ChatGPTIcons;
import com.obiscr.chatgpt.settings.ChatGPTSettingsPanel;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * @author Wuzi
 */
public class AccessTokenDialog extends DialogWrapper {

    private JPanel panel;

    private final ChatGPTSettingsPanel mySettingsPanel;

    private ExpandableTextField accessTokenInfo;

    public AccessTokenDialog(@Nullable Project project, ChatGPTSettingsPanel settingsPanel) {
        super(project);
        mySettingsPanel = settingsPanel;
        setTitle("Get Access Token");
        setResizable(false);
        setOKButtonText("Thanks for Your Supporting!");
        init();
        setModal(false);
        setOKActionEnabled(true);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        panel = new JPanel();
        panel.setLayout(new VerticalLayout(JBUIScale.scale(8)));
        panel.setBorder(JBUI.Borders.empty(20));
        panel.setBackground(UIManager.getColor("TextArea.background"));

        panel.add(new JBLabel("Please follow the steps below to get the Access Token: "));
        panel.add(createDescriptionPanel());
        panel.add(createTextAreaPanel());
        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return panel;
    }

    @Override
    protected @NotNull DialogStyle getStyle() {
        return DialogStyle.COMPACT;
    }

    @Override
    protected Action @NotNull [] createActions() {
        myOKAction = new DialogWrapperAction("Save the Access Token") {
            @Override
            protected void doAction(ActionEvent e) {
                if (!accessTokenInfo.getText().isEmpty()) {
                    try {
                        if (accessTokenInfo.getText().startsWith("ey")) {
                            mySettingsPanel.getAccessTokenField().setText(accessTokenInfo.getText());
                        } else {
                            JsonObject object = JsonParser.parseString(accessTokenInfo.getText())
                                    .getAsJsonObject();
                            String accessToken = object.get("accessToken").getAsString();
                            String expires = object.get("expires").getAsString();
                            mySettingsPanel.getAccessTokenField().setText(accessToken);
                            mySettingsPanel.getExpireTimeField().setText(expires);
                        }
                    } catch (Exception ex) {
                        MessageDialogBuilder.okCancel("Save failed",
                                        "Save the Access Token failed, cause: " + ex.getMessage())
                                .ask(mySettingsPanel.createComponent());
                    }
                    mySettingsPanel.apply();
                }
                dispose();
                close(OK_EXIT_CODE);
            }
        };
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        return actions.toArray(new Action[0]);
    }

    private JPanel createDescriptionPanel() {
        JPanel jPanel = new NonOpaquePanel();
        jPanel.setLayout(new GridLayout(6,1));

        JBLabel step1 = new JBLabel("Step 1:");
        step1.setFont(JBUI.Fonts.label(16));
        jPanel.add(step1);
        jPanel.add(createActionLink("Click here to login: https://chat.openai.com/chat","https://chat.openai.com/chat"));
        JBLabel step2 = new JBLabel("Step 2:");
        step2.setFont(JBUI.Fonts.label(16));
        jPanel.add(step2);
        jPanel.add(createActionLink("After login, click here to get access token(via browser)","https://chat.openai.com/api/auth/session"));
        JBLabel step3 = new JBLabel("Step 3:");
        step3.setFont(JBUI.Fonts.label(16));
        jPanel.add(step3);
        jPanel.add(new JBLabel("Copy all text from step 2 to the text field below"));
        return jPanel;
    }

    private ActionLink createActionLink(String text, String url) {
        ActionLink actionLink = new ActionLink(text);
        actionLink.addActionListener(e -> BrowserUtil.browse(url));
        return actionLink;
    }

    private JPanel createTextAreaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        accessTokenInfo = new ExpandableTextField();
        panel.add(accessTokenInfo,BorderLayout.CENTER);
        return panel;
    }
}
