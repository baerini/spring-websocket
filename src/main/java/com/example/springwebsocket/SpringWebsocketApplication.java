package com.example.springwebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringWebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringWebsocketApplication.class, args);
	}

	/*
	*  1. post로 채팅방 이름 넘어옴 => ChatRoom 객체 생성
	* 	request
	* 	{
	* 		"name" : "name"
	* 	}
	*
	* 	response
	* 	{
			"roomId": "1665c380-52a4-4feb-a3fa-5877a7d81d11",
			"name": "name",
			"sessions": []
		}
	*
	*  2. 누군가 springConfig에서 지정한 ws://localhost:8080/ws/chat에 접속하여
	* 		json(chatContent) 전송  => WebSocketChatHandler가 파싱하여 chatContent생성 후 chatRoomId
	* 		에 매칭되는 chatRoom객체를 찾아 handleAction 함수 수행
	*
	*  3. enter상태면 message수정, talk라면 그대로, chatroom에 들어있는 세션들(sessions라는 hashset)
	*  		chatservice의 sendMessage수행
	*
	*
	* 	request
	* 	{
		  "type":"TALK",
		  "roomId":"b0bfad07-dd10-45a4-b1a7-be0a46e4b293",
		  "sender":"사용자1",
		  "message":"안녕"
		}
	*
	* 	response
	*
	*
	*
	* */
}
