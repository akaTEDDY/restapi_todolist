package com.practice.restapi_todolist.service;

import com.practice.restapi_todolist.domain.Member;
import com.practice.restapi_todolist.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {

    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member) {
        // nick 존재 유무 확인
        if(checkMember(member.getNick()))
            return null;

        // member 생성
        if(!memberRepository.createMember(member))
            return null;

        // 결과 return
        Optional<Member> createdMember = Optional.ofNullable(findMember(member.getNick()));
        return createdMember.orElse(null);
    }

    public Member updateMember(String nick, Member member) {
        // nick으로 findMember - A (null check)
        Optional<Member> optMember = Optional.ofNullable(findMember(nick));

        // A에 member의 값 Set (null check)
        Member m = optMember.orElse(null);
        if(m == null) return null;

        if(member.getNick().isEmpty())     m.setNick(member.getNick());
        if(member.getPw().isEmpty())       m.setPw(member.getPw());
        if(member.getEmail().isEmpty())    m.setEmail(member.getEmail());

        // member update 요청
        // 결과 return
       if(!memberRepository.updateMember(m))
           return null;

        return m;
    }

    public boolean deleteMember(String nick) {
        // nick이 있는 지 확인
        if(!checkMember(nick))
            return false;

        // member delete 요청
        // 결과 return
        return memberRepository.deleteMember(nick);
    }

    public Member findMember(String nick) {
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

        Optional<Member> member = Optional.ofNullable(findMember(nick));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pw = encoder.encode(member.orElseGet(() -> new Member()).getPw());

        if(pw.isEmpty())
            return new User(nick, pw, null);

        return new User(nick, pw, authorities);
    }
}
