package com.practice.restapi_todolist.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.restapi_todolist.domain.Item;
import com.practice.restapi_todolist.exception.CustomBadRequestException;
import com.practice.restapi_todolist.exception.CustomException;
import com.practice.restapi_todolist.exception.CustomTooManyRequestsException;
import com.practice.restapi_todolist.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;

@RestController
public class ItemController {

    private ItemService itemService;
    private ObjectMapper mapper = new ObjectMapper();


    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/item")
    public ResponseEntity<List<Item>> create(@RequestBody Item item) throws CustomException, ParseException {
        // request 유효성 체크
        if(item.getTitle().isEmpty() || item.getStatus().isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST,
                    Map.of("title", item.getTitle().isEmpty() ? "null" : item.getTitle(),
                            "status", item.getStatus().isEmpty() ? "null" : item.getStatus(),
                            "dueDate", item.getDueDate() == null ? (new Date()).toString() : item.getDueDate().toString()
                            ),
                    "입력한 파라미터가 올바르지 않습니다.");

        // item 생성 요청
        List<Item> items = Optional.ofNullable(itemService.createItem(item))
                                    .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, mapper.convertValue(item, Map.class), "주어진 파라미터로 아이템 생성이 불가합니다."));

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(items, headers, HttpStatus.OK);
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<Item> update(@PathVariable("id") long id, @RequestBody Item item) throws CustomException, ParseException {
        // request 유효성 체크
        if(id <= 0)
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("id", Long.toString(id)), "입력한 id가 잘못된 값입니다.");

        // item 수정 요청
        Item updatedItem = itemService.updateItem(id, item);

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(updatedItem, headers, HttpStatus.OK);
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("id") long id) throws CustomException {
        // request 유효성 체크
        if(id <= 0)
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("id", Long.toString(id)), "입력한 id가 잘못된 값입니다.");

        // item 삭제 요청
        if(!itemService.deleteItem(id))
            throw new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS,Map.of("id", Long.toString(id)), "아이템 삭제에 실패하였습니다.");

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(Map.of("id", Long.toString(id)), headers, HttpStatus.OK);
    }
//
//    @GetMapping("/item/{id}")
//    public ResponseEntity<Item> find_pv_id(@PathVariable("id") long id) throws CustomException {
//        // request 유효성 체크
//        if(id <= 0)
//            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("id", Long.toString(id)), "입력한 id가 잘못된 값입니다.");
//
//        // item 조회 요청
//        Item item = Optional.ofNullable(itemService.findItemById(id))
//                            .orElseThrow(() -> new CustomNotFoundException(HttpStatus.NOT_FOUND,Map.of("id", Long.toString(id)), "입력한 id를 가진 멤버를 찾을 수 없습니다."));
//
//        // response return
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//        return new ResponseEntity<>(item, headers, HttpStatus.OK);
//    }

    @GetMapping("/item/{usernick}")
    public ResponseEntity<List<Item>> find_pv_usernick(@PathVariable("usernick") String usernick) throws CustomException {
        // request 유효성 체크
        if(usernick.isEmpty() || usernick == null)
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("usernick", usernick), "입력한 id가 잘못된 값입니다.");

        // item 조회 요청
        List<Item> items = Optional.ofNullable(itemService.findItemByUserNick(usernick))
                .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS,Map.of("usernick", usernick), "입력한 usernick를 가진 아이템을 찾을 수 없습니다."));

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(items, headers, HttpStatus.OK);
    }

    @GetMapping("/item")
    public ResponseEntity<List<Item>> find_rp(@RequestParam(value = "id", required = false, defaultValue = "-1") long id,
                                              @RequestParam(value = "usernick", required = false, defaultValue = "") String usernick) throws CustomException {
        // request 유효성 체크

        // item 조회 요청
        List<Item> items = new ArrayList<>();
        if (id > 0) {   // id로 검색
            Item item = Optional.ofNullable(itemService.findItemById(id))
                                .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS,Map.of("id", Long.toString(id)), "입력한 id를 가진 아이템을 찾을 수 없습니다."));

            items.add(item);
        }
        else if (id <= 0 && !usernick.isEmpty()) {     // nick으로 검색
            items = Optional.ofNullable(itemService.findItemByUserNick(usernick))
                            .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS,Map.of("usernick", usernick), "입력한 usernick를 가진 아이템을 찾을 수 없습니다."));
        }
        else if (id <= 0 && usernick.isEmpty()){   // 전체 검색
            items = Optional.ofNullable(itemService.findAllItems())
                            .orElseThrow(() -> new CustomTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, null, "아이템를 찾을 수 없습니다."));
        }

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(items, headers, HttpStatus.OK);
    }






    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> CustomExceptionHandler(CustomException e) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Map<String, String> mapParam = e.getParam();
        String strParam = mapper.writeValueAsString(mapParam);

        Map<String, String> mapBody = new HashMap<>();
        mapBody.put("param", strParam);
        mapBody.put("message", e.getDescription());
        mapBody.put("HttpState", e.getHttpStatus().toString());

        return new ResponseEntity<>(mapBody, headers, e.getHttpStatus());
    }
}
