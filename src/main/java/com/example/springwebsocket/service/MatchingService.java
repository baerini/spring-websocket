package com.example.springwebsocket.service;

import com.example.springwebsocket.domain.Game;
import com.example.springwebsocket.domain.Matching;
import com.example.springwebsocket.domain.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class MatchingService {
    private final GameService gameService;
    private final ObjectMapper mapper;

    private ArrayList<Matching> matchingSessions = new ArrayList<>();
    private boolean processing = false;
    private static final int ratingMaxDifference = 100;
    private static final int waitingTimeWeight = 1;

    @Scheduled(fixedRate = 10000)
    public void matching() throws IOException {
        if(matchingSessions.size() >= 2) {
            sorting();
            inspect();
            classification();
        }
    }

    public void sorting() {
        Collections.sort(matchingSessions, Comparator.comparing(m -> m.getMember().getRating()));
        Collections.sort(matchingSessions, Comparator.comparing(Matching::getTime));
    }

    public void inspect() {
        for(int i = 0; i < matchingSessions.size() - 1; i++) {
            Matching player1 = matchingSessions.get(i);
            Matching player2 = matchingSessions.get(i+1);

            if (player1.isState()) {
                continue;
            }

            if (!(player1.getTime() == player2.getTime())) {
                continue;
            }

            Date currentDate = new Date();
            int range1 = ratingMaxDifference +
                    (waitingTimeWeight * (int)((currentDate.getTime() - player1.getWaiting().getTime()) / 1000));
            int range2 = ratingMaxDifference +
                    (waitingTimeWeight * (int)((currentDate.getTime() - player2.getWaiting().getTime()) / 1000));

            if (player1.getMember().getRating() - range1 <= player2.getMember().getRating()
                && player2.getMember().getRating() <= player1.getMember().getRating() + range1) {
                player1.setState();
                player2.setState();
                continue;
            }

            if (player2.getMember().getRating() - range2 <= player1.getMember().getRating()
                    && player1.getMember().getRating() <= player2.getMember().getRating() + range2) {
                player1.setState();
                player2.setState();
            }
        }
    }

    public void classification() throws IOException {
        ArrayList<Matching> matchedSessions = new ArrayList<>();
        ArrayList<Matching> notMatchedSessions = new ArrayList<>();
        for (int i = 0; i < matchingSessions.size(); i++) {
            if (matchingSessions.get(i).isState()) {
                matchedSessions.add(matchingSessions.get(i));
            } else {
                notMatchedSessions.add(matchingSessions.get(i));
            }
        }


        if (!matchedSessions.isEmpty()) {
            matchSuccess(matchedSessions);
        }

        matchingSessions.clear();
        matchingSessions.addAll(notMatchedSessions);
    }

    public void matchSuccess(ArrayList<Matching> matchedSessions) throws IOException {
        if (matchedSessions.size() % 2 != 0) {
            return;
        }

        for (int i = 0; i < matchedSessions.size(); i += 2) {
            Matching player1 = matchingSessions.get(i); //흰
            Matching player2 = matchingSessions.get(i+1); //검

            Game game = gameService.createGame(player1.getMember().getUsername()
                    , player2.getMember().getUsername()
                    , player1.getTime());
            Long gameId = game.getId();

            String color1 = "white";
            String color2 = "black";
            Payload p1 = new Payload(gameId, "server", Payload.PayloadType.MATCHED, color1);
            Payload p2 = new Payload(gameId, "server", Payload.PayloadType.MATCHED, color2);
            TextMessage txt1 = new TextMessage(mapper.writeValueAsString(p1));
            TextMessage txt2 = new TextMessage(mapper.writeValueAsString(p2));
            player1.getSession().sendMessage(txt1);
            player2.getSession().sendMessage(txt2);
        }
    }
}
