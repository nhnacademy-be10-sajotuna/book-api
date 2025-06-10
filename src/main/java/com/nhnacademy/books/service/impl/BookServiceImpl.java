package com.nhnacademy.books.service.impl;

import com.nhnacademy.books.dto.BookRequest;
import com.nhnacademy.books.dto.BookResponse;
import com.nhnacademy.books.model.Book;
import com.nhnacademy.books.model.Category;
import com.nhnacademy.books.repository.BookRepository;
import com.nhnacademy.books.repository.CategoryRepository;
import com.nhnacademy.books.service.BookService;
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
    public BookResponse getBookByIsbn(String isbn) { // 구현 추가
        return bookRepository.findById(isbn)
                .map(BookResponse::new)
                .orElse(null); // 책이 없으면 null 반환 (컨트롤러에서 404 처리)
    }

    @Override
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = new Book(
                bookRequest.getIsbn(),
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getPublisher(),
                bookRequest.getPublicationDate(),
                bookRequest.getPageCount(), // 추가
                bookRequest.getImageUrl(), // 추가
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

        // 태그 설정
        if (bookRequest.getTags() != null) {
            book.setTags(bookRequest.getTags());
        }

        Book savedBook = bookRepository.save(book);
        return new BookResponse(savedBook);
    }
}