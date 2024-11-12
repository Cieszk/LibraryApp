CREATE TABLE "user" (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(70) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    roles VARCHAR(50) NOT NULL
);

CREATE TABLE author (
    author_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    nationality VARCHAR(50) NOT NULL,
    biography TEXT
);

CREATE TABLE publisher (
    publisher_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    website VARCHAR(100),
    contact_number VARCHAR(20)
);

CREATE TABLE category (
    category_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE book
(
    book_id      BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    genre        VARCHAR(50),
    publish_year INT,
    isbn         VARCHAR(20) UNIQUE,
    language     VARCHAR(50),
    page_count   INT,
    description  TEXT,
    publisher_id BIGINT REFERENCES publisher (publisher_id)
);

-- Table that join 'book' and 'author'
CREATE TABLE book_author (
    book_id BIGINT REFERENCES book(book_id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES author(author_id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

-- Table that join 'book' and 'category'
CREATE TABLE book_category
(
    book_id     BIGINT REFERENCES book (book_id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES category (category_id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, category_id)
);

CREATE TABLE book_instance (
    book_instance_id BIGSERIAL PRIMARY KEY,
    book_id BIGINT REFERENCES book(book_id) ON DELETE CASCADE,
    book_status VARCHAR(20) NOT NULL
);

CREATE TABLE reservation (
    reservation_id BIGSERIAL PRIMARY KEY,
    reservation_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP,
    user_id BIGINT REFERENCES "user"(user_id) ON DELETE CASCADE,
    book_instance_id BIGINT UNIQUE REFERENCES book_instance(book_instance_id) ON DELETE CASCADE
);

CREATE TABLE book_loan (
    book_loan_id BIGSERIAL PRIMARY KEY,
    loan_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    fine_amount DOUBLE PRECISION,
    renew_count INT DEFAULT 0,
    user_id BIGINT REFERENCES "user"(user_id) ON DELETE CASCADE,
    book_instance_id BIGINT REFERENCES book_instance(book_instance_id) ON DELETE CASCADE
);

create table review(
    review_id BIGSERIAL PRIMARY KEY,
    rating INT CHECK(rating BETWEEN 1 AND 5),
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT REFERENCES "user"(user_id) ON DELETE CASCADE,
    book_id BIGINT REFERENCES book(book_id) ON DELETE CASCADE
);