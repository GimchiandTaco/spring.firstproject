package com.sparta.spartaboard.controller;

import com.sparta.spartaboard.dto.BoardRequestDto;
import com.sparta.spartaboard.dto.BoardResponseDto;
import com.sparta.spartaboard.entity.Board;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController  //모든 메서드에 @ResponseBody 애너테이션 추가됨 Json 데이터 요청
@RequestMapping("/api")
public class BoardController {

    private final Map<Long, Board> postList = new HashMap<>();
    //데이터 저장소 대신에 넣음 Key 에 id , value에 실제 데이터 Board 객체를 넣음

    //게시글 생성하기
    @PostMapping("/post")
    public BoardResponseDto createBoard(@RequestBody BoardRequestDto requestDto) {
        //html body 부분에 json 형태로 데이터 넘어옴 RequestBOdy
        //RequestDto 클래스 로 body에 데이터를 받음

        //RequestDto -> Entity (데이터베이스에 저장)
        Board board = new Board(requestDto); // requestDto로 클라이언트에서 받아온 데이터

        //Post Max ID check
        Long maxId = postList.size() > 0 ? Collections.max(postList.keySet()) + 1 : 1;
        board.setId(maxId);
        //postList.keySet을 호출하면 위에 Map에 있는 Long 값을 가져옴
        // Map 자료구조에 데이터가 하나도 없으면 : 1 << 1을 가져온다 (중복 될 일이 없기에 1부터 시작)
        // 중복을 방지 하기위해 + 1

        // DB 저장
        postList.put(board.getId(), board);


        //Entity - > ResponseDto
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);

        return boardResponseDto;
    }

    //게시글 조회하기
    @GetMapping("/post")
    public List<BoardResponseDto> getPost() { //post가 여러개이기 때문에  리스트형식으로 반환
        //Map To List
        List<BoardResponseDto> responselist = postList.values().stream()
                .map(BoardResponseDto::new).toList();
        //stream은 values 여러개 나오는걸 for문처럼 돌리고
        // .map 을 통해  BoardResponseDto::new 생성자를 수행하면서 글을 모아서 list로 바꿈
        //for문으로도 가능
        return responselist;

    }
}
