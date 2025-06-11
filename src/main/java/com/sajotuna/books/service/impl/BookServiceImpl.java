package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.BookRequest;
import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.Category;
import com.sajotuna.books.repository.BookRepository;
import com.sajotuna.books.repository.CategoryRepository;
import com.sajotuna.books.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
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

        // 카테고리 설정
        if (bookRequest.getCategoryIds() != null && !bookRequest.getCategoryIds().isEmpty()) {
            Set<Category> categories = bookRequest.getCategoryIds().stream()
                    .map(categoryRepository::findById)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toSet());
            book.setCategories(categories);
        }

        // 태그 설정: bookRequest.getTags() 대신 bookRequest.getTagIds()를 사용하고,
        // 필요에 따라 Long 타입을 String으로 변환합니다.
        if (bookRequest.getTagIds() != null && !bookRequest.getTagIds().isEmpty()) {
            // tagIds를 String으로 변환하여 Book의 tags 필드에 설정
            Set<String> tags = bookRequest.getTagIds().stream()
                    .map(String::valueOf) // Long을 String으로 변환
                    .collect(Collectors.toSet());
            book.setTags(tags);
        } else {
            book.setTags(new HashSet<>()); // tagIds가 null이거나 비어있으면 빈 Set으로 초기화
        }


        Book savedBook = bookRepository.save(book);
        return new BookResponse(savedBook);
    }
}