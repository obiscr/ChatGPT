package com.obiscr.chatgpt.ui;

import com.alibaba.fastjson2.JSONArray;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.NullableComponent;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.core.Constant;
import com.obiscr.chatgpt.core.ConversationManager;
import com.obiscr.chatgpt.core.builder.OfficialBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MessageGroupComponent extends JBPanel<MessageGroupComponent> implements NullableComponent {
    private final JPanel myList = new JPanel(new VerticalLayout(JBUI.scale(10)));
    private final MyScrollPane myScrollPane = new MyScrollPane(myList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    private int myScrollValue = 0;

    private final MyAdjustmentListener scrollListener = new MyAdjustmentListener();
    private final MessageComponent chatGPTExplanation =
            new MessageComponent(Constant.getChatGPTContent(),false);
    private final MessageComponent gpt35TurboModelExplanation =
            new MessageComponent(Constant.getGpt35TurboContent(),false);
    private final MessageComponent tips =
            new MessageComponent("Type anything to ask me.",false);
    private final MessageComponent mustRead =
            new MessageComponent("Must Read: <a href='https://chatgpt.en.obiscr.com/getting-started/'>https://chatgpt.en.obiscr.com/getting-started/</a><br />&#20351;&#29992;&#24517;&#35835;: <a href='https://chatgpt.cn.obiscr.com/getting-started/'>https://chatgpt.cn.obiscr.com/getting-started/</a>",false);
    private JBTextField systemRole;
    private static final String systemRoleText = "You are a helpful language assistant";
    private final JSONArray messages = new JSONArray();
    public MessageGroupComponent(@NotNull Project project, boolean isChatGPT) {
        setBorder(JBUI.Borders.empty(10, 10, 10, 0));
        setLayout(new BorderLayout(JBUI.scale(7), 0));
        setBackground(UIUtil.getListBackground());

        JPanel mainPanel = new JPanel(new BorderLayout(0, JBUI.scale(8)));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(JBUI.Borders.emptyLeft(8));

        if (!isChatGPT) {
            JPanel panel = new NonOpaquePanel(new GridLayout(2,1));
            panel.add(new JBLabel(" System role: you can direct your assistant and set its behavior"));
            systemRole = new JBTextField();
            systemRole.getEmptyText().setText(systemRoleText);
            panel.add(systemRole);
            panel.setBorder(JBUI.Borders.empty(0,8,10,10));
            add(panel,BorderLayout.NORTH);
        }

        add(mainPanel,BorderLayout.CENTER);

        JBLabel myTitle = new JBLabel("Conversation");
        myTitle.setForeground(JBColor.namedColor("Label.infoForeground", new JBColor(Gray.x80, Gray.x8C)));
        myTitle.setFont(JBFont.label());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(JBUI.Borders.empty(0,10,10,0));

        panel.add(myTitle, BorderLayout.WEST);

        LinkLabel<String> newChat = new LinkLabel<>("New chat", null);
        newChat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                myList.removeAll();
                myList.add(tips);
                myList.add(mustRead);
                myList.updateUI();
                if (isChatGPT) {
                    ConversationManager.getInstance(project).setConversationId(null);
                } else {
                    messages.clear();
                }
            }
        });

        newChat.setFont(JBFont.label());
        newChat.setBorder(JBUI.Borders.emptyRight(20));
        panel.add(newChat, BorderLayout.EAST);
        mainPanel.add(panel, BorderLayout.NORTH);

        myList.setOpaque(true);
        myList.setBackground(UIUtil.getListBackground());
        myList.setBorder(JBUI.Borders.emptyRight(10));

        myScrollPane.setBorder(null);
        mainPanel.add(myScrollPane);
        myScrollPane.getVerticalScrollBar().setAutoscrolls(true);
        myScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            int value = e.getValue();
            if (myScrollValue == 0 && value > 0 || myScrollValue > 0 && value == 0) {
                myScrollValue = value;
                repaint();
            }
            else {
                myScrollValue = value;
            }
        });

        // Add the default message
        if (isChatGPT) {
            add(chatGPTExplanation);
        } else {
            add(gpt35TurboModelExplanation);
        }
        add(tips);
        add(mustRead);
    }

    public void add(MessageComponent messageComponent) {
        // The component should be immediately added to the
        // container and displayed in the UI

        // SwingUtilities.invokeLater(() -> {
        myList.add(messageComponent);
        updateLayout();
        scrollToBottom();
        updateUI();
        // });
    }

    public void scrollToBottom() {
        JScrollBar verticalScrollBar = myScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    public void updateLayout() {
        LayoutManager layout = myList.getLayout();
        int componentCount = myList.getComponentCount();
        for (int i = 0 ; i< componentCount ; i++) {
            layout.removeLayoutComponent(myList.getComponent(i));
            layout.addLayoutComponent(null,myList.getComponent(i));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (myScrollValue > 0) {
            g.setColor(JBColor.border());
            int y = myScrollPane.getY() - 1;
            g.drawLine(0, y, getWidth(), y);
        }
    }


    @Override
    public boolean isVisible() {
        if (super.isVisible()) {
            int count = myList.getComponentCount();
            for (int i = 0 ; i < count ; i++) {
                if (myList.getComponent(i).isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isNull() {
        return !isVisible();
    }

    static class MyAdjustmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            JScrollBar source = (JScrollBar) e.getSource();
            if (!source.getValueIsAdjusting()) {
                source.setValue(source.getMaximum());
            }
        }
    }

    public void addScrollListener() {
        myScrollPane.getVerticalScrollBar().
                addAdjustmentListener(scrollListener);
    }

    public void removeScrollListener() {
        myScrollPane.getVerticalScrollBar().
                removeAdjustmentListener(scrollListener);
    }

    public JSONArray getMessages() {
        return messages;
    }

    public String getSystemRole() {
        if (systemRole.getText().isEmpty()) {
            return systemRoleText;
        }
        return systemRole.getText();
    }
}
