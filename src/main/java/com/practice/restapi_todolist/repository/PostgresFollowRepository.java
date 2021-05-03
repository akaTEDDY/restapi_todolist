package com.practice.restapi_todolist.repository;

import com.practice.restapi_todolist.domain.Follow;
import com.practice.restapi_todolist.domain.FollowPair;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PostgresFollowRepository implements FollowRepository {

    private SqlSession sqlSession;

    @Autowired
    public PostgresFollowRepository(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public boolean createFollow(Follow follow) {
        return sqlSession.insert("Follow.createFollow", follow) > 0 ? true : false;
    }

    @Override
    public boolean deleteFollow(Follow follow) {
        return sqlSession.delete("Follow.deleteFollow", follow) > 0 ? true : false;
    }

    @Override
    public boolean updateFollow(Follow follow) {
        return sqlSession.update("Follow.updateFollow", follow) > 0 ? true : false;
    }

    @Override
    public Follow findFollow(FollowPair pair) {
        return sqlSession.selectOne("Follow.findFollow", pair);
    }

    @Override
    public List<Follow> findAllFollows(String nick) {
        return sqlSession.selectList("Follow.findAllFollows", nick);
    }

    @Override
    public boolean checkMember(String nick) {
        return (int)sqlSession.selectOne("Follow.checkMember", nick) > 0 ? true : false;
    }

    @Override
    public boolean checkFollow(FollowPair pair) {
        return (int)sqlSession.selectOne("Follow.checkFollow", pair) > 0 ? true : false;
    }
}
