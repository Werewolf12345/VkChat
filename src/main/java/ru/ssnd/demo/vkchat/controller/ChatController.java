package ru.ssnd.demo.vkchat.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(value = "/api/chat")
public class ChatController {
    private final ChatService chatService;

    private static final String EMPTY_MESSAGES_JSON = "{\"messages\": []}";
    private static final long LONGPOLL_TIMEOUT_MSECS = 25 * 1000L;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @RequestMapping(value = "{interlocutorId}/poll", method = RequestMethod.GET)
    public DeferredResult<Response> poll(@PathVariable Long interlocutorId) {

        //TODO Wait for a new message from ChatService, then set, not just thread with sleep

        final Response emptyResponse = new Response(EMPTY_MESSAGES_JSON, HttpStatus.OK);
        final DeferredResult<Response> deferredResult = new DeferredResult<>(LONGPOLL_TIMEOUT_MSECS, emptyResponse);

        CompletableFuture.supplyAsync(() -> chatService.getMessage(interlocutorId))
                .whenCompleteAsync((result, throwable) -> {

                    List<Message> list = Collections.emptyList();
                    try {
                        list = result.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        deferredResult.setResult(emptyResponse);
                    }

                    Map<String, List<Message>> map = new TreeMap<>();
                    map.put("messages", list);

                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .excludeFieldsWithoutExposeAnnotation()
                            .serializeNulls()
                            .create();
                    String jsonString = gson.toJson(map);

                    deferredResult.setResult(new Response(jsonString, HttpStatus.OK));
                });

        return deferredResult;
    }

    @RequestMapping(value = "{interlocutorId}/send", method = RequestMethod.POST)
    public Response send(@PathVariable Long interlocutorId, @RequestBody String  jsonString) {

        //TODO Send with ChatService

        JSONObject jsonObject = new JSONObject(jsonString);
        String messageText = jsonObject.getString("messagetext");

        Message message = chatService.sendMessage(interlocutorId, messageText);

        return new Response.Builder()
                .withEntityField("message", message)
                .build();
    }
}