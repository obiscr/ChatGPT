package com.obiscr.chatgpt;

import com.obiscr.OpenAIProxy;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.ui.MessageComponent;
import okhttp3.*;

import java.net.Proxy;

/**
 * @author Wuzi
 */
public abstract class AbstractHandler {

    public abstract  <T> T handle(MainPanel mainPanel, MessageComponent component, String question);

    public static Proxy getProxy() {
        Proxy proxy;
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        if (!instance.enableProxy) {
            return null;
        }
        switch (instance.proxyType) {
            case HTTP:
                proxy = new OpenAIProxy(instance.proxyHostname,
                        Integer.parseInt(instance.proxyPort),
                        Proxy.Type.HTTP).build();
                break;
            case SOCKS:
                proxy = new OpenAIProxy(instance.proxyHostname,
                        Integer.parseInt(instance.proxyPort),
                        Proxy.Type.SOCKS).build();
                break;
            case DIRECT:
                proxy = new OpenAIProxy(instance.proxyHostname,
                        Integer.parseInt(instance.proxyPort),
                        Proxy.Type.DIRECT).build();
                break;
            default:
                proxy = null;
                break;
        }
        return proxy;
    }

    protected Authenticator getProxyAuth() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        return (route, response) -> {
            String credential = Credentials.basic(state.proxyUsername, state.proxyPassword);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };
    }
}
