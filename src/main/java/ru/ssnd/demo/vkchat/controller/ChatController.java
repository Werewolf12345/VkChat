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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import ru.ssnd.demo.vkchat.entity.Message;
import ru.ssnd.demo.vkchat.http.Response;
import ru.ssnd.demo.vkchat.service.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
            Future<Message> messageFuture = null;
            try {
                messageFuture = chatService.getMessage(interlocutorId);
            } catch (ClientException | ApiException e) {
                e.printStackTrace();
            }

            assert messageFuture != null;
            while (!messageFuture.isDone()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Message message = null;
            try {
                message = messageFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println(message != null ? message : "No messages yet, sorry...");

            List<Message> list = new ArrayList<>();
            list.add(message);

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
    public Response send(@PathVariable Long interlocutorId) {

        //TODO Send with ChatService

        return new Response.Builder()
                .withField("message", new JSONObject())
                .build();
    }

}