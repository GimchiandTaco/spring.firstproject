package com.sparta.spartaboard.entity;

import com.sparta.spartaboard.dto.BoardRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor //기본 생성자 생성

public class Board {
    private Long id;
    private String username;
    private String contents;


    public Board(BoardRequestDto requestDto) {
        //클라이언트에서 받아온 데이터를 넣어줘야함
        //Get 메서드를 사용해서 내용을 갖고온 후 Board 클래스 필드에 데이터를 넣으면서 객체 하나 생성

        this.contents = requestDto.getContents();
        this.username = requestDto.getUsername();

    }
}
