-- Category 테이블에 데이터 삽입 (기존과 동일)
INSERT INTO categories (id, name, parent_id) VALUES (1, 'IT 모바일', NULL);
INSERT INTO categories (id, name, parent_id) VALUES (2, '웹사이트', 1);
INSERT INTO categories (id, name, parent_id) VALUES (3, '소설', NULL);
INSERT INTO categories (id, name, parent_id) VALUES (4, '판타지', 3);
INSERT INTO categories (id, name, parent_id) VALUES (5, '자기계발', NULL);
INSERT INTO categories (id, name, parent_id) VALUES (6, '경제/경영', NULL);

-- Book 테이블에 데이터 삽입 (기존과 동일)
INSERT INTO books (isbn, title, author, publisher, publication_date, page_count, image_url, description, table_of_contents, original_price, selling_price, gift_wrapping_available, likes) VALUES
    ('9788966262174', '이것이 자바다', '신용권', '한빛미디어', '2023-01-15', 864, 'https://image.aladin.co.kr/img/book/8966262174_w.jpg', '자바 프로그래밍의 기본부터 고급까지 다루는 종합 교재입니다.', 'Part 1. 자바 시작하기\nPart 2. 변수와 타입...', 35000.0, 31500.0, TRUE, 0);

INSERT INTO books (isbn, title, author, publisher, publication_date, page_count, image_url, description, table_of_contents, original_price, selling_price, gift_wrapping_available, likes) VALUES
    ('9791193132640', '엘든 링: 황금 나무의 그림자 공식 가이드', '프롬 소프트웨어', '블루인포', '2024-06-21', 400, 'https://image.aladin.co.kr/img/book/9791193132640_w.jpg', '엘든 링 DLC 황금 나무의 그림자 완벽 공략 가이드.', 'DLC 지역 탐험\n새로운 보스 전략...', 45000.0, 42750.0, TRUE, 5);

INSERT INTO books (isbn, title, author, publisher, publication_date, page_count, image_url, description, table_of_contents, original_price, selling_price, gift_wrapping_available, likes) VALUES
    ('9788932900010', '데미안', '헤르만 헤세', '민음사', '2019-03-01', 288, 'https://image.aladin.co.kr/img/book/8932900010_w.jpg', '헤르만 헤세의 대표작 중 하나.', '자아 탐색과 성장...', 12000.0, 10800.0, FALSE, 10);

INSERT INTO books (isbn, title, author, publisher, publication_date, page_count, image_url, description, table_of_contents, original_price, selling_price, gift_wrapping_available, likes) VALUES
    ('9788965705353', '클린 코드', '로버트 C. 마틴', '인사이트', '2013-12-24', 500, 'https://image.aladin.co.kr/img/book/8965705353_w.jpg', '소프트웨어 장인의 필독서.', '깨끗한 코드 작성법...', 28000.0, 25200.0, FALSE, 8);

INSERT INTO books (isbn, title, author, publisher, publication_date, page_count, image_url, description, table_of_contents, original_price, selling_price, gift_wrapping_available, likes) VALUES
    ('9788950942509', '부의 추월차선', '엠제이 드마코', '파이널샷', '2017-03-20', 440, 'https://image.aladin.co.kr/img/book/8950942509_w.jpg', '재정적 자유를 얻는 새로운 길.', '빠르게 부자 되는 법...', 16000.0, 14400.0, TRUE, 12);


-- TAGS 테이블에 데이터 삽입 (기존과 동일)
INSERT INTO tags (id, name) VALUES (1, '자바');
INSERT INTO tags (id, name) VALUES (2, '프로그래밍');
INSERT INTO tags (id, name) VALUES (3, '초급');
INSERT INTO tags (id, name) VALUES (4, '게임');
INSERT INTO tags (id, name) VALUES (5, '가이드북');
INSERT INTO tags (id, name) VALUES (6, '판타지');
INSERT INTO tags (id, name) VALUES (7, '고전');
INSERT INTO tags (id, name) VALUES (8, '성장소설');
INSERT INTO tags (id, name) VALUES (9, '소프트웨어');
INSERT INTO tags (id, name) VALUES (10, '코드');
INSERT INTO tags (id, name) VALUES (11, '개발');
INSERT INTO tags (id, name) VALUES (12, '재테크');
INSERT INTO tags (id, name) VALUES (13, '자기계발');


-- BOOK_TAGS_JOIN 조인 테이블에 데이터 삽입 (기존과 동일)
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788966262174', 1);
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788966262174', 2);
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788966262174', 3);

INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9791193132640', 4);
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9791193132640', 5);
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9791193132640', 6);

INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788932900010', 7);
-- --- 이 줄이 문제였습니다. 아래와 같이 INTO를 한 번만 써주세요. ---
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788932900010', 8);

INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788965705353', 9);
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788965705353', 10);
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788965705353', 11);

INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788950942509', 12);
INSERT INTO book_tags_join (book_isbn, tag_id) VALUES ('9788950942509', 13);

-- book_categories 조인 테이블에 데이터 삽입 (기존과 동일)
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788966262174', 1);
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788966262174', 2);
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9791193132640', 4);
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788932900010', 3);
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788965705353', 1);
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788950942509', 5);
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788950942509', 6);

--- 좋아요 기능 추가를 위한 데이터 ---

-- USERS 테이블에 샘플 사용자 데이터 삽입
INSERT INTO users (id, username) VALUES (1, 'user1');
INSERT INTO users (id, username) VALUES (2, 'user2');

-- LIKES 테이블에 샘플 좋아요 데이터 삽입 (선택 사항, 테스트 시 직접 추가하는 것이 더 유용할 수 있음)
-- INSERT INTO likes (user_id, book_isbn) VALUES (1, '9788966262174'); -- user1이 '이것이 자바다'에 좋아요
-- INSERT INTO likes (user_id, book_isbn) VALUES (1, '9788932900010'); -- user1이 '데미안'에 좋아요
-- INSERT INTO likes (user_id, book_isbn) VALUES (2, '9791193132640'); -- user2이 '엘든 링'에 좋아요