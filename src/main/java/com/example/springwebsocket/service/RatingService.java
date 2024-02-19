package com.example.springwebsocket.service;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.domain.Member;
import com.example.springwebsocket.domain.Payload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class RatingService {
    private final GameService gameService;
    private final MemberService memberService;
    private final ObjectMapper mapper;
    private static final int k = 400;
    private static final int w = 32; // 16, 24, 32 .. (5 ~ 60)

    @Transactional
    public void renew(Long gameId, String winnerColor) throws JsonProcessingException {
        Game game = gameService.findById(gameId);
        Member whiteMember = memberService.findByUsername(game.getWhite());
        Member blackMember = memberService.findByUsername(game.getBlack());

        int whiteRating = whiteMember.getRating();
        int blackRating = blackMember.getRating();
        int differenceRating = blackRating - whiteRating;

        double whiteOdds = 1 / (1 + Math.pow(10, ((double) differenceRating / k)));
        double roundWhiteOdds = Math.round(whiteOdds * 100) / 100.0;

        int whiteRatingAfter;
        int blackRatingAfter;

        String winner = "";
        String loser = "";

        Set<WebSocketSession> sessions = gameService.getMap().get(gameId);

        if(winnerColor.equals("white")) {
            winner = whiteMember.getUsername();
            loser = blackMember.getUsername();

            whiteRatingAfter = whiteRating + (int) (w * (1 - roundWhiteOdds));
            blackRatingAfter = blackRating + (int) (w * (roundWhiteOdds - 1));

            whiteMember.win(whiteRatingAfter);
            blackMember.lose(blackRatingAfter);

            Payload p1 = new Payload(gameId, "white", Payload.PayloadType.RESULT, String.valueOf(whiteRatingAfter));
            Payload p2 = new Payload(gameId, "black", Payload.PayloadType.RESULT, String.valueOf(blackRatingAfter));
            TextMessage txt1 = new TextMessage(mapper.writeValueAsString(p1));
            TextMessage txt2 = new TextMessage(mapper.writeValueAsString(p2));

            sessions.stream().forEach(session -> {
                try {
                    session.sendMessage(txt1);
                    session.sendMessage(txt2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            winner = blackMember.getUsername();
            loser = whiteMember.getUsername();

            whiteRatingAfter = whiteRating + (int) (w * (-1 * roundWhiteOdds));
            blackRatingAfter = blackRating + (int) (w * (roundWhiteOdds));

            whiteMember.lose(whiteRatingAfter);
            blackMember.win(blackRatingAfter);

            Payload p1 = new Payload(gameId, "white", Payload.PayloadType.RESULT, String.valueOf(whiteRatingAfter));
            Payload p2 = new Payload(gameId, "black", Payload.PayloadType.RESULT, String.valueOf(blackRatingAfter));
            TextMessage txt1 = new TextMessage(mapper.writeValueAsString(p1));
            TextMessage txt2 = new TextMessage(mapper.writeValueAsString(p2));

            sessions.stream().forEach(session -> {
                try {
                    session.sendMessage(txt1);
                    session.sendMessage(txt2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        game.finishGame(winner, loser, true);
    }
}
