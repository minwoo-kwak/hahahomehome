package com.ssafy.house.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.annotation.AuthRequired;
import com.ssafy.house.user.model.dto.User;
import com.ssafy.house.user.model.service.UserService;
import com.ssafy.house.util.JWTUtil;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
	private final UserService userService;
	private final JWTUtil jwtUtil;

	public UserController(UserService userService, JWTUtil jwtUtil) {
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * 로그인
	 * @param requestUser 로그인 아이디, 비밀번호 요청 정보
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody User requestUser) throws UnsupportedEncodingException {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		User validUser = userService.login(requestUser);
		
		System.out.println(validUser);

		// 해당 사용자가 없을 경우
		if (validUser == null) {
			resultMap.put("message", "아이디와 패스워드를 확인해주세요");

			return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.UNAUTHORIZED);
		}

		resultMap.put("Authorization", jwtUtil.createToken(validUser.getUserId()));

		// 사용자가 일치하면 그 사용자를 반환
		return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.CREATED);
	}

	@AuthRequired
	@GetMapping("/mypage")
	public ResponseEntity<User> myPage(HttpServletRequest request) throws ParseException {
		String authorization = request.getHeader("Authorization");
		
		String[] chunks = authorization.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(payload);
		String userId = (String) jsonObject.get("userId");
		
		System.out.println(userId);
		
		User user = userService.myPage(userId);
		
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}
}
