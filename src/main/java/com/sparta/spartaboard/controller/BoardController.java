package com.sparta.spartaboard.controller;

import com.sparta.spartaboard.dto.BoardRequestDto;
import com.sparta.spartaboard.dto.BoardResponseDto;
import com.sparta.spartaboard.entity.Board;
import com.sparta.spartaboard.service.BoardService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController  //모든 메서드에 @ResponseBody 애너테이션 추가됨 Json 데이터 요청
@RequestMapping("/api")
public class BoardController {

    private final JdbcTemplate jdbcTemplate;

    public BoardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }  //jdbcTemplate을 이용한 DB 저장

    //jdbc template 사용 대신 Map 과 List로 데이터를 저장함

    //게시글 생성하기
    @PostMapping("/post")
    public BoardResponseDto createBoard(@RequestBody BoardRequestDto requestDto) {
        //html body 부분에 json 형태로 데이터 넘어옴 RequestBOdy
        //RequestDto 클래스 로 body에 데이터를 받음


        //RequestDto -> Entity (데이터베이스에 저장)
        Board board = new Board(requestDto); // requestDto로 클라이언트에서 받아온 데이터

        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO board (username, contents) VALUES (?, ?)";
        //이름과 내용은 항상 바뀌기 때문에 동적으로 처리하기위해 ? 로 넣음
        jdbcTemplate.update( con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, board.getUsername());
                    preparedStatement.setString(2, board.getContents());
                    return preparedStatement;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        board.setId(id);

        //Entity - > ResponseDto
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);

        return boardResponseDto;
    }

    //게시글 조회하기
    @GetMapping("/post")
    public List<BoardResponseDto> getPost() { //post가 여러개이기 때문에  리스트형식으로 반환

        String sql = "SELECT * FROM board";

        return jdbcTemplate.query(sql, new RowMapper<BoardResponseDto>() {
            @Override
            public BoardResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                // SQL 의 결과로 받아온 Memo 데이터들을 MemoResponseDto 타입으로 변환해줄 메서드
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String contents = rs.getString("contents");
                return new BoardResponseDto(id, username, contents);
            }
        });
    }
    @GetMapping("/post/{id}")
    public Long selectedPost(@PathVariable Long id) {
        Board board = findById(id);
        if(board != null){
            return id;
        }else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다");
        }
    }

    //게시글 수정하기
    @PutMapping("/post/{id}")
    public Long updatePost(@PathVariable Long id, @RequestBody BoardRequestDto requestDto) {
        //@PathVariable 서버에 보내려는 데이터를 URL 경로에 추가 ,
        // {id}에 선언한 변수명과 변수타입을 선언Long id , body의 json형식으로 requestbody에  해당 경로의 데이터를 받아온다

        Board board = findById(id);
        if(board != null){
            //게시글 내용 수정
            String sql = "UPDATE board SET username = ?, contents = ? WHERE id = ?";
            jdbcTemplate.update(sql, requestDto.getUsername(), requestDto.getContents(), id);

            return id;
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다");
        }
    }

    @DeleteMapping("/post/{id}")
    public Long deletePost(@PathVariable Long id) {   //삭제는 id만 받으면 됨 body 필요없음

        Board board = findById(id);
        if (board != null) {
            //게시글 삭제
            String sql = "DELETE FROM board WHERE id = ?";
            jdbcTemplate.update(sql, id);

            return id;
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다");
        }
    }
        private Board findById(Long id) {
            // DB 조회
            String sql = "SELECT * FROM board WHERE id = ?";

            return jdbcTemplate.query(sql, resultSet -> {
                if(resultSet.next()) {
                    Board board = new Board();
                    board.setUsername(resultSet.getString("username"));
                    board.setContents(resultSet.getString("contents"));
                    return board;
                } else {
                    return null;
                }
            }, id);

    }
}



