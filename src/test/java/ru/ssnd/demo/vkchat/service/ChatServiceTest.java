package ru.ssnd.demo.vkchat.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.junit.Ignore;
import org.junit.Test;
import ru.ssnd.demo.vkchat.entity.Message;

import java.io.IOException;
import java.util.concurrent.Future;


public class ChatServiceTest {

    @Ignore
    @Test
    public void getMessageTest() throws ClientException, ApiException, IOException, InterruptedException {
        ChatService chatService = new ChatService(null);
        Future<Message> messageFuture = chatService.getMessage(509947643L);
        while (!messageFuture.isDone()){
            Thread.sleep(1000);
            System.out.println("Sleep...");
        }
    }
}