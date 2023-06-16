package com.sparta.spartaboard.controller;

import com.sparta.spartaboard.dto.BoardRequestDto;
import com.sparta.spartaboard.dto.BoardResponseDto;
import com.sparta.spartaboard.service.BoardService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController  //모든 메서드에 @ResponseBody 애너테이션 추가됨 Json 데이터 요청
@RequestMapping("/api")
public class BoardController {

    private final JdbcTemplate jdbcTemplate;
    public BoardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate; }
    //jdbcTemplate을 이용한 DB 저장

    //게시글 생성하기
    @PostMapping("/post")
    public BoardResponseDto createBoard(@RequestBody BoardRequestDto requestDto) {
        //html body 부분에 json 형태로 데이터 넘어옴 RequestBOdy
        //RequestDto 클래스 로 body에 데이터를 받음
        BoardService boardService = new BoardService(jdbcTemplate);
        return boardService.createPost(requestDto);
    }

    //게시글 조회하기
    @GetMapping("/post")
    public List<BoardResponseDto> getPost() { //post가 여러개이기 때문에  리스트형식으로 반환
        BoardService boardService = new BoardService(jdbcTemplate);
        return boardService.getPost();
    }

    //선택된 게시글 조회
    @GetMapping("/post/{id}")
    public Long selectedPost(@PathVariable Long id) {
        BoardService boardService = new BoardService(jdbcTemplate);
        return boardService.selectedPost(id);
    }

    //게시글 수정하기
    @PutMapping("/post/{id}")
    public Long updatePost(@PathVariable Long id, @RequestBody BoardRequestDto requestDto) {
        //@PathVariable 서버에 보내려는 데이터를 URL 경로에 추가 ,
        // {id}에 선언한 변수명과 변수타입을 선언Long id , body의 json형식으로 requestbody에  해당 경로의 데이터를 받아온다
        BoardService boardService = new BoardService(jdbcTemplate);
        return boardService.updatePost(id, requestDto);
    }
    @DeleteMapping("/post/{id}")
    public Long deletePost(@PathVariable Long id) {   //삭제는 id만 받으면 됨 body 필요없음
        BoardService boardService = new BoardService(jdbcTemplate);
        return boardService.deletePost(id);
    }
}



