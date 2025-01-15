package com.example.gamemate.domain.match.service;

import com.example.gamemate.domain.match.dto.*;
import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.match.entity.MatchUserInfo;
import com.example.gamemate.domain.match.enums.GameRank;
import com.example.gamemate.domain.match.enums.MatchStatus;
import com.example.gamemate.domain.match.repository.MatchRepository;
import com.example.gamemate.domain.match.repository.MatchUserInfoRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.UserStatus;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchUserInfoRepository matchUserInfoRepository;

    // 매칭 요청 생성
    @Transactional
    public MatchResponseDto createMatch(MatchCreateRequestDto dto, User loginUser) {

        User receiver = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (receiver.getUserStatus() == UserStatus.WITHDRAW) {
            throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
        }

        if (matchRepository.existsBySenderAndReceiverAndStatus(loginUser, receiver, MatchStatus.PENDING)) {
            throw new ApiException(ErrorCode.IS_ALREADY_PENDING);
        }

        Match match = new Match(dto.getMessage(), loginUser, receiver);
        matchRepository.save(match);

        return MatchResponseDto.toDto(match);
    }

    // 매칭 수락/거절
    @Transactional
    public void updateMatch(Long id, MatchUpdateRequestDto dto, User loginUser) {

        Match findMatch = matchRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_NOT_FOUND));

        if (findMatch.getStatus() != MatchStatus.PENDING) {
            throw new ApiException(ErrorCode.IS_ALREADY_PROCESSED);
        }

        if (!Objects.equals(loginUser.getId(), findMatch.getReceiver().getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        findMatch.updateStatus(dto.getStatus());
    }

    // 받은 매칭 전체 조회
    public List<MatchResponseDto> findAllReceivedMatch(User loginUser) {

        List<Match> matchList = matchRepository.findAllByReceiverId(loginUser.getId());

        return matchList
                .stream()
                .map(MatchResponseDto::toDto)
                .toList();
    }

    // 보낸 매칭 전체 조회
    public List<MatchResponseDto> findAllSentMatch(User loginUser) {

        List<Match> matchList = matchRepository.findAllBySenderId(loginUser.getId());

        return matchList
                .stream()
                .map(MatchResponseDto::toDto)
                .toList();
    }

    // 매치 삭제 (취소)
    @Transactional
    public void deleteMatch(Long id, User loginUser) {

        Match findMatch = matchRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.MATCH_NOT_FOUND));

        if (!Objects.equals(findMatch.getSender().getId(), loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        matchRepository.delete(findMatch);
    }

    // 내 정보 입력
    @Transactional
    public MatchInfoResponseDto createMyInfo(MatchInfoCreateRequestDto dto, User loginUser) {

        MatchUserInfo matchUserInfo = new MatchUserInfo(
                dto.getGender(),
                dto.getLanes(),
                dto.getPurposes(),
                dto.getPlayTimeRanges(),
                dto.getGameRank(),
                dto.getSkillLevel(),
                dto.getMicUsage(),
                dto.getMessage(),
                loginUser
        );

        matchUserInfoRepository.save(matchUserInfo);


        log.info("라인 : {}, 목적 : {}, 플레이 시간대 : {}", matchUserInfo.getLanes(), matchUserInfo.getPurposes(), matchUserInfo.getPlayTimeRanges());
        return MatchInfoResponseDto.toDto(matchUserInfo);
    }

    // 매칭 로직
    public List<MatchInfoResponseDto> findRecommendation(MatchSearchConditionDto dto, User loginUser) {
        // 1. 성별과 플레이 시간대를 기준으로 필터링된 사용자 정보 조회
        List<MatchUserInfo> filteredUsers = matchUserInfoRepository.findByGenderAndPlayTimeRanges(
                dto.getGender(),
                dto.getPlayTimeRanges(),
                loginUser.getId()
        );

        // 2. 매칭 점수 계산 및 저장
        for (MatchUserInfo matchUserInfo : filteredUsers) {
            int score = calculateMatchScore(dto, matchUserInfo);
            matchUserInfo.updateMatchScore(score);
        }

        // 3. 매칭 점수 내림차순으로 정렬
        filteredUsers.sort((u1, u2) -> Integer.compare(u2.getMatchScore(), u1.getMatchScore()));


        // 4. 동점자 처리 (동점자끼리 랜덤 섞기)
        List<MatchUserInfo> resultList = handleTies(filteredUsers);

        // 5. 상위 5명 추출 및 DTO 변환
        return resultList.stream()
                .limit(5)
                .map(MatchInfoResponseDto::toDto)
                .collect(Collectors.toList());
    }


    // 점수 계산 로직
    private int calculateMatchScore(MatchSearchConditionDto condition, MatchUserInfo userInfo) {
        int score = 0;
        int normalScorePerMatch = 5; // 매칭되는 항목당 점수
        int priorityWeight = 2; // 우선순위 가중치

        String priority = condition.getPriority();

        // 우선순위 항목 점수 계산 및 가중치 적용
        if (priority != null) {
            switch (priority) {
                case "lanes":
                    int matchedLanes = (int) condition.getLanes().stream()
                            .filter(userInfo.getLanes()::contains)
                            .count();
                    score += matchedLanes * normalScorePerMatch * priorityWeight;
                    break;
                case "purposes":
                    int matchedPurposes = (int) condition.getPurposes().stream()
                            .filter(userInfo.getPurposes()::contains)
                            .count();
                    score += matchedPurposes * normalScorePerMatch * priorityWeight;
                    break;
                case "playTimeRanges":
                    int matchedPlayTimeRanges = (int) condition.getPlayTimeRanges().stream()
                            .filter(userInfo.getPlayTimeRanges()::contains)
                            .count();
                    score += matchedPlayTimeRanges * normalScorePerMatch * priorityWeight;
                    break;
                case "gameRank":
                    if (condition.getGameRank().equals(userInfo.getGameRank())) {
                        score += normalScorePerMatch * priorityWeight * 2;
                    } else if (isRankSimilar(condition.getGameRank(), userInfo.getGameRank())) {
                        score += normalScorePerMatch * priorityWeight;
                    }
                    break;
                case "skillLevel":
                    int skillLevelDifference = Math.abs(condition.getSkillLevel() - userInfo.getSkillLevel());
                    score += (normalScorePerMatch * 2 - skillLevelDifference) * priorityWeight;
                    break;
                case "micUsage":
                    if (condition.getMicUsage().equals(userInfo.getMicUsage())) {
                        score += normalScorePerMatch * priorityWeight * 2;
                    }
                    break;
            }
        }

        // 우선순위가 아닌 조건의 점수 계산 방식
        if (priority == null || !priority.equals("lanes")) {
            int matchedLanes = (int) condition.getLanes().stream()
                    .filter(userInfo.getLanes()::contains)
                    .count();
            score += matchedLanes * normalScorePerMatch;
        }

        if (priority == null || !priority.equals("purposes")) {
            int matchedPurposes = (int) condition.getPurposes().stream()
                    .filter(userInfo.getPurposes()::contains)
                    .count();
            score += matchedPurposes * normalScorePerMatch;
        }

        if (priority == null || !priority.equals("playTimeRanges")) {
            int matchedPlayTimeRanges = (int) condition.getPlayTimeRanges().stream()
                    .filter(userInfo.getPlayTimeRanges()::contains)
                    .count();
            score += matchedPlayTimeRanges * normalScorePerMatch;
        }

        if (priority == null || !priority.equals("gameRank")) {
            if (condition.getGameRank().equals(userInfo.getGameRank())) {
                score += normalScorePerMatch * 2;
            } else if (isRankSimilar(condition.getGameRank(), userInfo.getGameRank())) {
                score += normalScorePerMatch;
            }
        }

        if (priority == null || !priority.equals("skillLevel")){
            int skillLevelDifference = Math.abs(condition.getSkillLevel() - userInfo.getSkillLevel());
            score += (normalScorePerMatch * 2 - skillLevelDifference);
        }

        if(priority == null || !priority.equals("micUsage")){
            if (condition.getMicUsage().equals(userInfo.getMicUsage())) {
                score += normalScorePerMatch * 2;
            }
        }

        return score;
    }


    // 랭크가 비슷한지 판단하는 로직 추가
    private boolean isRankSimilar(GameRank conditionRank, GameRank userRank) {
        if (conditionRank == GameRank.DONT_MIND) {
            return true; // "상관없음"은 모든 랭크와 유사하다고 판단
        }

        int conditionRankIndex = conditionRank.ordinal();
        int userRankIndex = userRank.ordinal();
        return Math.abs(conditionRankIndex - userRankIndex) <= 1; // 랭크 차이가 1 이하면 유사하다고 판단
    }


    // 동점자는 랜덤으로 섞어서 출력
    private List<MatchUserInfo> handleTies(List<MatchUserInfo> sortedUsers) {
        if (sortedUsers.isEmpty()) {
            return sortedUsers;
        }
        List<MatchUserInfo> resultList = new ArrayList<>();
        List<MatchUserInfo> tieGroup = new ArrayList<>(); // 동점자 그룹 임시 저장

        tieGroup.add(sortedUsers.get(0));

        for (int i = 1; i < sortedUsers.size(); i++) {
            MatchUserInfo currentUser = sortedUsers.get(i);
            MatchUserInfo previousUser = sortedUsers.get(i - 1);

            if (currentUser.getMatchScore() == previousUser.getMatchScore()) {
                tieGroup.add(currentUser);
            } else {
                Collections.shuffle(tieGroup); // 동점자 그룹 섞기
                resultList.addAll(tieGroup);
                tieGroup.clear(); // 다음 그룹을 위해 비우기
                tieGroup.add(currentUser); //새로운 그룹 시작
            }
        }

        Collections.shuffle(tieGroup);
        resultList.addAll(tieGroup); //마지막 그룹 추가

        return resultList;
    }
}

