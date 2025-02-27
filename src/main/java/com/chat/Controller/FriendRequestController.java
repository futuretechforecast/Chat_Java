package com.chat.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chat.Model.FriendRequest;
import com.chat.Service.FriendRequestService;

@RestController
@RequestMapping("/api/chat")
public class FriendRequestController {

	@Autowired
	private FriendRequestService friendRequestService;

	@PostMapping("/sendFriendRequest")
	public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequest request) {
		try {

			String responseMessage = friendRequestService.sendFriendRequest(request);
			return ResponseEntity.ok(responseMessage);
		} catch (IllegalArgumentException e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {

			return ResponseEntity.status(500).body("An error occurred while sending the friend request.");
		}
	}

	@PutMapping("/acceptOrReject")
	public ResponseEntity<String> updateStatus(@RequestBody FriendRequest request) {
		try {
			if (!request.getStatus().equalsIgnoreCase("accepted")
					&& !request.getStatus().equalsIgnoreCase("rejected")) {
				return ResponseEntity.badRequest().body("Invalid status. Status must be 'accepted' or 'rejected'.");
			}

			String responseMessage = friendRequestService.updateFriendRequestStatus(request);
			return ResponseEntity.ok(responseMessage);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("An error occurred while updating the friend request status.");
		}
	}

	@GetMapping("/getReceivedList")
	public List<Map<String, Object>> getReceivedList(@RequestParam String receiverId) {
		try {
			return friendRequestService.getReceivedList(receiverId);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
			return List.of(Map.of("error", "Failed to fetch user list"));
		}
	}

	@GetMapping("/getSendList")
	public List<Map<String, Object>> getUserList(@RequestParam String senderId) {
		try {
			return friendRequestService.getSendList(senderId);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
			return List.of(Map.of("error", "Failed to fetch user list"));
		}
	}
}
