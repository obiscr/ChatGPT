package com.obiscr.chatgpt.ui;

import com.intellij.find.SearchTextArea;
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.core.Constant;
import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.core.parser.CloudflareParser;
import com.obiscr.chatgpt.core.parser.CustomizeParser;
import com.obiscr.chatgpt.core.parser.DefaultParser;
import com.obiscr.chatgpt.core.parser.OfficialParser;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.SettingConfiguration;
import com.obiscr.chatgpt.settings.SettingsState;
import com.obiscr.chatgpt.ui.listener.SendListener;
import com.obiscr.chatgpt.util.HtmlUtil;
import org.intellij.plugins.markdown.ui.preview.jcef.MarkdownJCEFHtmlPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Wuzi
 */
public class MainPanel {

    private final SearchTextArea searchTextArea;
    private final JButton button;
    private final MarkdownJCEFHtmlPanel contentPanel;
    private final JProgressBar progressBar;
    private final OnePixelSplitter splitter;

    public MainPanel() {
        SendListener listener = new SendListener(this);

        splitter = new OnePixelSplitter(true,.9f);
        splitter.setDividerWidth(2);

        searchTextArea = new SearchTextArea(new JBTextArea(),true);
        searchTextArea.getTextArea().addKeyListener(listener);
        searchTextArea.setPreferredSize(new Dimension(searchTextArea.getWidth(),150));

        button = new JButton(ChatGPTBundle.message("ui.toolwindow.send"), IconLoader.getIcon("/icons/send.svg",MainPanel.class));
        button.addActionListener(listener);
        button.setUI(new DarculaButtonUI());

        JPanel top = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        top.add(progressBar, BorderLayout.NORTH);
        top.add(searchTextArea, BorderLayout.CENTER);
        top.add(button, BorderLayout.EAST);
        top.setBorder(UIUtil.getTextFieldBorder());
        contentPanel = new MarkdownJCEFHtmlPanel();

        String s = HtmlUtil.md2html(Constant.HOME_CONTENT);
        contentPanel.setHtml(s,0);

        splitter.setFirstComponent(contentPanel.getComponent());
        splitter.setSecondComponent(top);
    }

    public SearchTextArea getSearchTextArea() {
        return searchTextArea;
    }

    public MarkdownJCEFHtmlPanel getContentPanel() {
        return contentPanel;
    }

    public JPanel init() {
        return splitter;
    }

    public JButton getButton() {
        return button;
    }

    public void aroundRequest(boolean status) {
        progressBar.setIndeterminate(status);
        button.setEnabled(!status);
    }

    public void showContent() {
        DataFactory dataFactory = DataFactory.getInstance();
        String html = HtmlUtil.md2html(dataFactory.buildConversations());
        contentPanel.setHtml(html, html.length());
        contentPanel.scrollBy(0,html.length());
    }
}
