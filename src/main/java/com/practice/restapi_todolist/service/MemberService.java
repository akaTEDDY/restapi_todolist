package com.practice.restapi_todolist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.restapi_todolist.domain.Member;
import com.practice.restapi_todolist.exception.CustomBadRequestException;
import com.practice.restapi_todolist.exception.CustomException;
import com.practice.restapi_todolist.exception.CustomNotFoundException;
import com.practice.restapi_todolist.repository.MemberRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {

    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member) throws CustomException {
        // nick 존재 유무 확인
        if(checkMember(member.getNick()))
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("nick", member.getNick()), "이미 " + member.getNick() + "로 사용된 nick입니다.");

        // member 생성
        ObjectMapper mapper = new ObjectMapper();
        if(!memberRepository.createMember(member))
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, mapper.convertValue(member, Map.class), "주어진 파라미터로 멤버 생성이 불가합니다.");

        // 결과 return
        Optional<Member> createdMember = Optional.ofNullable(findMember(member.getNick()));
        return createdMember.orElse(null);
    }

    public Member updateMember(String nick, Member member) throws CustomException {
        // nick으로 findMember - A (null check)
        Optional<Member> optMember = Optional.ofNullable(findMember(nick));

        // A에 member의 값 Set (null check)
        Member m = optMember.orElse(null);
        if(m == null)
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND,Map.of("nick", nick), "입력한 nick을 가진 멤버를 찾을 수 없습니다.");

        if(!member.getNick().isEmpty())     m.setNick(member.getNick());
        if(!member.getPw().isEmpty())       m.setPw(member.getPw());
        if(!member.getEmail().isEmpty())    m.setEmail(member.getEmail());

        // member update 요청
        // 결과 return
        ObjectMapper mapper = new ObjectMapper();
       if(!memberRepository.updateMember(m))
           throw new CustomNotFoundException(HttpStatus.BAD_REQUEST, mapper.convertValue(member, Map.class), "주어진 파라미터로 멤버 수정이 불가합니다.");

        return m;
    }

    public boolean deleteMember(String nick) throws CustomException {
        // nick이 있는 지 확인
        if(!checkMember(nick))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("nick", nick), "입력한 nick을 가진 멤버를 찾을 수 없습니다.");

        // member delete 요청
        // 결과 return
        return memberRepository.deleteMember(nick);
    }

    public Member findMember(String nick) throws CustomException {
        if(!checkMember(nick))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("nick", nick), "입력한 nick을 가진 멤버를 찾을 수 없습니다.");

        // member find 요청
        Optional<Member> member = Optional.ofNullable(memberRepository.findMember(nick));
        return member.orElse(null);
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAllMembers();
    }

    public boolean checkMember(String nick) {
        return memberRepository.checkMember(nick);
    }

    @Override
    public UserDetails loadUserByUsername(String nick) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        try {
            Optional<Member> member = Optional.ofNullable(findMember(nick));

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String pw = encoder.encode(member.orElseGet(() -> new Member()).getPw());

            if(pw.isEmpty())
                throw new UsernameNotFoundException(nick + " 인증 불가");

            return new User(nick, pw, authorities);
        } catch (CustomException e) {
            throw new UsernameNotFoundException(nick + " 인증 불가");
        }
    }
}
