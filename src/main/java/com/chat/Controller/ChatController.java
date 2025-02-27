package com.chat.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chat.Model.ChatModel;
import com.chat.Service.ChatService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping("/messages/save")
	public ResponseEntity<String> saveMessage(@RequestBody ChatModel chatModel) {
		try {
			String messageId = chatService.saveMessage(chatModel);
			return ResponseEntity.ok("Message saved with ID: " + messageId);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error: " + e.getMessage());
		}
	}
	
//	@GetMapping("/getAllChat")
//		public ResponseEntity<String> getAllChat(@RequestParam String )
//		
	

}
