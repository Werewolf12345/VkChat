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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@Service
public class ChatService {
    private final MessagesRepository messagesRepository;
    private final VkApiClient vk;
    private final GroupActor actor;

    private static final String COMMUNITY_ACCESS_TOKEN =
            "4d6de12562bb8e274c51fcc3120dadbf13574f9b40189891d0e65cd062f89a81097df561daa1ff5b3bef3";
    private static final Integer COMMUNITY_ID = 172078261;

    @Autowired
    public ChatService(MessagesRepository messages) {
        this.messagesRepository = messages;
        this.vk = new VkApiClient(new HttpTransportClient());
        this.actor = new GroupActor(COMMUNITY_ID, COMMUNITY_ACCESS_TOKEN);
        // TODO Community vk auth
    }

    public Integer getCommunityId() {
        // TODO Get community id
        return COMMUNITY_ID;
    }

    public Message sendMessage(Long interlocutorId, String message) {
        Integer messageId;
        try {
            messageId = vk.messages()
                    .send(actor)
                    .message(message)
                    .userId(Math.toIntExact(interlocutorId))
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            return new Message();
        }

        Sender sender = new Sender();
        sender.setId(interlocutorId);
        sender.setAvatarUrl("https://vgy.me/8zipf9.png");
        sender.setName("Я грустный кот");

        Message messageBeenSent = new Message();
        messageBeenSent.setText(message);
        messageBeenSent.setSender(sender);
        messageBeenSent.setId(messageId.longValue());
        messageBeenSent.setSentAt(new Date());
        messageBeenSent.setSent(true);

        messagesRepository.save(messageBeenSent);

        return messageBeenSent;
    }

    public Future<List<Message>> getMessage(Long interlocutorId) {
        CompletableFuture<List<Message>> promise
                = new CompletableFuture<>();

        LongpollParams longpollParams;
        try {
            longpollParams = vk.messages()
                               .getLongPollServer(actor)
                               .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            promise.complete(Collections.emptyList());
            return promise;
        }

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
                        JSONObject mainObj = new JSONObject(response.getResponseBody());
                        JSONArray jsonArray = (JSONArray) mainObj.get("updates");

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        Object[][] updates = gson.fromJson(jsonArray.toString(), Object[][].class);

                        List<Message> messagesList = new ArrayList<>();

                        for (Object[] updatesArray : updates) {
                            if ((Double) updatesArray[0] == 4.0
                                    && new Double((double) updatesArray[3]).longValue() == interlocutorId) {
                                Message message = new Message();
                                message.setId(new Double((double) updatesArray[1]).longValue());
                                Sender sender = new Sender();
                                sender.setId(new Double((double) updatesArray[3]).longValue());
                                sender.setAvatarUrl("https://vk.com/images/camera_50.png?ava=1");
                                sender.setName("John Connor");
                                message.setSender(sender);
                                message.setSentAt(new Date(new Double((double) updatesArray[4]).longValue() * 1000L));
                                message.setText((String) updatesArray[6]);
                                message.setSent(true);

                                messagesRepository.save(message);

                                messagesList.add(message);
                            }
                        }

                        promise.complete(messagesList);
                        return response;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        promise.complete(Collections.emptyList());
                    }
                });

        return promise;
    }

    // TODO Get, send & store messagesRepository
}
