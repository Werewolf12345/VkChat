package ru.ssnd.demo.vkchat.controller;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import ru.ssnd.demo.vkchat.http.Response;

@Controller
@RequestMapping(value = "/api/chat")
public class ChatController {

    @RequestMapping(value = "{interlocutorId}/poll", method = RequestMethod.GET)
    public DeferredResult<Response> poll(@PathVariable Long interlocutorId) {

        DeferredResult<Response> result = new DeferredResult<>();

        // This is debug code
        //TODO Wait for a new message from ChatService, then set, not just thread with sleep
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result.setResult(new Response());
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