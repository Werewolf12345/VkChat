package ru.ssnd.demo.vkchat.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import ru.ssnd.demo.vkchat.entity.Message;
import ru.ssnd.demo.vkchat.http.Response;
import ru.ssnd.demo.vkchat.service.ChatService;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
@RequestMapping(value = "/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @RequestMapping(value = "{interlocutorId}/poll", method = RequestMethod.GET)
    public DeferredResult<Response> poll(@PathVariable Long interlocutorId) {

        DeferredResult<Response> result = new DeferredResult<>();

        // This is debug code
        //TODO Wait for a new message from ChatService, then set, not just thread with sleep
        new Thread(() -> {
            Future<List<Message>> messagesListFuture = null;
            try {
                messagesListFuture = chatService.getMessage(interlocutorId);
            } catch (ClientException | ApiException e) {
                e.printStackTrace();
            }

            assert messagesListFuture != null;
            while (!messagesListFuture.isDone()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            List<Message> list = Collections.emptyList();
            try {
                list = messagesListFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .excludeFieldsWithoutExposeAnnotation()
                    .serializeNulls()
                    .create();

            Map<String, List<Message>> map = new TreeMap<>();
            map.put("messages", list);
            String jsonString = gson.toJson(map);

            result.setResult(new Response(jsonString, HttpStatus.OK));
        }).start();

        return result;
    }

    @RequestMapping(value = "{interlocutorId}/send", method = RequestMethod.POST)
    public Response send(@PathVariable Long interlocutorId, @RequestBody String  jsonString) throws ClientException,
            ApiException {

        //TODO Send with ChatService

        JSONObject jsonObject = new JSONObject(jsonString);
        String messageText = jsonObject.getString("messagetext");

        Message message = chatService.sendMessage(interlocutorId, messageText);

        return new Response.Builder()
                .withEntityField("message", message)
                .build();
    }
}