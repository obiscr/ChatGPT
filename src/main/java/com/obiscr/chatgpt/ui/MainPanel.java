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
        String usage = "## 使用方法\n首先，您需要注册一个[OpenAI](https://auth0.openai.com/u/signup/identifier?state=hKFo2SBOVnZMbDF4T1hLeFZIcTluZ1hKbmZOZENvVDgydHduRaFur3VuaXZlcnNhbC1sb2dpbqN0aWTZIER2ZVJvUzdhN2Q4MVBpUTdZelg0cGlBcnNyQm5oUG5zo2NpZNkgRFJpdnNubTJNdTQyVDNLT3BxZHR3QjNOWXZpSFl6d0Q)的账号，然后打开：[https://chat.openai.com/api/auth/session](https://chat.openai.com/api/auth/session)\n" +
                "这个网站，把accessToken记录下来。\n" +
                "\t\t\t\t\n" +
                "然后打开IDE，点击ChatGPT工具窗口的 ![](https://intellij-icons.jetbrains.design/icons/AllIcons/general/settings.svg) 按钮（或者手动打开： <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>ChatGPT，</kbd>）\n" +
                "然后把上面记录的accessToken填写到文本框。之后就可以使用啦。\n" +
                "\n" +
                "注意：这并不是一个完美的工具，可能会有错误。请斟酌使用。\n" +
                "\n" +
                "[主页](https://plugins.jetbrains.com/plugin/20603-chatgpt) |\n" +
                "[文档/预览](https://docs.obiscr.com/article/GPT) |\n" +
                "[Github](https://github.com/obiscr/ChatGPT) |\n" +
                "[Q&A](https://docs.obiscr.com/article/GPT-QA) |\n" +
                "[微信群](https://www.obiscr.com/article/Wechat-group-is-now-open)\n" +
                "\t  \n" +
                "\t\t\t\t\n" +
                "## Usage\n" +
                "First, you need to register a [OpenAI](https://auth0.openai.com/u/signup/identifier?state=hKFo2SBOVnZMbDF4T1hLeFZIcTluZ1hKbmZOZENvVDgydHduRaFur3VuaXZlcnNhbC1sb2dpbqN0aWTZIER2ZVJvUzdhN2Q4MVBpUTdZelg0cGlBcnNyQm5oUG5zo2NpZNkgRFJpdnNubTJNdTQyVDNLT3BxZHR3QjNOWXZpSFl6d0Q) account,\n" +
                "and then open this website: [https://chat.openai.com/api/auth/session](https://chat.openai.com/api/auth/session)\n" +
                "and records the response accessToken.\n" +
                "\t\t\t\t\n" +
                "Then open the IDE, and then, click the ![](https://intellij-icons.jetbrains.design/icons/AllIcons/general/settings.svg) of the Chat GPT tool window. (Or or open manually: <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>ChatGPT</kbd>)\n" +
                "then fill in the accessToken recorded above into the text box. You can use it later.\n" +
                "\n" +
                "NOTE: This is not a perfect tool and may have bugs. Please use with discretion.\n" +
                "\n" +
                "[Homepage](https://plugins.jetbrains.com/plugin/20603-chatgpt) |\n" +
                "[Docs/Preview](https://docs.obiscr.com/article/GPT) |\n" +
                "[Github](https://github.com/obiscr/ChatGPT) |\n" +
                "[Q&A](https://docs.obiscr.com/article/GPT-QA) |\n" +
                "[Slack](https://join.slack.com/t/observercreator/shared_invite/zt-14g3dnzkx-FGJM_WgY~vj0bJINTHQSAA)";
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
