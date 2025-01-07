package com.example.gamemate.domain.follow;

import com.example.gamemate.domain.follow.dto.*;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 팔로우하기
    // todo: 현재 로그인이 구현되지 않아 1번유저가 팔로우 하는것으로 구현했으니 추후 로그인이 구현되면 follower 는 로그인한 유저로 설정
    @Transactional
    public FollowCreateResponseDto createFollow(FollowCreateRequestDto dto) {
        User follower = userRepository.findById(1L).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));
        User followee = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));

        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new ApiException(ErrorCode.IS_ALREADY_FOLLOWED);
        }

        if (Objects.equals(follower.getEmail(), dto.getEmail())) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        }

        Follow follow = new Follow(follower,followee);
        followRepository.save(follow);

        return new FollowCreateResponseDto("팔로우 했습니다.");
    }

    // 팔로우 취소하기
    // todo: 현재 로그인이 구현되지 않아 1번유저가 팔로우를 취소 하는것으로 구현했으니 추후 로그인이 구현되면 follower 는 로그인한 유저로 설정
    @Transactional
    public FollowDeleteResponseDto deleteFollow(Long followId) {
        Follow findFollow = followRepository.findById(followId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_FOLLOW));

        User follower = userRepository.findById(1L).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));

        if (findFollow.getFollower() != follower) {
            throw new ApiException(ErrorCode.INVALID_INPUT);
        }

        followRepository.delete(findFollow);

        return new FollowDeleteResponseDto("팔로우가 취소되었습니다.");
    }

    // 팔로워 목록보기
    public List<FollowFindResponseDto> findFollowerList(FollowFindRequestDto dto) {
        User followee = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));
        List<Follow> FollowListByFollowee = followRepository.findByFollowee(followee);

        List<User> FollowerListByFollowee = new ArrayList<>();

        for (Follow follow : FollowListByFollowee) {
            FollowerListByFollowee.add(follow.getFollower());
        }

        return FollowerListByFollowee
                .stream()
                .map(FollowFindResponseDto::toDto)
                .toList();
    }
}
