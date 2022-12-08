package com.obiscr.chatgpt.ui;

import com.intellij.find.SearchTextArea;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.ui.listener.SendListener;
import com.obiscr.chatgpt.util.HtmlUtil;
import org.intellij.plugins.markdown.ui.preview.jcef.MarkdownJCEFHtmlPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Wuzi
 */
public class MainPanel {

    private final JPanel myToolWindowContent;

    private final SearchTextArea searchTextArea;
    private final JButton button;
    private final MarkdownJCEFHtmlPanel contentPanel;

    public MainPanel() {
        SendListener listener = new SendListener(this);

        searchTextArea = new SearchTextArea(new JBTextArea(),true);
        searchTextArea.getTextArea().addKeyListener(listener);

        button = new JButton("Send");
        button.addActionListener(listener);

        JPanel top = new JPanel(new BorderLayout());
        top.add(searchTextArea, BorderLayout.CENTER);
        top.add(button, BorderLayout.EAST);
        top.setBorder(UIUtil.getTextFieldBorder());
        contentPanel = new MarkdownJCEFHtmlPanel();
        String usage = ("""
                ## 使用说明
                                
                首先，您需要注册一个[OpenAI](https://auth0.openai.com/u/signup/identifier?state=hKFo2SBOVnZMbDF4T1hLeFZIcTluZ1hKbmZOZENvVDgydHduRaFur3VuaXZlcnNhbC1sb2dpbqN0aWTZIER2ZVJvUzdhN2Q4MVBpUTdZelg0cGlBcnNyQm5oUG5zo2NpZNkgRFJpdnNubTJNdTQyVDNLT3BxZHR3QjNOWXZpSFl6d0Q)的账号，然后打开：[https://chat.openai.com/api/auth/session](https://chat.openai.com/api/auth/session)
                这个网站，把accessToken记录下来。
                                
                然后打开IDE，再接着，依次打开 <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>ChatGPT，</kbd>
                然后把上面记录的accessToken填写到文本框。之后就可以使用啦。
                
                注意：这并不是一个完美的工具，可能会有错误。请斟酌使用。
                                
                                
                ## Usage
                First, you need to register a [OpenAI](https://auth0.openai.com/u/signup/identifier?state=hKFo2SBOVnZMbDF4T1hLeFZIcTluZ1hKbmZOZENvVDgydHduRaFur3VuaXZlcnNhbC1sb2dpbqN0aWTZIER2ZVJvUzdhN2Q4MVBpUTdZelg0cGlBcnNyQm5oUG5zo2NpZNkgRFJpdnNubTJNdTQyVDNLT3BxZHR3QjNOWXZpSFl6d0Q) account,
                and then open this website: [https://chat.openai.com/api/auth/session](https://chat.openai.com/api/auth/session)
                and records the response accessToken.
                                
                Then open the IDE, and then, open in turn <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>ChatGPT,</kbd>
                then fill in the accessToken recorded above into the text box. You can use it later.

                NOTE: This is not a perfect tool and may have bugs. Please use with discretion.
                """);
        String s = HtmlUtil.md2html(usage);
        contentPanel.setHtml(s,0);

        myToolWindowContent = new JPanel(new BorderLayout());
        myToolWindowContent.add(top,BorderLayout.NORTH);
        myToolWindowContent.add(contentPanel.getComponent(),BorderLayout.CENTER);
    }

    public SearchTextArea getSearchTextArea() {
        return searchTextArea;
    }

    public MarkdownJCEFHtmlPanel getContentPanel() {
        return contentPanel;
    }

    public JPanel init() {
        return myToolWindowContent;
    }

    public JButton getButton() {
        return button;
    }
}
