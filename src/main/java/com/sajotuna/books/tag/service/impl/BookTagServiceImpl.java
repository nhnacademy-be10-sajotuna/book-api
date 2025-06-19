package com.sajotuna.books.tag.service.impl;

import com.sajotuna.books.tag.controller.request.BookTagRequest;
import com.sajotuna.books.tag.controller.response.BookTagResponse;
import com.sajotuna.books.book.exception.BookNotFoundException;
import com.sajotuna.books.tag.exception.TagAlreadyExistsException;
import com.sajotuna.books.tag.exception.TagNotFoundException;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.tag.repository.BookTagRepository;
import com.sajotuna.books.tag.repository.TagRepository;
import com.sajotuna.books.tag.service.BookTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookTagServiceImpl implements BookTagService {

    private final BookRepository bookRepository;
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;

    @Override
    public BookTagResponse addTagToBook(BookTagRequest request){
        Book book = bookRepository.findById(request.isbn())
                .orElseThrow(() -> new BookNotFoundException(request.isbn()));
        Tag tag = tagRepository.findById(request.tagId())
                .orElseThrow(() -> new TagNotFoundException(request.tagId()));

        boolean exists = bookTagRepository.findByBook(book).stream()
                .anyMatch(bt ->bt.getTag().equals(tag));
        if(exists){
            throw new TagAlreadyExistsException(tag.getId(),book.getIsbn());
        }

        BookTag bookTag = new BookTag(tag,book);
        return BookTagResponse.from(bookTagRepository.save(bookTag));
    }

    @Override
    public void removeTagFromBook(BookTagRequest request){
        Book book = bookRepository.findById(request.isbn())
                .orElseThrow(() -> new BookNotFoundException(request.isbn()));
        Tag tag = tagRepository.findById(request.tagId())
                .orElseThrow(() -> new TagNotFoundException(request.tagId()));

        List<BookTag> bookTags = bookTagRepository.findByBook(book).stream()
                .filter(bt -> bt.getTag().equals(tag)).collect(Collectors.toList());

        bookTagRepository.deleteAll(bookTags);
    }

    @Override
    public List<BookTagResponse> getTagsByBook(String isbn){
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        return bookTagRepository.findByBook(book).stream()
                .map(BookTagResponse::from)
                .collect(Collectors.toList());
    }
}
