package com.sajotuna.books.book.service.impl;


import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.exception.BookNotFoundException; // 변경
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import com.sajotuna.books.tag.repository.BookTagRepository;
import com.sajotuna.books.category.repository.CategoryRepository;
import com.sajotuna.books.book.service.BookService;
import com.sajotuna.books.category.domain.BookCategory;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.category.service.CategoryService;
import com.sajotuna.books.tag.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final BookSearchRepository bookSearchRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookResponse::new);
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        book.incrementViewCount();
        book.calculatePopularity();

        bookRepository.save(book); //db 반영
        bookSearchRepository.save(BookSearchDocument.from(book)); // Es 반영
        return new BookResponse(book);
    }

    @Override
    public void updateReviewInfo(String isbn, double rating) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        book.calculateRating(rating);
        book.incrementReviewCount();

        bookRepository.save(book); //  DB 반영

        // Elasticsearch에도 반영
        bookSearchRepository.save(BookSearchDocument.from(book));
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

    @Override
    public BookResponse updateBook(String isbn, BookCreateRequest request) {
        // 1. 해당 ISBN의 책이 존재하는지 확인
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        // 2. 도서 정보 업데이트 (Book 엔티티의 updateInfo 메서드 활용)
        book.updateInfo(request);

        // 3. 카테고리 업데이트 (기존 카테고리 삭제 후 새로 추가)
        book.getBookCategories().clear(); // 기존 카테고리 연결 제거
        if (request.getCategoryNames() != null && !request.getCategoryNames().isEmpty()) {
            List<Category> categories = categoryService.findOrCreateCategories(request.getCategoryNames());
            Set<BookCategory> newBookCategories = new HashSet<>();
            for (Category category : categories) {
                BookCategory bookCategory = new BookCategory();
                bookCategory.setBook(book);
                bookCategory.setCategory(category);
                newBookCategories.add(bookCategory);
            }
            book.setBookCategories(newBookCategories);
        }

        // 4. 태그 업데이트 (기존 태그 삭제 후 새로 추가)
        book.getBookTags().clear(); // 기존 태그 연결 제거
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            Set<Tag> tags = tagService.findOrCreateTags(request.getTagNames());
            Set<BookTag> newBookTags = new HashSet<>();
            for (Tag tag : tags) {
                BookTag bookTag = new BookTag(tag, book);
                newBookTags.add(bookTag);
            }
            book.setBookTags(newBookTags);
        }

        // 5. 도서 저장 (변경사항 반영)
        Book updatedBook = bookRepository.save(book);

        // 6. 응답 DTO 반환
        return new BookResponse(updatedBook);
    }

    // 도서 삭제 (추가된 부분)
    @Override
    public void deleteBook(String isbn) {
        // 1. 해당 ISBN의 책이 존재하는지 확인
        if (!bookRepository.existsById(isbn)) {
            throw new BookNotFoundException(isbn);
        }
        // 2. 도서 삭제
        bookRepository.deleteById(isbn);
    }
}