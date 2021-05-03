package com.practice.restapi_todolist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.restapi_todolist.domain.Follow;
import com.practice.restapi_todolist.domain.FollowPair;
import com.practice.restapi_todolist.exception.CustomBadRequestException;
import com.practice.restapi_todolist.exception.CustomException;
import com.practice.restapi_todolist.exception.CustomNotFoundException;
import com.practice.restapi_todolist.exception.CustomTooManyRequestsException;
import com.practice.restapi_todolist.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FollowService {

    private FollowRepository followRepository;

    @Autowired
    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public Follow createFollow(String toNick) throws CustomException {
        // 있는 member인지 확인
        if(!checkMember(toNick))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("nick", toNick), "입력한 " + toNick + "를 찾지 못했습니다");

        // 로그인된 나의 nick Get
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userNick = userDetails.getUsername();

        // 있는 follow인지 확인
        FollowPair pair = new FollowPair(userNick, toNick);
        if(checkFollow(pair)) {
            // 있으면 follow get
            ObjectMapper mapper = new ObjectMapper();
            Follow follow = Optional.ofNullable(followRepository.findFollow(pair))
                                    .orElseThrow(() -> new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("nick", toNick), "주어진 파라미터로 아이템 생성이 불가합니다."));

            // 본인이 toNick이고, 상태가 req면 상태를 follow로
            if(follow.getToNick().equals(userNick) && follow.getStatus().equals("req")) {
                follow.setStatus("follow");

                if(!followRepository.updateFollow(follow))
                    throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, mapper.convertValue(follow, Map.class), "이미 생성된 follow의 상태 변경을 시도했지만 실패했습니다.");

                return follow;
            } else {
                throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, mapper.convertValue(follow, Map.class), "이미 생성된 follow가 있습니다 follow의 상태를 확인해주세요.");
            }
        } else {
            // 없으면 본인을 from으로 req상태의 follow 생성
            Follow follow = new Follow();
            follow.setFromNick(userNick);
            follow.setToNick(toNick);
            follow.setStatus("req");

            if(!followRepository.createFollow(follow))
                throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("nick", toNick), "주어진 파라미터로 follow 생성에 실패했습니다.");

            return follow;
        }
    }

    public Follow updateFollow(String toNick, String status) throws CustomException {
        // 있는 member인지 확인
        if(!checkMember(toNick))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("nick", toNick), "입력한 " + toNick + "를 찾지 못했습니다");

        // 로그인된 나의 nick Get
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userNick = userDetails.getUsername();

        // nick과의 follow 관계가 있는 지 확인
        // 있으면 status로 변경
        // 없으면 notFound (toNick과의 생성된 follow 관계가 없습니다.)

        return null;
    }

    public boolean deleteFollow(String toNick) throws CustomException {
        // 있는 member인지 확인
        if(!checkMember(toNick))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("nick", toNick), "입력한 " + toNick + "를 찾지 못했습니다");

        // 로그인된 나의 nick Get
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userNick = userDetails.getUsername();

        // nick과의 follow 관게가 있는 지 확인
        // 있으면 follow get
        //          -> follow status가 req면
        //                  -> 본인이 to면 상태 canceled로 변경
        //                  -> 본인이 from이면 삭제
        //          -> follow status가 canceled면 삭제
        //          -> follow status가 follow면 canceled로 변경
        // 없으면 notFound (toNick과의 생성된 follow 관계가 없습니다.)

        return true;
    }

    public Follow findFollowByUserNick(String toNick) throws CustomException {
        // 있는 member인지 확인
        if(!checkMember(toNick))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("nick", toNick), "입력한 " + toNick + "를 찾지 못했습니다");

        // 로그인된 나의 nick Get
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userNick = userDetails.getUsername();

        // nick과의 follow Get

        return null;
    }

    public List<Follow> findAllFollows() {
        // 로그인된 나의 nick Get
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userNick = userDetails.getUsername();

        // 내가 신청하고 신청받은 모든 follow Get

        return null;
    }

    public boolean checkMember(String nick) {
        return followRepository.checkMember(nick);
    }

    public boolean checkFollow(FollowPair pair) {
        return followRepository.checkFollow(pair);
    }
}
