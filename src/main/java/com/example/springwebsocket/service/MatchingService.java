package com.example.springwebsocket.service;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.domain.Matching;
import com.example.springwebsocket.domain.Payload;
import com.example.springwebsocket.domain.dto.MemberDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;

@Service
@Slf4j
@Getter
public class MatchingService {
    private ArrayList<Matching> matchSessions = new ArrayList<>();
    private ArrayList<Matching> sortedMatchSessions = new ArrayList<>();

    // sort rating + 대기시간(10초 주기로 우선순위올려주기)

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

    // 웹소켓에 전달해서 url 옮기기(조건 MATCHED추가하고) localhost:8080/game?gameId=12314&color=white

}
