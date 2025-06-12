package com.sajotuna.books.service.impl;

import com.sajotuna.books.dto.BookRequest;
import com.sajotuna.books.dto.BookResponse;
import com.sajotuna.books.exception.BookNotFoundException; // 변경
import com.sajotuna.books.exception.CategoryNotFoundException; // 변경
import com.sajotuna.books.exception.TagNotFoundException;
import com.sajotuna.books.model.Book;
import com.sajotuna.books.model.BookTag;
import com.sajotuna.books.model.Category;
import com.sajotuna.books.model.Tag;
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
        return bookRepository.findAllWithCategoriesAndTags().stream() // N+1 문제 방지를 위해 변경
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        return bookRepository.findByIdWithCategoriesAndTags(isbn) // N+1 문제 방지를 위해 변경
                .map(BookResponse::new)
                .orElseThrow(() -> new BookNotFoundException(isbn));
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

        if (bookRequest.getCategoryIds() != null && !bookRequest.getCategoryIds().isEmpty()) {
            Set<Category> categories = bookRequest.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new CategoryNotFoundException(categoryId)))
                    .collect(Collectors.toSet());
            book.setCategories(categories);
        } else {
            book.setCategories(new HashSet<>());
        }

        Book savedBook = bookRepository.save(book); // Save the book first to get it managed

        if (bookRequest.getTagIds() != null && !bookRequest.getTagIds().isEmpty()) {
            Set<BookTag> bookTags = new HashSet<>();
            for (Long tagId : bookRequest.getTagIds()) {
                Tag tag = tagService.getTagById(tagId) // Use tagService to get the Tag entity
                        .orElseThrow(() -> new TagNotFoundException(tagId));
                bookTags.add(new BookTag(tag, savedBook)); // Create BookTag with the savedBook
            }
            savedBook.getBookTags().addAll(bookTags);
            bookTagRepository.saveAll(bookTags); // Explicitly save BookTag entities
        }

        return new BookResponse(savedBook);
    }

    @Override
    public void incrementViewCount(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        book.setViewCount(book.getViewCount() + 1);
        bookRepository.save(book);
    }
}