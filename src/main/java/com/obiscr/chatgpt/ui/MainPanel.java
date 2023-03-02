package com.obiscr.chatgpt.ui;

import com.intellij.find.SearchTextArea;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBTextArea;
import com.obiscr.chatgpt.MyToolWindowFactory;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.listener.SendListener;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Wuzi
 */
public class MainPanel {

    private final SearchTextArea searchTextArea;
    private final JButton button;
    private final JButton stopGenerating;
    private final MessageGroupComponent contentPanel;
    private final JProgressBar progressBar;
    private final OnePixelSplitter splitter;
    private final Project myProject;
    private JPanel actionPanel;
    private ExecutorService executorService;
    private Object requestHolder;

    private boolean myIsChatGPTModel;

    public MainPanel(@NotNull Project project, boolean isChatGPTModel) {
        myIsChatGPTModel = isChatGPTModel;
        myProject = project;
        SendListener listener = new SendListener(this);

        splitter = new OnePixelSplitter(true,.9f);
        splitter.setDividerWidth(2);

        searchTextArea = new SearchTextArea(new JBTextArea(),true);
        searchTextArea.getTextArea().addKeyListener(listener);
        searchTextArea.setPreferredSize(new Dimension(searchTextArea.getWidth(),50));

        button = new JButton(ChatGPTBundle.message("ui.toolwindow.send"), IconLoader.getIcon("/icons/send.svg",MainPanel.class));
        button.addActionListener(listener);
        button.setUI(new DarculaButtonUI());

        stopGenerating = new JButton("Stop", AllIcons.Actions.Suspend);
        stopGenerating.addActionListener(e -> {
            executorService.shutdownNow();
            aroundRequest(false);
            if (requestHolder instanceof EventSource) {
                ((EventSource)requestHolder).cancel();
            } else if (requestHolder instanceof MessageComponent) {
                ((MessageComponent) requestHolder).setContent("Stop generating");
                ((MessageComponent) requestHolder).setStopping(true);
            }
        });
        stopGenerating.setUI(new DarculaButtonUI());

        actionPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        actionPanel.add(progressBar, BorderLayout.NORTH);
        actionPanel.add(searchTextArea, BorderLayout.CENTER);
        actionPanel.add(button, BorderLayout.EAST);
        contentPanel = new MessageGroupComponent(project, isChatGPTModel());

        splitter.setFirstComponent(contentPanel);
        splitter.setSecondComponent(actionPanel);
    }

    public Project getProject() {
        return myProject;
    }

    public SearchTextArea getSearchTextArea() {
        return searchTextArea;
    }

    public MessageGroupComponent getContentPanel() {
        return contentPanel;
    }

    public JPanel init() {
        return splitter;
    }

    public JButton getButton() {
        return button;
    }

    public ExecutorService getExecutorService() {
        executorService = Executors.newFixedThreadPool(1);
        return executorService;
    }

    public void aroundRequest(boolean status) {
        progressBar.setIndeterminate(status);
        button.setEnabled(!status);
        if (status) {
            contentPanel.addScrollListener();
            actionPanel.remove(button);
            actionPanel.add(stopGenerating,BorderLayout.EAST);
        } else {
            contentPanel.removeScrollListener();
            actionPanel.remove(stopGenerating);
            actionPanel.add(button,BorderLayout.EAST);
        }
        actionPanel.updateUI();
    }

    public void setRequestHolder(Object eventSource) {
        System.out.println(eventSource);
        this.requestHolder = eventSource;
    }

    public boolean isChatGPTModel() {
        return myIsChatGPTModel;
    }
}
