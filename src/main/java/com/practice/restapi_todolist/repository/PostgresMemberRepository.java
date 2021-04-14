package com.practice.restapi_todolist.repository;

import com.practice.restapi_todolist.domain.Member;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostgresMemberRepository implements MemberRepository {

    private SqlSession sqlSession;

    @Autowired
    public PostgresMemberRepository(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public boolean createMember(Member member) {
        return sqlSession.insert("Member.createMember", member) > 0 ? true : false;
    }

    @Override
    public boolean updateMember(Member member) {
        return sqlSession.update("Member.updateMember", member) > 0 ? true : false;
    }

    @Override
    public boolean deleteMember(String nick) {
        return sqlSession.delete("Member.deleteMember", nick) > 0 ? true : false;
    }

    @Override
    public Member findMember(String nick) {
        return sqlSession.selectOne("Member.findMember", nick);
    }

    @Override
    public List<Member> findAllMembers() {
        return sqlSession.selectList("Member.findAllMembers");
    }

    @Override
    public boolean checkMember(String nick) {
        return ((int)sqlSession.selectOne("Member.checkMember", nick) >= 1) ? true : false;
    }
}
