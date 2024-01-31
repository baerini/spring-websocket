package com.example.springwebsocket.service;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.domain.Matching;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class MatchingService {
    private final GameService gameService;

    private ArrayList<Matching> matchingSessions = new ArrayList<>();
    private boolean processing = false;
    private static int ratingMaxDifference = 100;
    private static int waitingTimeWeight = 1;

    /**
     *  session, memberDto, time(Long), waiting(Date)
     *  웹소켓 match 들어왔을때 matchSessions 가 2이상일 때 매칭 알고리즘 시작
     *  => 계속 실행되는 문제점 어떻게 처리? 3, 4, 5, 6 .. => processing 진행 중 boolean 설정
     *  => processing True: 매칭 진행 중, False: 매칭 시작 안함
     *  => True : 참여만 가능
     *  => False : 참여 가능 + 타이머 재설정
     *  => while(len(matchSessions) > 2) {
     *      // 10 초 뒤에 정렬 시작
     *
     *  }
     *  시간 주기 : 시스템 시간 기준 10초
     *
     *  1. rating 기준 정렬
     *  2. 인덱스 0부터 n-1까지 순회 ( step = 1 )
     *  2-1. 비교, 1. date 로 기다린 시간 구해서 최대 레이팅 차이 범위 알아내고 2. 서로의 레이팅이 범위에 속하는지 검사
     *      둘 중 한명이라도 최대 레이팅 차이 범위 안에 속한다면 바로 시작
     *      (i, i+1) => 매칭 성공?
     *               => 매칭 실패? i += 1
     *
     *  2-2. 삭제, 탐색 후 매칭성공 matchedSessions 리스트 따로 만들어서 나중에 시간 지나면 matchSessions 에서 삭제 후 따로 보내주기
     *  3. matchedSessions 순회하면서 흑백 정하고 api 전송
     *
     *  최대 레이팅 차이 범위 : 100
     *  정렬은 memberDto 의 rating 기준으로
     *  waiting 존재 이유 => 매칭 범위 가중치 쁠마
     *
     */

    // 1. 매칭 참여 => 타이머 시작
    public void participate(Matching matching) {
        matchingSessions.add(matching);
        log.info("participate 시작 후 세션 = {}", matchingSessions);

        // 이미 매칭 잡고있음
        if (!processing) {
            processing = true;
            while (matchingSessions.size() >= 2) {
                // 10 초 시작

                // 10 초 뒤의 로직
                sorting();
                inspect();
                classification();
                break; // 임시
            }
            processing = false;
        }
    }

    public void sorting() {
        Collections.sort(matchingSessions, Comparator.comparing(m -> m.getMember().getRating()));
    }

    // 2. 인덱스 0부터 n-1까지 순회 ( step = 1 )
    public void inspect() {
        for(int i = 0; i < matchingSessions.size() - 1; i++) {

            //        Date currentDate = new Date();
            //        시간 차이 계산 (밀리초 단위)
            //        long timeDifference = currentDate.getTime() - matchingSessions.get(0).getWaiting().getTime();
            //        log.info("시간차이 = {}", timeDifference); ms니깐 1000으로 나눠야함

            Matching player1 = matchingSessions.get(i);
            Matching player2 = matchingSessions.get(i+1);

            if (player1.isState()) {
                continue;
            }

            // 원하는 timer 다름
            if (!player1.getTime().equals(player2.getTime())) {
                continue;
            }

            Date currentDate = new Date();
            int range1 = ratingMaxDifference +
                    (waitingTimeWeight * (int)((currentDate.getTime() - player1.getWaiting().getTime()) / 1000));
            int range2 = ratingMaxDifference +
                    (waitingTimeWeight * (int)((currentDate.getTime() - player2.getWaiting().getTime()) / 1000));

            log.info("range1 = {}", range1);
            log.info("range2 = {}", range2);

            // player1 입장
            if (player1.getMember().getRating() - range1 <= player2.getMember().getRating()
                && player2.getMember().getRating() <= player1.getMember().getRating() + range1) {
                player1.setState();
                player2.setState();
                continue;
            }

            // player2 입장
            if (player2.getMember().getRating() - range2 <= player1.getMember().getRating()
                    && player1.getMember().getRating() <= player2.getMember().getRating() + range2) {
                player1.setState();
                player2.setState();
            }
        }

        log.info("inspect 종료 후 세션 = {}", matchingSessions);
    }

    public void classification() {
        ArrayList<Matching> matchedSessions = new ArrayList<>();
        ArrayList<Matching> notMatchedSessions = new ArrayList<>();
        for (int i = 0; i < matchingSessions.size(); i++) {
            if (matchingSessions.get(i).isState()) {
                matchedSessions.add(matchingSessions.get(i));
            } else {
                notMatchedSessions.add(matchingSessions.get(i));
            }
        }

        //copy
        if (!matchedSessions.isEmpty()) {
            matchSuccess(matchedSessions);
        }
//        matchingSessions = new ArrayList<>();
        matchingSessions.addAll(notMatchedSessions);
        log.info("notMatchedSessions = {}", notMatchedSessions);
        log.info("classification 종료 후 세션 = {}", matchingSessions);
    }

    public void matchSuccess(ArrayList<Matching> matchedSessions) {
        if (matchedSessions.size() % 2 != 0) {
            log.error("매칭완료리스트 홀수 에러");
            return;
        }

        Game game = gameService.createGame();
    }

    // 3. matchedSessions 순회하면서 흑백 정하고 api 전송


    // 매칭완료 => 흑백 랜덤 배정 => 2명의 인덱스?? 반환
//if (matchingSessions.size() == 2) { //time 같은거 아직 배제
//        Game game = gameService.createGame();
//        Long gameId = game.getId();
//        WebSocketSession whiteSession = matchingSessions.get(0).getSession();
//        WebSocketSession blackSession = matchingSessions.get(1).getSession();
//
//        for(int i=0; i<2; i++) {
//            if (i == 0) {
//                String str = "white|" + time;
//                Payload p = new Payload(gameId, "me", Payload.PayloadType.MATCHED, str);
//                TextMessage txt = new TextMessage(mapper.writeValueAsString(p));
//                log.info("txt.toString = {}", txt);
//                whiteSession.sendMessage(txt);
//                System.out.println("화이트 전송 완료");
//            } else {
//                String str = "black|" + time;
//                Payload p = new Payload(gameId, "me", Payload.PayloadType.MATCHED, str);
//                TextMessage txt = new TextMessage(mapper.writeValueAsString(p));
//                log.info("txt.toString = {}", txt);
//                blackSession.sendMessage(txt);
//                System.out.println("블랙 전송 완료");
//            }
//        }
//    }

    // 2명 리스트에서 제거

    // 대기시간 제외 순 rating 200이상 차이나는지 확인


}
