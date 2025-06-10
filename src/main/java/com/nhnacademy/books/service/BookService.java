package com.nhnacademy.books.service;

import com.nhnacademy.books.model.Book;
import com.nhnacademy.books.model.Category;
import com.nhnacademy.books.repository.BookRepository;
import com.nhnacademy.books.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository; // 카테고리 조회용

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Book registerBook(String isbn, String title, String author, String publisher, LocalDate publicationDate,
                             double originalPrice, double sellingPrice, boolean giftWrappingAvailable,
                             List<Long> categoryIds, List<String> tags) {
        if (bookRepository.existsById(isbn)) {
            throw new IllegalArgumentException("이미 존재하는 ISBN입니다: " + isbn);
        }

        Book newBook = new Book(isbn, title, author, publisher, publicationDate, originalPrice, sellingPrice);
        newBook.setGiftWrappingAvailable(giftWrappingAvailable);

        // 카테고리 설정
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + categoryId));
                categories.add(category);
            }
            if (categories.size() > 10) {
                throw new IllegalArgumentException("도서는 최대 10개의 카테고리에만 속할 수 있습니다.");
            }
            newBook.setCategories(categories);
        }

        // 태그 설정
        if (tags != null && !tags.isEmpty()) {
            newBook.setTags(new HashSet<>(tags)); // Set으로 변환하여 중복 제거
        }

        return bookRepository.save(newBook);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn);
    }

    public List<Book> searchBooksByTitle(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Book> searchBooksByAuthor(String keyword) {
        return bookRepository.findByAuthorContainingIgnoreCase(keyword);
    }

    @Transactional
    public Book updateBookDetails(String isbn, String title, String tableOfContents, String description,
                                  String author, String publisher, LocalDate publicationDate,
                                  double originalPrice, double sellingPrice, boolean giftWrappingAvailable,
                                  List<Long> categoryIds, List<String> tags) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("업데이트할 도서를 찾을 수 없습니다. ISBN: " + isbn));

        book.setTitle(title);
        book.setTableOfContents(tableOfContents);
        book.setDescription(description);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setPublicationDate(publicationDate);
        book.setOriginalPrice(originalPrice);
        book.setSellingPrice(sellingPrice);
        book.setGiftWrappingAvailable(giftWrappingAvailable);

        // 카테고리 업데이트
        book.getCategories().clear(); // 기존 카테고리 모두 제거
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> newCategories = new HashSet<>();
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + categoryId));
                newCategories.add(category);
            }
            if (newCategories.size() > 10) {
                throw new IllegalArgumentException("도서는 최대 10개의 카테고리에만 속할 수 있습니다.");
            }
            book.setCategories(newCategories);
        }

        // 태그 업데이트
        book.getTags().clear(); // 기존 태그 모두 제거
        if (tags != null && !tags.isEmpty()) {
            book.setTags(new HashSet<>(tags));
        }

        return bookRepository.save(book); // 변경된 엔티티 저장
    }

    @Transactional
    public void deleteBook(String isbn) {
        if (!bookRepository.existsById(isbn)) {
            throw new IllegalArgumentException("삭제할 도서를 찾을 수 없습니다. ISBN: " + isbn);
        }
        bookRepository.deleteById(isbn);
    }

    @Transactional
    public void likeBook(String isbn) {
        bookRepository.findById(isbn).ifPresentOrElse(book -> {
            book.incrementLikes();
            bookRepository.save(book); // 변경사항 저장
        }, () -> {
            throw new IllegalArgumentException("좋아요할 도서를 찾을 수 없습니다. ISBN: " + isbn);
        });
    }

    @Transactional
    public void unlikeBook(String isbn) {
        bookRepository.findById(isbn).ifPresentOrElse(book -> {
            book.decrementLikes();
            bookRepository.save(book); // 변경사항 저장
        }, () -> {
            throw new IllegalArgumentException("좋아요 취소할 도서를 찾을 수 없습니다. ISBN: " + isbn);
        });
    }
}