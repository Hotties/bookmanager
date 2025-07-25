package bookmanager.service;

import bookmanager.model.Book;

import java.sql.SQLException;
import java.util.List;

public interface BookService {

    void registerBook(Book book) throws SQLException;

    Book getBookDetails(int Id) throws SQLException;

    Book getBookDetailsByISBN(String ISBN) throws SQLException;

    List<Book> searchBooks(String keyword) throws  SQLException;

    void updateBookDetails(Book book) throws SQLException;

    void removeBook(int Id) throws SQLException;
}
