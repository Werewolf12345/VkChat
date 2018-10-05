package ru.ssnd.demo.vkchat.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.LongpollParams;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ssnd.demo.vkchat.entity.Message;
import ru.ssnd.demo.vkchat.entity.Sender;
import ru.ssnd.demo.vkchat.repository.MessagesRepository;

import java.sql.Date;
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

    public Future<Message> getMessage(Long interlocutorId) throws ClientException, ApiException {
        CompletableFuture<Message> promise
                = new CompletableFuture<>();

        LongpollParams longpollParams = vk.messages()
                                          .getLongPollServer(actor)
                                          .execute();
        String key = longpollParams.getKey();
        Integer ts = longpollParams.getTs();
        String server = longpollParams.getServer();

        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        asyncHttpClient.prepareGet("https://" + server)
                .addQueryParam("act", "a_check")
                .addQueryParam("key", key)
                .addQueryParam("ts", ts != null ? ts.toString() : "0")
                .addQueryParam("wait", "25")
                .addQueryParam("mode", "2")
                .execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(Response response) {
                        Message message = null;

                        JSONObject mainObj = new JSONObject(response.getResponseBody());
                        JSONArray jsonArray = (JSONArray) mainObj.get("updates");

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        Object[][] updates = gson.fromJson(jsonArray.toString(), Object[][].class);

                        System.out.println("Got response: " + response);

                        for (Object[] updatesArray : updates) {
                            if ((Double) updatesArray[0] == 4.0
                                    && new Double((double) updatesArray[3]).longValue() == interlocutorId) {
                                message = new Message();
                                message.setId(new Double((double) updatesArray[1]).longValue());
                                Sender sender = new Sender();
                                sender.setId(new Double((double) updatesArray[3]).longValue());
                                sender.setAvatarUrl("https://vk.com/images/camera_50.png?ava=1");
                                sender.setName("John Connor");
                                message.setSender(sender);
                                message.setSentAt(new Date(new Double((double) updatesArray[4]).longValue() * 1000L));
                                message.setText((String) updatesArray[6]);
                            }
                        }

                        promise.complete(message);
                        return response;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        System.out.println("public void onThrowable(Throwable t): " + t.getMessage());
                        promise.completeExceptionally(t);
                    }
                });

        return promise;
    }

    // TODO Get, send & store messages
}
