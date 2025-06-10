// src/main/java/com/sajotuna/books/service/impl/BookServiceImpl.java
package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.BookRequest;
import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.Category;
import com.sajotuna.books.model.Tag; // Tag 엔티티 임포트
import com.sajotuna.books.repository.BookRepository;
import com.sajotuna.books.repository.CategoryRepository;
import com.sajotuna.books.repository.TagRepository; // TagRepository 임포트
import com.sajotuna.books.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository; // TagRepository 주입

    public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository; // 주입
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn)
                .map(BookResponse::new)
                .orElse(null);
    }

    @Override
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = new Book(
                bookRequest.getIsbn(),
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getPublisher(),
                bookRequest.getPublicationDate(),
                bookRequest.getPageCount(),
                bookRequest.getImageUrl(),
                bookRequest.getDescription(),
                bookRequest.getTableOfContents(),
                bookRequest.getOriginalPrice(),
                bookRequest.getSellingPrice(),
                bookRequest.getGiftWrappingAvailable(),
                bookRequest.getLikes()
        );

        // 카테고리 설정 (기존과 동일)
        if (bookRequest.getCategoryIds() != null && !bookRequest.getCategoryIds().isEmpty()) {
            Set<Category> categories = bookRequest.getCategoryIds().stream()
                    .map(categoryRepository::findById)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toSet());
            book.setCategories(categories);
        }

        // 태그 설정 (Tag 엔티티 사용하도록 변경)
        if (bookRequest.getTagIds() != null && !bookRequest.getTagIds().isEmpty()) {
            Set<Tag> tags = bookRequest.getTagIds().stream()
                    .map(tagRepository::findById) // ID로 Tag 엔티티 조회
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toSet());
            book.setTags(tags);
        }

        Book savedBook = bookRepository.save(book);
        return new BookResponse(savedBook);
    }
}