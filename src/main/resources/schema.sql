

CREATE DATABASE IF NOT EXISTS bookmanager;
-- src/main/resources/schema.sql 파일 내용 예시

CREATE TABLE IF NOT EXISTS Books (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
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