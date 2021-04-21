package com.practice.restapi_todolist.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.restapi_todolist.domain.Follow;
import com.practice.restapi_todolist.exception.CustomBadRequestException;
import com.practice.restapi_todolist.exception.CustomException;
import com.practice.restapi_todolist.exception.CustomTooManyRequestsException;
import com.practice.restapi_todolist.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.*;

@Controller
public class FollowController {

    private FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow")
    public ResponseEntity<Follow> create(@RequestParam String toNick) throws CustomException {
        // follow 필수 항목 확인
        if(toNick.isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, null, "toNick이 잘못된 값입니다.");

        // follow 생성 요청
        Follow createdFollow = Optional.ofNullable(followService.createFollow(toNick))
                                                 .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, Map.of("toNick", toNick), "주어진 파라미터로 팔로우 생성이 불가합니다."));

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(createdFollow, headers, HttpStatus.OK);
    }

    @PutMapping("/follow/{toNick}")
    public ResponseEntity<Follow> update(@PathVariable("toNick") String toNick, @RequestBody String status) throws CustomException {
        // nick null 체크
        if(toNick.isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, null, "toNick이 잘못된 값입니다.");
        if(status.isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, null, "statu가 잘못된 값입니다.");

        // update 요청
        Follow updatedFollow = Optional.ofNullable(followService.updateFollow(toNick, status))
                .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, Map.of("toNick", toNick, "status", status), "주어진 파라미터로 팔로우 상태 변경이 불가합니다."));

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(updatedFollow, headers, HttpStatus.OK);
    }

    @DeleteMapping("/follow/{toNick}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("toNick") String toNick) throws CustomException {
        // nick null 체크
        if(toNick.isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, null, "toNick이 잘못된 값입니다.");

        if(!followService.deleteFollow(toNick))
                throw new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, Map.of("toNick", toNick), "주어진 파라미터로 팔로우 삭제가 불가합니다.");

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(Map.of("toNick", toNick), headers, HttpStatus.OK);
    }

    @GetMapping("/follow/{toNick}")
    public ResponseEntity<Follow> findFollows_rp(@PathVariable("toNick") String toNick) throws CustomException {
        // nick null 체크
        if(toNick.isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, null, "fromNick가 잘못된 값입니다.");

        // read 요청
        Follow follow = Optional.ofNullable(followService.findFollowByUserNick(toNick))
                                        .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, Map.of("toNick", toNick), "주어진 파라미터로 팔로우 조회가 불가합니다."));

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(follow, headers, HttpStatus.OK);
    }

    @GetMapping("/follow")
    public ResponseEntity<List<Follow>> findFollows_pv(@RequestParam(value = "to", required = false, defaultValue = "") String toNick) throws CustomException {
        // nick null 체크

        // read 요청
        List<Follow> follows = new ArrayList<>();
        if(toNick.isEmpty()) {
            follows = Optional.ofNullable(followService.findAllFollows())
                    .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, Map.of("toNick", toNick), "주어진 파라미터로 팔로우 조회에 실패했습니다."));
        } else {
            Follow follow = Optional.ofNullable(followService.findFollowByUserNick(toNick))
                    .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, Map.of("toNick", toNick), "주어진 파라미터로 팔로우 조회에 실패했습니다."));

            follows.add(follow);
        }

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(follows, headers, HttpStatus.OK);
    }







    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> CustomExceptionHandler(CustomException e) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> mapParam = e.getParam();
        String strParam = mapper.writeValueAsString(mapParam);

        Map<String, String> mapBody = new HashMap<>();
        mapBody.put("param", strParam);
        mapBody.put("message", e.getDescription());
        mapBody.put("HttpState", e.getHttpStatus().toString());

        return new ResponseEntity<>(mapBody, headers, e.getHttpStatus());
    }
}
