package com.example.gamemate.domain.follow.controller;

import com.example.gamemate.domain.follow.service.FollowService;
import com.example.gamemate.domain.follow.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    /**
     * 팔로우 하기
     * @param dto FollowCreateRequestDto
     * @return followResponseDto
     */
    @PostMapping
    public ResponseEntity<FollowResponseDto> createFollow(
            @RequestBody FollowCreateRequestDto dto
    ) {

        FollowResponseDto followResponseDto = followService.createFollow(dto);
        return new ResponseEntity<>(followResponseDto, HttpStatus.CREATED);
    }

    /**
     * 팔로우 취소
     * @param id 취소할 팔로우 식별자
     * @return NO_CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollow(
            @PathVariable Long id
    ) {

        followService.deleteFollow(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 팔로우 상태 확인 (follower 가 followee 를 팔로우 했는지 확인)
     * @param followerEmail
     * @param followeeEmail
     * @return followBooleanResponseDto
     */
    @GetMapping("/status")
    public ResponseEntity<FollowBooleanResponseDto> findFollow(
            @RequestParam String followerEmail,
            @RequestParam String followeeEmail
    ) {

        FollowBooleanResponseDto followBooleanResponseDto = followService.findFollow(followerEmail, followeeEmail);
        return new ResponseEntity<>(followBooleanResponseDto, HttpStatus.OK);
    }

    /**
     * 팔로우 목록 보기
     * @param email 팔로우 목록을 보고 싶은 유저 email
     * @return followFindResponseDtoList
     */
    @GetMapping("/followers")
    public ResponseEntity<List<FollowFindResponseDto>> findFollowers(
            @RequestParam String email
    ) {

        List<FollowFindResponseDto> followFindResponseDtoList = followService.findFollowers(email);
        return new ResponseEntity<>(followFindResponseDtoList, HttpStatus.OK);
    }

    /**
     * 팔로잉 목록 보기
     * @param email 팔로잉 목록을 보고 싶은 유저 email
     * @return followFindResponseDtoList
     */
    @GetMapping("/following")
    public ResponseEntity<List<FollowFindResponseDto>> findFollowing(
            @RequestParam String email
    ) {

        List<FollowFindResponseDto> followFindResponseDtoList = followService.findFollowing(email);
        return new ResponseEntity<>(followFindResponseDtoList, HttpStatus.OK);
    }
}
