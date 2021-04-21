package com.practice.restapi_todolist.service;

import com.practice.restapi_todolist.domain.Follow;
import com.practice.restapi_todolist.domain.Item;
import com.practice.restapi_todolist.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowService {

    private FollowRepository followRepository;

    @Autowired
    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public Follow createFollow(String toNick) {
        // 있는 member인지 확인
        //      -> 없으면 not found

        // 로그인된 나의 nick Get

        // 있는 follow인지 확인
        // 있으면 follow get -> 본인이 toNick이고, 상태가 req면 상태를 follow로
        //                  -> 아니면 BadRequest throw (올바른 요청이 아닙니다.)
        // 없으면 본인을 from으로 req상태의 follow 생성

        return null;
    }

    public Follow updateFollow(String toNick, String status) {
        // 있는 member인지 확인
        //      -> 없으면 not found

        // 로그인된 나의 nick Get

        // nick과의 follow 관계가 있는 지 확인
        // 있으면 status로 변경
        // 없으면 notFound (toNick과의 생성된 follow 관계가 없습니다.)

        return null;
    }

    public boolean deleteFollow(String toNick) {
        // 있는 member인지 확인
        //      -> 없으면 not found

        // 로그인된 나의 nick Get

        // nick과의 follow 관게가 있는 지 확인
        // 있으면 follow get
        //          -> follow status가 req면
        //                  -> 본인이 to면 상태 cancelled로 변경
        //                  -> 본인이 from이면 삭제
        //          -> follow status가 canceled면 삭제
        //          -> follow status가 follow면 canceled로 변경
        // 없으면 notFound (toNick과의 생성된 follow 관계가 없습니다.)

        return true;
    }

    public Follow findFollowByUserNick(String toNick) {
        // 있는 member인지 확인
        //      -> 없으면 not found

        // 로그인된 나의 nick Get

        // nick과의 follow Get

        return null;
    }

    public List<Follow> findAllFollows() {
        // 로그인된 나의 nick Get

        // 내가 신청하고 신청받은 모든 follow Get

        return null;
    }
}
