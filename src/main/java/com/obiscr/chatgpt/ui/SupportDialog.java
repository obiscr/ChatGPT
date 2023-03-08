package com.obiscr.chatgpt.ui;

import com.intellij.CommonBundle;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import com.obiscr.chatgpt.icons.ChatGPTIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * @author Wuzi
 */
public class SupportDialog extends DialogWrapper {

    private JPanel panel;

    public SupportDialog(@Nullable Project project) {
        super(project);
        setTitle("Support / Donate");
        setResizable(false);
        setOKButtonText("Thanks for Your Supporting!");
        init();
        setOKActionEnabled(true);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        panel = new JPanel();
        panel.setLayout(new VerticalLayout(JBUIScale.scale(8)));
        panel.setBorder(JBUI.Borders.empty(20));
        panel.setBackground(UIManager.getColor("TextArea.background"));

        panel.add(new JBLabel("You can contribute or support this project through the following ways:"));
        panel.add(createItemPanel());
        panel.add(new JBLabel("If you like this plugin, please consider donating it, it will encourage me to develop better plugins."));
        panel.add(createDonatePanel());
        panel.add(createSupportPanel());
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
        myOKAction = new DialogWrapperAction(CommonBundle.getOkButtonText()) {
            @Override
            protected void doAction(ActionEvent e) {
                dispose();
                close(OK_EXIT_CODE);
            }
        };
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        return actions.toArray(new Action[0]);
    }

    private JPanel createItemPanel() {
        JPanel jPanel = new NonOpaquePanel();
        jPanel.setLayout(new GridLayout(5,1));
        jPanel.add(createActionLink("1. Star this project on GitHub","https://github.com/dromara/ChatGPT"));
        jPanel.add(createActionLink("2. Report bugs","https://github.com/dromara/ChatGPT/issues"));
        jPanel.add(createActionLink("3. Tell me your ideas","https://github.com/dromara/ChatGPT/discussions"));
        jPanel.add(createActionLink("4. Create pull requests","https://github.com/dromara/ChatGPT"));
        jPanel.add(createActionLink("5. Share this plugin with you friends","https://github.com/dromara/ChatGPT"));
        return jPanel;
    }

    private JPanel createDonatePanel() {
        JPanel jPanel = new NonOpaquePanel();
        jPanel.setLayout(new GridLayout(2,1));
        jPanel.add(createActionLink("1. PayPal","https://paypal.me/obiscr"));
        jPanel.add(createActionLink("2. Buy me a coffee","https://www.buymeacoffee.com/obiscr"));
        return jPanel;
    }

    private ActionLink createActionLink(String text, String url) {
        ActionLink actionLink = new ActionLink(text);
        actionLink.addActionListener(e -> BrowserUtil.browse(url));
        return actionLink;
    }

    private JPanel createSupportPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JPanel alipay = new JPanel(new BorderLayout());
        alipay.add(new JLabel(ChatGPTIcons.ALIPAY), BorderLayout.CENTER);
        JLabel alipayLabel = new JLabel("Alipay", SwingConstants.CENTER);
        alipayLabel.setBorder(JBUI.Borders.empty(10, 0));
        alipay.add(alipayLabel, BorderLayout.SOUTH);
        alipay.setBorder(JBUI.Borders.empty(20));

        JPanel wechat = new JPanel(new BorderLayout());
        wechat.add(new JLabel(ChatGPTIcons.WE_CHAT), BorderLayout.CENTER);
        JLabel weChatPayLabel = new JLabel("WeChat Pay", SwingConstants.CENTER);
        weChatPayLabel.setBorder(JBUI.Borders.empty(10, 0));
        wechat.add(weChatPayLabel, BorderLayout.SOUTH);
        wechat.setBorder(JBUI.Borders.empty(20));

        JPanel coffee = new JPanel(new BorderLayout());
        coffee.add(new JLabel(ChatGPTIcons.BUE_ME_A_COFFEE), BorderLayout.CENTER);
        JLabel buyMeACoffeePay = new JLabel("Buy me a coffee", SwingConstants.CENTER);
        buyMeACoffeePay.setBorder(JBUI.Borders.empty(10, 0));
        coffee.add(buyMeACoffeePay, BorderLayout.SOUTH);
        coffee.setBorder(JBUI.Borders.empty(20));

        panel.add(alipay);
        panel.add(wechat);
        //panel.add(coffee);

        return panel;
    }
}
