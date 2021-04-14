package com.practice.restapi_todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CheckController {
    @PostMapping("/check")
    public void check(HttpServletResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<String, String>();

        try {
            map.put("name", "apple");
            map.put("price", "10000");

            String jsonStr = mapper.writeValueAsString(map);

            response.setStatus(404);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(jsonStr);
        } catch (IOException e) {
            response.setStatus(404);
        }
    }
}
