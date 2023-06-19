package com.sparta.spartaboard.service;

import com.sparta.spartaboard.dto.BoardRequestDto;
import com.sparta.spartaboard.dto.BoardResponseDto;
import com.sparta.spartaboard.entity.Board;
import com.sparta.spartaboard.repository.BoardRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

//Service
//사용자의 요구사항을 처리(비즈니스로직) 하는 서비스 코드
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(JdbcTemplate jdbcTemplate) {

        this.boardRepository = new BoardRepository(jdbcTemplate);
    }


    public BoardResponseDto createPost(BoardRequestDto requestDto) {
        //RequestDto -> Entity (데이터베이스에 저장)
        Board board = new Board(requestDto); // requestDto로 클라이언트에서 받아온 데이터
        //board 클래스 객체 하나가 데이터베이스의 한줄, 한row다

        // DB 저장

        Board savePost = boardRepository.save(board);
        //Entity - > ResponseDto
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);

        return boardResponseDto;
    }

    public List<BoardResponseDto> getPost() {
        //DB조회
        return boardRepository.findAll();

    }

    public Long updatePost(Long id, BoardRequestDto requestDto) {

        Board board = boardRepository.findById(id);
        if (board != null) {
            //게시글 내용 수정
            boardRepository.update(id,requestDto);

            return id;
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다");
        }
    }

    public Long selectedPost(Long id) {
        Board board = boardRepository.findById(id);
        if (board != null) {
            return id;
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다");
        }
    }

    public Long deletePost(Long id) {

        Board board = boardRepository.findById(id);
        if (board != null) {
            //게시글 삭제
            boardRepository.delete(id);
            return id;
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다");
        }
    }

}
