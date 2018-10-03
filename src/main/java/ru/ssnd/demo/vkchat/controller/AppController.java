package ru.ssnd.demo.vkchat.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.ssnd.demo.vkchat.service.ChatService;

@Controller
public class AppController {

    private final ChatService chat;

    @Autowired
    public AppController(ChatService chat) {
        this.chat = chat;
    }

    @RequestMapping(value = "/")
    public ModelAndView app() {
        ModelAndView mav = new ModelAndView("app.ftl");
        mav.addObject("appData", new JSONObject()
            .put("selfId", chat.getCommunityId())
        );
        return mav;
    }

}