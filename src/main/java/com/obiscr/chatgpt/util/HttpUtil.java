package com.obiscr.chatgpt.util;

import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.core.SseParams;
import com.obiscr.chatgpt.core.parser.*;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.SettingConfiguration;
import com.obiscr.chatgpt.settings.SettingsState;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.ui.notifier.MyNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Wuzi
 */
public class HttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);
    private static final String LOGIN_URL = "https://chat.openai.com/api/auth/session";

    public static final String DEFAULT_CONVERSATION_URL = "https://gpt.chatapi.art/backend-api/conversation";
    public static final String OFFICIAL_CONVERSATION_URL = "https://chat.openai.com/backend-api/conversation";

    public static final String EMPTY_RESPONSE = "# Ops\n" +
            "It looks like something went wrong, no data was response.";

    public static String get(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        conn.setConnectTimeout(5000);
        // Set Headers
        conn.setRequestProperty("Accept", "*/*");
        //conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..5zeIMygyzPUBjBZD.04dbcK7NhdTUPr9VM1kY0euc4YXp0eOAbqIivE5C7h3YJ9AErGPJavtD_3OWmugtGRebFOj45qNTH07LW9CnsJAWNiaCjZTvLCGjemRLqel5W-MuRN2E2jft2LuGdCjoWOFOiG6B2HunNOdi7MU2XumM7obiA9n1l7Wjs8nknCcgxDtVZm7xnJhZeRpQ2wQkD2bqOI_Vw-MezcD_dk_SI89aAzEHFYp25uKx_4G0ejLO-Aa9wsrcML2bmTtslqepHzf0xd01mko2GDWlRX70c6Z2_K0D-PPM8fx9XNwEZPeqN1JVAQ6Qt__bmt7rwxtZv4DKciAVvwR_7IineSjwSWB1bodNbTQd6rf2KgsZRTP3Xis0cIGNJ3BO_iXBJAwlYz34YehPgKqEFonT-Zq3fSuJkcApWMob8RpDmkGRWiyVUq95TgF1g5cL4n0mLJb6zcJtaZ_rqNXdqVu4ha8icndRvZelZznQW7KoaJusCTTGwZQMXCmDVoG1NPRW13xG6tsIbKImXnxDVlPqPmxcAXhPHKbRmSty3pWoq5tbCgemhaXiG_ujJhazWGWWgxv1tCNgZi0O8JP0LmDZ5bWdTTdtKMJwEZ0-zCFuNFmhOVORI7DlflIQEusNXj20FKp_sjd1EgzWhWerXg5tP2qHn9g4qXmhxg7ZEWstgKdMhs-1uBpN8PuNhBxoorQDzIC_olcLXm0PLAig6IgV4NEpmKfBkruUbMeuqeA3zeDaEv5l78Km79U-73t2hfLKUr6w1cPldSBg43jtvIv8M-kb6zC7vt2NfDLKWK8hcSujHyUWo7LpvmWtxmK0h1bbqAagKn7Mi1i9CKUVbSufrUCDNRGuwqLhZRzrEgLB3u4X83OWWwfeQHGWjzKJc_P0XK5w7th0XhIHIA4JCymRrmpRFMfCnnvg_CIbk1ZBdz3xMny2UaJJSrHDUGiKWSUrquSPxT8i30PyXjqWT1TSsNezLKSfZyDtufbaStVlOXAC4F9VnMtC9gvTK7RzzcBh--wvSV3InzPrCiOa-HMByQXeDGMiMU4XA0pNpC1wm7ovPjnWFclcXnIrK3SyTREtNJShshdJOrqW9PcpgDUU6cpUvI0acGrPmKrnziOMpOQmUaz060PqvWUEyUdDur5J0OR7mrO3J2GXrHjfJT6215nqnZxGEQ04Jcr8KG9fcmx_UrkLl-QLNoz4_CTfimCri_jvmrXyp3NFW8AHL1oAZqs2sh31w_G56CUNowNuPDfgH_51D3UDpEVtdxRGn4wq6U-J5vpfLBN36puGAB46qeyDUkn2FJZfKkXgqi0O67Vn41gr9AfxYf3CjViBDIVQ6tVZBpGYMX6PhPjIuTQxYVM1Cm0flA9bzyn36f3WtB_8Kup-nNR4RNteD8j-1HrApbFIpzKwR9ZUeMo_MpexukSZnYFfj-_VT-XALHdRGreEW5I0uJHGG1gKnvvUQUpBKSDE6_0E-VQ7UAEOekR9SEOtXSxRzqjWfLLEnu6B1YK9Fo3RR8exmJRP08UZ_VZeNsSnGeaVFfi1_jkrC5YzOr6vfmdDrJE7G5sMgqiNqPoY9wXfqjfjyIds0W9dDZndgPClxvsynmdCRAfpJA6cK5Iim6XZmDpvDS1Azx1UoQutNLGPl7jEqPxno_bKksyWcZgiZadpgvrjUJEwybhOVUSRL-dgO9Mr-aSbRnB1a0L2ULejATRl0hAsG7GlB0kiu9fkN26764STCPL3fOi5GE3UkMaSjorWuh7Mi27NrWsvLCBDnQXQs7wuEGJ9hmtnGhFuoNbnuWQc6SCQJ9CjvKL4wabcoWD5OgMsY1x6xL42Gq8BnNFhDJuVQVo6mDf5C0eMI-YyQ63ZfYF7r2bnKB2ZE6khZo40MrNK9IW2i5Oj_2j27dCyx6gXV2SqUtogET2HsMr490Q8n3VlWhBBc9GuLMG6B6NBa1WRr1eDKteyYI1Id2UPvg6IpLbOzXv3bbch-QiNehgI8bTzWag9ebrGv0es90Ts6UiOnUmF95YTssi9hgo5VGWW2q2kbchIkwgGHzvz0n3ZZ8mM65B1Rezp_rwsD72EBD21yp-PyFXKv4yrfl5Y3dYXWj58-JVq1zzZGwOt4quKC0qNuNcb9jxnWqfg6t5Nk5dYpmr_lWZAC8ZPU4KKcz0-gT59wkmB-GJQl1muoho-5WWP23MGptb_U5AoSelGn3TOWGJsZafiIHH2Ql1BjS0atJuGmN-BMznbfvP2NQQtsqwlPZ_M6PXARGPcIr5UFkjPLp2-oJi_k4v4h8A1zeAyGdyrjEhU3F1bxk-c-UJNJjHk0B-gEzvxRZrd._LpE8ipK0sCJM_CNYLCAfw");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
        conn.setRequestProperty("Cookie", "__Host-next-auth.csrf-token=5cdddd7eb053d7cea96e7cb093167274ae80399f2480328081afefc5e437177e%7C4a8b652bb6c70b33332c390a5bd83d23378608a291a72452016ed8e78371864b");

        // 连接并发送HTTP请求:
        conn.connect();

        // 判断HTTP响应是否200:
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Bad response");
        }
        // 获取响应内容:
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String current;
        while((current = reader.readLine()) != null)
        {
            sb.append(current);
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Get the answer
     * @param params Question String
     * @param panel Content Component
     * @throws Exception /
     */
    public static void sse(SseParams params, MainPanel panel) throws Exception {

        Stack<String> stack =new Stack<>();

        // Create Pool
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        URL url = new URL(params.getUrl());

        SettingsState state = SettingsState.getInstance();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(Integer.parseInt(state.connectionTimeout));
        connection.setReadTimeout(Integer.parseInt(state.readTimeout));
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.setRequestProperty("Accept", "text/event-stream");
        connection.setRequestProperty("Content-Type", "application/json");
        if (StringUtil.isNotEmpty(params.getAccessToken())) {
            connection.setRequestProperty("Authorization", "Bearer " + params.getAccessToken());
        }
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");

        LOG.info("ChatGPT Request: url={}, data:{}",params.getUrl(),params.getData().toJSONString());

        // Write data
        connection.getOutputStream().write(params.getData().toJSONString().getBytes());

        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            LOG.error("ChatGPT Response error, responseCode={}", responseCode);
            if (responseCode == 401) {
                MyNotifier.notifyErrorWithAction(DataFactory.getInstance().getProject(),
                        ChatGPTBundle.message("notify.response.title"),
                        ChatGPTBundle.message("notify.response.text"));
                panel.aroundRequest(false);
                return;
            } else if (responseCode == 429) {
                MyNotifier.notifyError(DataFactory.getInstance().getProject(),
                        ChatGPTBundle.message("notify.too_many_request.error.title"),
                        ChatGPTBundle.message("notify.too_many_request.error.text"));
                panel.aroundRequest(false);
                return;
            }
            throw new Exception("Failed to connect to SSE server");
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );
        executorService.submit(() -> {
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    if ("data: [DONE]".equals(line)) {
                        String last = stack.peek();
                        LOG.info("Last content: {}", last);
                        stack.clear();
                        break;
                    } else if (!line.isEmpty()) {
                        stack.push(line);


                        // Pre-check the response data, format is: data: {}
                        if (line.length() <= 6){
                            line = HtmlUtil.create(line);
                        }
                        line = line.substring(5);
                        if ("{}".equals(line.trim())) {
                            line = EMPTY_RESPONSE;
                        }

                        // Dispatch to parse
                        line = dispatchParse(line);
                        if (line == null) {
                            line = EMPTY_RESPONSE;
                        }

                        String html = HtmlUtil.md2html(line);
                        panel.getContentPanel().setHtml(html, 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("ChatGPT Request exception: " +
                        "url:{}, params:{}, data:{}, errorMsg{}:", params.getUrl(),
                        line,params.getData(), e.getMessage());
            } finally {
                connection.disconnect();
                panel.aroundRequest(false);
            }
        });

        executorService.shutdown();
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    private static String dispatchParse(String line) {
        SettingConfiguration.SettingURLType urlType = SettingsState.getInstance().urlType;
        switch (urlType) {
            case DEFAULT:
                line =  new DefaultParser().parse(line);
                break;
            case OFFICIAL:
                line =  new OfficialParser().parse(line);
                break;
            case CUSTOMIZE:
                line =  new CustomizeParser().parse(line);
                break;
            case CLOUDFLARE:
                line =  new CloudflareParser().parse(line);
                break;
            default:break;
        }
        return line;
    }
}
