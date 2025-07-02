// src/main/java/com/sajotuna/books/book/service/impl/BookServiceImpl.java
package com.sajotuna.books.book.service.impl;


import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.exception.BookNotFoundException;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.category.domain.BookCategory;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.category.service.CategoryService;
import com.sajotuna.books.tag.service.TagService;
import com.sajotuna.books.book.service.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // 추가
import org.springframework.data.domain.Pageable; // 추가
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final TagService tagService;


    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) { // List -> Page, Pageable 추가
        return bookRepository.findAll(pageable) // Pageable을 파라미터로 넘깁니다.
                .map(BookResponse::new); // Page<Book>를 Page<BookResponse>로 변환합니다.
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn)
                .map(BookResponse::new)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @Override
    public BookResponse createBook(BookCreateRequest request) {
        // 1. ISBN 중복 확인
        if (bookRepository.existsById(request.getIsbn())) {
            throw new IllegalArgumentException("이미 존재하는 ISBN입니다: " + request.getIsbn());
        }

        // 2. Book 엔티티 생성 및 기본 정보 설정
        Book book = new Book(
                request.getIsbn(),
                request.getTitle(),
                request.getAuthor(),
                request.getPublisher(),
                request.getPublicationDate(),
                request.getPageCount(),
                request.getImageUrl(),
                request.getDescription(),
                request.getOriginalPrice(),
                request.getSellingPrice(),
                request.getGiftWrappingAvailable(),
                request.getLikes()
        );

        // 3. 카테고리 처리
        if (request.getCategoryNames() != null && !request.getCategoryNames().isEmpty()) {
            List<Category> categories = categoryService.findOrCreateCategories(request.getCategoryNames());
            Set<BookCategory> bookCategories = new HashSet<>();
            for (Category category : categories) {
                BookCategory bookCategory = new BookCategory();
                bookCategory.setBook(book);
                bookCategory.setCategory(category);
                bookCategories.add(bookCategory);
            }
            book.setBookCategories(bookCategories);
        }


        // 4. 태그 처리
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            Set<Tag> tags = tagService.findOrCreateTags(request.getTagNames());
            Set<BookTag> bookTags = new HashSet<>();
            for (Tag tag : tags) {
                BookTag bookTag = new BookTag(tag, book);
                bookTags.add(bookTag);
            }
            book.setBookTags(bookTags);
        }

        // 5. 도서 저장
        Book savedBook = bookRepository.save(book);

        // 6. 응답 DTO 반환
        return new BookResponse(savedBook);
    }
}