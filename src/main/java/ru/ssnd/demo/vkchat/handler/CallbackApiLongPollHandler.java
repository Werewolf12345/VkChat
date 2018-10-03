package ru.ssnd.demo.vkchat.handler;


import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.objects.messages.Message;

public class CallbackApiLongPollHandler extends CallbackApi {

    @Override
    public void messageNew(Integer groupId, Message message) {
        System.out.println(message.getBody());
    }
}
