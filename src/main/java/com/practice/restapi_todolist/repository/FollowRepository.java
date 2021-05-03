package com.practice.restapi_todolist.repository;

import com.practice.restapi_todolist.domain.Follow;
import com.practice.restapi_todolist.domain.FollowPair;

import java.util.List;

public interface FollowRepository {
    boolean createFollow(Follow follow);
    boolean deleteFollow(Follow follow);
    boolean	updateFollow(Follow follow);
    Follow findFollow(FollowPair pair);
    List<Follow> findAllFollows(String nick);
    boolean checkMember(String nick);
    boolean checkFollow(FollowPair pair);
}
