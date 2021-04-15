package com.practice.restapi_todolist.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.restapi_todolist.domain.Member;
import com.practice.restapi_todolist.exception.CustomBadRequestException;
import com.practice.restapi_todolist.exception.CustomException;
import com.practice.restapi_todolist.exception.CustomNotFoundException;
import com.practice.restapi_todolist.service.MemberService;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.*;

@RestController
public class MemberController {

    private MemberService memberService;
    private ObjectMapper m = new ObjectMapper();

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/member")
    public ResponseEntity<Member> createMember(@RequestBody Member member) throws CustomException {
        // request 유효성 검사
        if(member.getNick().isEmpty() || member.getPw().isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST,
                                                Map.of("nick", member.getNick().isEmpty() ? member.getNick() : "null",
                                                        "pw", member.getPw().isEmpty() ? member.getPw() : "null"),
                                                "입력한 nick 혹은 pw가 null입니다.");

        // member 생성 요청
        Member createdMember = Optional.ofNullable(memberService.createMember(member))
                                    .orElseThrow(() -> new CustomBadRequestException(HttpStatus.BAD_REQUEST, m.convertValue(member, Map.class), "주어진 파라미터로 멤버 생성이 불가합니다."));

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(createdMember, headers, HttpStatus.OK);
    }

    @PutMapping("/member/{nick}")
    public ResponseEntity<Member> updateMember(@PathVariable("nick") String nick, @RequestBody Member member) throws CustomException {
        // request 유효성 검사
        if(nick == null || nick.isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("nick", nick == null ? "null" : ""), "입력한 nick이 잘못된 값입니다.");

        // member 수정 요청
        Member updatedMember = Optional.ofNullable(memberService.updateMember(nick, member))
                                        .orElseThrow(() -> new CustomNotFoundException(HttpStatus.NOT_FOUND,Map.of("nick", nick), "입력한 nick을 가진 멤버를 찾을 수 없습니다."));
        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(updatedMember, headers, HttpStatus.OK);
    }

    @DeleteMapping("/member/{nick}")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable("nick") String nick) throws CustomException {
        // request 유효성 검사
        if(nick == null || nick.isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("nick", nick == null ? "null" : ""), "입력한 nick이 잘못된 값입니다.");

        // member 삭제 요청
        if(!memberService.deleteMember(nick))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("nick", nick), "입력한 nick을 가진 멤버를 찾을 수 없습니다.");

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(Map.of("nick", nick), headers, HttpStatus.OK);
    }

    @GetMapping("/member/{nick}")
    public ResponseEntity<Member> findMember_pv(@PathVariable("nick") String nick) throws CustomException {
        // request 유효성 검사
        if(nick == null || nick.isEmpty())
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("nick", nick == null ? "null" : ""), "입력한 nick이 잘못된 값입니다.");

        // member 조회 요청
        Member member = Optional.ofNullable(memberService.findMember(nick))
                                .orElseThrow(() -> new CustomNotFoundException(HttpStatus.NOT_FOUND,Map.of("nick", nick), "입력한 nick을 가진 멤버를 찾을 수 없습니다."));

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(member, headers, HttpStatus.OK);
    }

    @GetMapping("/member")
    public ResponseEntity<List<Member>> findMember_rp(@RequestParam(value = "nick", required = false) String nick) throws CustomException {
        // request 유효성 검사

        // member 조회 요청
        List<Member> members = new ArrayList<>();
        if(nick == null) {
            List<Member> optList = memberService.findAllMembers();

            for(int i = 0; i < optList.size(); i++) {
                Optional<Member> optMember = Optional.ofNullable(optList.get((i)));
                if(optMember.isPresent())
                    members.add(optMember.get());
            }
        } else {
            Optional<Member> optMember = Optional.ofNullable(memberService.findMember(nick));

            if(optMember.isPresent())
                members.add(optMember.get());
        }

        if(members.isEmpty())
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND,
                                                (nick != null) ? Map.of("nick", nick) : null,
                                                "입력한 nick을 가진 멤버를 찾을 수 없습니다.");

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(members, headers, HttpStatus.OK);
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<Map<String, String>> NotFoundExceptionHandler(CustomNotFoundException e) throws JsonProcessingException {
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

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<Map<String, String>> BadRequestExceptionHandler(CustomBadRequestException e) throws JsonProcessingException {
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
