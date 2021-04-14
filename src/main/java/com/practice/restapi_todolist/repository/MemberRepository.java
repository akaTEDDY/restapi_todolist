package com.practice.restapi_todolist.repository;

import com.practice.restapi_todolist.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    boolean createMember(Member member);
    boolean updateMember(Member member);
    boolean deleteMember(String nick);
    Member findMember(String nick);
    List<Member> findAllMembers();
    boolean checkMember(String nick);
}
