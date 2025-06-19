package com.sajotuna.books.tag.service;

import com.sajotuna.books.tag.controller.request.BookTagRequest;
import com.sajotuna.books.tag.controller.response.BookTagResponse;

import java.util.List;

public interface BookTagService {
    BookTagResponse addTagToBook(BookTagRequest request);//태그 추가
    void removeTagFromBook(BookTagRequest request);//태그 제거
    List<BookTagResponse> getTagsByBook(String isbn);//태그 전제 조회
}
