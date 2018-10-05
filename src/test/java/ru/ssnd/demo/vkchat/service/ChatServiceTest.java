package ru.ssnd.demo.vkchat.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.junit.Ignore;
import org.junit.Test;
import ru.ssnd.demo.vkchat.entity.Message;
import ru.ssnd.demo.vkchat.entity.Sender;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;


public class ChatServiceTest {

    @Ignore
    @Test
    public void getMessageTest() throws ClientException, ApiException, InterruptedException, ExecutionException {
        ChatService chatService = new ChatService(null);
        Future<Message> messageFuture = chatService.getMessage(509947643L);

        while (!messageFuture.isDone()){
            Thread.sleep(1000);
        }

        Message message = messageFuture.get();
        System.out.println(message != null ? message : "No messages yet, sorry...");
    }

    @Ignore
    @Test
    public void jsonArrayMessagesTest() {
        Message message1 = new Message();
        message1.setId(1L);
        Sender sender1 = new Sender();
        sender1.setId(1L);
        message1.setSender(sender1);
        message1.setSentAt(new Date(1538605724L * 1000L));
        message1.setText("Message text 1");

        Message message2 = new Message();
        message2.setId(2L);
        Sender sender2 = new Sender();
        sender2.setId(2L);
        message2.setSender(sender2);
        message2.setSentAt(new Date(1538605624L * 1000L));
        message2.setText("Message text 2");

        List<Message> list = new ArrayList<>();

        list.add(message1);
        list.add(message2);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .create();

        Map<String, List<Message>> map = new TreeMap<>();
        map.put("messages", list);
        String jsonString = gson.toJson(map);

        assertEquals("{\"messages\":[{\"id\":1,\"sender\":{\"id\":1,\"avatarUrl\":null,\"name\":null}," +
                "\"sentAt\":\"2018-10-03 17:28:44\",\"text\":\"Message text 1\"},{\"id\":2,\"sender\":{\"id\":2," +
                "\"avatarUrl\":null,\"name\":null},\"sentAt\":\"2018-10-03 17:27:04\",\"text\":\"Message text 2\"}]}",
                jsonString) ;
    }
}