package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.BookRequest;
import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.exception.BookNotFoundException; // 변경
import com.sajotuna.books.exception.CategoryNotFoundException; // 변경
import com.sajotuna.books.exception.TagNotFoundException;
import com.sajotuna.books.model.*;
import com.sajotuna.books.repository.BookRepository;
import com.sajotuna.books.repository.BookTagRepository;
import com.sajotuna.books.repository.CategoryRepository;
import com.sajotuna.books.service.BookService;
import com.sajotuna.books.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final CategoryRepository categoryRepository;
    private final TagService tagService;
    private final BookTagRepository bookTagRepository;




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
                .orElseThrow(() -> new BookNotFoundException(isbn)); // 예외 변경
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
                bookRequest.getOriginalPrice(),
                bookRequest.getSellingPrice(),
                bookRequest.getGiftWrappingAvailable(),
                bookRequest.getLikes()
        );

        if (bookRequest.getCategoryIds() != null && !bookRequest.getCategoryIds().isEmpty()) {
            for (Long categoryId : bookRequest.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CategoryNotFoundException(categoryId));
                BookCategory bookCategory = new BookCategory();
                bookCategory.setBook(book);
                bookCategory.setCategory(category);
                book.getBookCategories().add(bookCategory);
            }
        }

        if (bookRequest.getTagIds() != null && !bookRequest.getTagIds().isEmpty()) {
            Set<BookTag> tags = bookRequest.getTagIds().stream()
                    .map(id -> bookTagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id)))
                    .collect(Collectors.toSet());
            book.setBookTags(tags);
        }

        Book savedBook = bookRepository.save(book);
        return new BookResponse(savedBook);
    }
}