package ru.ssnd.demo.vkchat.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.LongpollParams;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssnd.demo.vkchat.entity.Message;
import ru.ssnd.demo.vkchat.repository.MessagesRepository;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@Service
public class ChatService {
    private final MessagesRepository messages;
    private final VkApiClient vk;
    private final GroupActor actor;

    private static final String COMMUNITY_ACCESS_TOKEN =
            "4d6de12562bb8e274c51fcc3120dadbf13574f9b40189891d0e65cd062f89a81097df561daa1ff5b3bef3";
    private static final Integer COMMUNITY_ID = 172078261;

    @Autowired
    public ChatService(MessagesRepository messages) {
        this.messages = messages;
        this.vk = new VkApiClient(new HttpTransportClient());
        this.actor = new GroupActor(COMMUNITY_ID, COMMUNITY_ACCESS_TOKEN);
        // TODO Community vk auth
    }

    public Integer getCommunityId() {
        // TODO Get community id
        return COMMUNITY_ID;
    }

    public boolean sendMessage(String message) throws ClientException, ApiException {
        vk

                .messages().send(actor).message(message).userId(185014513).execute();

        return true;
    }

    public Future<Message> getMessage(Long interlocutorId) throws ClientException, ApiException, IOException {
        CompletableFuture<Message> completableFuture
                = new CompletableFuture<>();
        LongpollParams longpollParams = vk.messages().getLongPollServer(actor).execute();

        String key = longpollParams.getKey();
        Integer ts = longpollParams.getTs();
        String server = longpollParams.getServer();

        try (AsyncHttpClient asyncHttpClient = asyncHttpClient()) {
            asyncHttpClient
                    .prepareGet("https://" + server)
                    .addQueryParam("act", "a_check")
                    .addQueryParam("key", key)
                    .addQueryParam("ts", ts != null ? ts.toString() : "0")
                    .addQueryParam("wait", "25")
                    .addQueryParam("mode", "2")
                    .execute()
                    .toCompletableFuture()
                    .thenApply(Response::getResponseBody)
                    .thenAccept(responseBody -> {
                        System.out.println("Got response: " + responseBody);
                        JSONObject mainObj = new JSONObject(responseBody);
                        JSONArray jsonArray = (JSONArray) mainObj.get("updates");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            System.out.println(jsonArray.get(i).toString());
                        }

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();

                        Object[][] updates = gson.fromJson(jsonArray.toString(), Object[][].class);

                        for (Object[] updatesArray : updates) {
                                if((Double) updatesArray[0] == 4.0
                                        && new Double((double)updatesArray[3]).longValue() == interlocutorId) {
                                    System.out.println("New message from: " + new Double((double)updatesArray[3]).longValue() + "\nText: " + updatesArray[6]);
                                    completableFuture.complete(new Message());
                                }
                        }
                    });
        }
        return completableFuture;
    }

    // TODO Get, send & store messages
}
