

CREATE DATABASE IF NOT EXISTS bookmanager;
-- src/main/resources/schema.sql 파일 내용 예시

CREATE TABLE IF NOT EXISTS Books (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL, -- ISBN은 여전히 UNIQUE 유지!
    total_copies INT DEFAULT 1,      -- 총 보유 권수
    available_copies INT DEFAULT 1   -- 현재 대출 가능한 권수
    );

CREATE TABLE IF NOT EXISTS Members (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    contact_info VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS Loans (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     book_id INT NOT NULL,
                                     member_id INT NOT NULL,
                                     loan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     return_date TIMESTAMP,
                                     FOREIGN KEY (book_id) REFERENCES Books(id),
    FOREIGN KEY (member_id) REFERENCES Members(id)
    );