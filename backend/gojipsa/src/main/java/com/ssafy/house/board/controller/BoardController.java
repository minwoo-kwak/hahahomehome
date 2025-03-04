package com.ssafy.house.board.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.annotation.AuthRequired;
import com.ssafy.house.board.model.dto.BoardDto;
import com.ssafy.house.board.model.service.BoardService;
import com.ssafy.house.util.BoardPageConstant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/v1/board")
@Api(tags= {"공지사항 컨트롤러 API V1"})
public class BoardController {

	// Service
	private final BoardService boardService;

	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	// 전체 게시글 가져오기
	@ApiOperation(value = "공지사항 정보 목록 가져오기", notes = "pagination을 적용한 공지사항 글 목록을 가져옵니다. page 번호를 Query String 형식으로 받는다. data: 글 목록 데이터, pageInfo: pagination 정보")
	@ResponseBody
	@GetMapping(path = "/list")
	public ResponseEntity<?> listBoard(@RequestParam(value = "page", required = false) @ApiParam(value = "페이지 번호", required = false) String pageNo) {

		HashMap<String, Object> hMap = new HashMap<>();

		// pagination이 적용된 글 목록 데이터 리스트
		List<BoardDto> boardList = (List<BoardDto>) boardService.getAllBoard(pageNo);

		// 페이지 정보를 저장할 HashMap
		HashMap<String, Object> pageInfo = new HashMap<>();

		int pgno = pageNo == null ? 1 : Integer.parseInt(pageNo);

		// pagination에 필요한 정보
		// 1. 총 페이지 개수 (totalPageCnt)
		// 전체 컨텐츠 개수
		long totalBoardCnt = boardService.countBoard();
		long totalPageCnt = (long) Math.ceil((totalBoardCnt * 1.0) / BoardPageConstant.LIST_SIZE);
		pageInfo.put("totalPageCnt", totalPageCnt);

		// 2. 화면에 보여질 페이지의 첫 번째 페이지 번호
		long startPage = ((pgno - 1) * BoardPageConstant.LIST_SIZE) + 1;
		pageInfo.put("startPage", startPage);

		// 3. 화면에 보여질 페이지의 마지막 페이지 번호
		long lastPage = (pgno) * BoardPageConstant.LIST_SIZE;
		if (lastPage > totalPageCnt)
			lastPage = totalPageCnt;
		pageInfo.put("lastPage", lastPage);

		// Navigation Bar에 보여질 페이지 개수
		pageInfo.put("navigationSize", BoardPageConstant.NAVIGATION_SIZE);

		// 게시글 정보와 pagination 정보를 담는다.
		hMap.put("data", boardList);
		hMap.put("pageInfo", pageInfo);

		return ResponseEntity.ok().body(hMap);

	}

	// 상세 게시글 가져오기
	@ResponseBody
	@GetMapping("/{id}")
	@ApiOperation(value="공지사항 상세 정보 받기",notes="아이디로 공지사항 상세 정보 확인하는 API")
	public ResponseEntity<Map<String, Object>> detailBoard(@PathVariable("id") @ApiParam(value = "게시글 아이디", required = true) int id) {

		BoardDto boardInfo = (BoardDto) boardService.getDetailBoard(id);

		// board_no가==id인 게시글을 찾을 수 없는 경우
		if (boardInfo == null) {
			return ResponseEntity.notFound().build();
		}
		
		
		
		// 해당 게시글이 있는 경우
		Map<String, Object> result = new HashMap<>();
		result.put("data", boardInfo);
		return ResponseEntity.ok().body(result);
	}
	
	@ResponseBody
	@PutMapping("/hit/{id}")
	@ApiOperation(value="조회수 증가",notes="아이디로 조회수가 증가하는 API")
	 public ResponseEntity<Map<String, Object>> increaseHit(@PathVariable("id") @ApiParam(value = "게시글 아이디", required = true) int id) {
		boardService.increaseHit(id);
		
		return ResponseEntity.ok().build();
	}
	
	// 게시글 등록하기
	@ResponseBody
	@AuthRequired
	@PostMapping("/write")
	@ApiOperation(value="게시글 등록하기",notes="게시글을 등록하는 API")
	public ResponseEntity<Map<String, Object>> writeBoard(
			@ApiParam(value = "등록할 글 정보", required = true) @RequestBody BoardDto boardDto, HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		String userId;
		try {
			userId = validAdmin(request);
			boardDto.setUser_id(userId);
			System.out.println("write notice boardDto : " + boardDto);
			if (userId == null || userId.equals("")) {
				System.out.println("실패");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("write notice boardDto : " + boardDto);

		int resultCode = boardService.writeBoard(boardDto);

		// 게시글이 생성된 경우
		if (resultCode == 1) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	// 게시글 수정하기
	@ApiOperation(value = "게시판 글수정하기", notes = "수정할 게시글 정보를 입력한다. 그리고 DB수정 성공여부에 따라 'success' 또는 'fail' 문자열을 반환한다.", response = String.class)
	@ResponseBody
	@AuthRequired
	@PutMapping("/modify")
	public ResponseEntity<Map<String, Object>> modifyBoard(
			@RequestBody @ApiParam(value = "수정할 글정보", required = true) BoardDto boardDto) {
		System.out.println(boardDto);

		int resultCode = boardService.modifyBoard(boardDto);

		// 게시글이 수정된 경우
		if (resultCode == 1) {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
	}

	// 게시글 삭제하기
	@ResponseBody
	@AuthRequired
	@DeleteMapping("delete/{boardNo}")
	@ApiOperation(value="게시글 삭제하기",notes="게시글 번호로 게시글을 삭제하는 API")
	public ResponseEntity<Map<String, Object>> deleteBoard(@PathVariable("boardNo") @ApiParam(value = "삭제할 글 아이디", required = true) int boardNo) {

		int resultCode = boardService.deleteBoard(boardNo);

		// 게시글이 삭제된 경우
		if (resultCode == 1) {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/**
	 * 어드민인지 파악하는 메서드
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	public String validAdmin(HttpServletRequest request) throws ParseException {
		System.out.println("validAdmin 실행");
		
		String authorization = request.getHeader("Authorization");
		if (authorization == null || authorization.equals("")) {
			System.out.println("validAdmin Authorization NULL");
			return null;
		}

		String[] chunks = authorization.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));

		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(payload);
		String userId = (String) jsonObject.get("userId");
		String grad = (String) jsonObject.get("grad");
		System.out.println("validAdmin grad  == " + grad);

		if (grad.equals("관리자")) {
			return userId;
		} else {
			return null;
		}

	}

}
