package com.sparta.spartaboard.repository;

import com.sparta.spartaboard.dto.BoardRequestDto;
import com.sparta.spartaboard.dto.BoardResponseDto;
import com.sparta.spartaboard.entity.Board;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
@Component
//Repository DB관리, DB CRUD 작업처리
public class BoardRepository {

    private final JdbcTemplate jdbcTemplate;
    public BoardRepository(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public Board save(Board board) {
        //DB저장 
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
        
        return board;
    }
    public List<BoardResponseDto> findAll() {
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
    public Board findById(Long id) {
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

    public void update(Long id, BoardRequestDto requestDto) {
        String sql = "UPDATE board SET username = ?, contents = ? WHERE id = ?";
        jdbcTemplate.update(sql, requestDto.getUsername(), requestDto.getContents(), id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM board WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
