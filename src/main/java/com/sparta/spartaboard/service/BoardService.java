package com.sparta.spartaboard.service;

import com.sparta.spartaboard.dto.BoardRequestDto;
import com.sparta.spartaboard.dto.BoardResponseDto;
import com.sparta.spartaboard.entity.Board;
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
    private final JdbcTemplate jdbcTemplate;

    public BoardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public BoardResponseDto createPost(BoardRequestDto requestDto) {
        //RequestDto -> Entity (데이터베이스에 저장)
        Board board = new Board(requestDto); // requestDto로 클라이언트에서 받아온 데이터

        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO board (username, contents) VALUES (?, ?)";
        //이름과 내용은 항상 바뀌기 때문에 동적으로 처리하기위해 ? 로 넣음
        jdbcTemplate.update(con -> {
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

    public List<BoardResponseDto> getPost() {
        //DB조회
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

    public Long updatePost(Long id, BoardRequestDto requestDto) {
        Board board = findById(id);
        if (board != null) {
            //게시글 내용 수정
            String sql = "UPDATE board SET username = ?, contents = ? WHERE id = ?";
            jdbcTemplate.update(sql, requestDto.getUsername(), requestDto.getContents(), id);

            return id;
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다");
        }
    }

    public Long selectedPost(Long id) {
        Board board = findById(id);
        if (board != null) {
            return id;
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다");
        }
    }


    public Long deletePost(Long id) {
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
            if (resultSet.next()) {
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
