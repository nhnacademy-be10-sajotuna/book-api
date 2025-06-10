-- Category 테이블에 데이터 삽입 (부모 카테고리를 먼저 삽입)
INSERT INTO categories (id, name, parent_id) VALUES (1, 'IT 모바일', NULL);
INSERT INTO categories (id, name, parent_id) VALUES (2, '웹사이트', 1);
INSERT INTO categories (id, name, parent_id) VALUES (3, '소설', NULL);
INSERT INTO categories (id, name, parent_id) VALUES (4, '판타지', 3);
INSERT INTO categories (id, name, parent_id) VALUES (5, '자기계발', NULL);
INSERT INTO categories (id, name, parent_id) VALUES (6, '경제/경영', NULL);

-- Book 테이블에 데이터 삽입 (page_count와 image_url 값 추가)
-- 이미지가 없는 경우 NULL 또는 빈 문자열을 사용하고, pageCount는 적절한 값으로 채우세요.
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

-- book_tags 테이블에 데이터 삽입 (도서 태그)
INSERT INTO book_tags (book_isbn, tag) VALUES ('9788966262174', '자바');
INSERT INTO book_tags (book_isbn, tag) VALUES ('9788966262174', '프로그래밍');
INSERT INTO book_tags (book_isbn, tag) VALUES ('9788966262174', '초급');

INSERT INTO book_tags (book_isbn, tag) VALUES ('9791193132640', '게임');
INSERT INTO book_tags (book_isbn, tag) VALUES ('9791193132640', '가이드북');
INSERT INTO book_tags (book_isbn, tag) VALUES ('9791193132640', '판타지');

INSERT INTO book_tags (book_isbn, tag) VALUES ('9788932900010', '고전');
INSERT INTO book_tags (book_isbn, tag) VALUES ('9788932900010', '성장소설');

INSERT INTO book_tags (book_isbn, tag) VALUES ('9788965705353', '소프트웨어');
INSERT INTO book_tags (book_isbn, tag) VALUES ('9788965705353', '코드');
INSERT INTO book_tags (book_isbn, tag) VALUES ('9788965705353', '개발');

INSERT INTO book_tags (book_isbn, tag) VALUES ('9788950942509', '재테크');
INSERT INTO book_tags (book_isbn, tag) VALUES ('9788950942509', '자기계발');

-- book_categories 조인 테이블에 데이터 삽입 (책과 카테고리 연결)
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788966262174', 1); -- 이것이 자바다 -> IT 모바일
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788966262174', 2); -- 이것이 자바다 -> 웹사이트
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9791193132640', 4); -- 엘든 링 -> 판타지
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788932900010', 3); -- 데미안 -> 소설
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788965705353', 1); -- 클린 코드 -> IT 모바일
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788950942509', 5); -- 부의 추월차선 -> 자기계발
INSERT INTO book_categories (book_isbn, category_id) VALUES ('9788950942509', 6); -- 부의 추월차선 -> 경제/경영