package com.practice.restapi_todolist.controller;

import com.practice.restapi_todolist.domain.Member;
import com.practice.restapi_todolist.service.MemberService;
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

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/member")
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        // request 유효성 검사
        if(member.getNick().isEmpty() || member.getPw().isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // member 생성 요청
        Optional<Member> createdMember = Optional.ofNullable(memberService.createMember(member));

        if(!createdMember.isPresent())
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(createdMember.get(), headers, HttpStatus.OK);
    }

    @PutMapping("/member/{nick}")
    public ResponseEntity<Member> updateMember(@PathVariable("nick") String nick, @RequestBody Member member) {
        // request 유효성 검사
        if(nick.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // member 수정 요청
        Optional<Member> updatedMember = Optional.ofNullable(memberService.updateMember(nick, member));

        if(!updatedMember.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(updatedMember.get(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/member/{nick}")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable("nick") String nick) {
        // request 유효성 검사
        if(nick.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // member 삭제 요청
        if(!memberService.deleteMember(nick))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(new HashMap<String, String>(){{put("nick", nick);}}, headers, HttpStatus.OK);
    }

    @GetMapping("/member/{nick}")
    public ResponseEntity<Member> findMember_pv(@PathVariable("nick") String nick) {
        // request 유효성 검사
        if(nick.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // member 조회 요청
        Optional<Member> member = Optional.ofNullable(memberService.findMember(nick));

        if(!member.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(member.get(), headers, HttpStatus.OK);
    }

    @GetMapping("/member")
    public ResponseEntity<List<Member>> findMember_rp(@RequestParam(value = "nick", required = false) String nick) {
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // response return
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(members, headers, HttpStatus.OK);
    }
}
