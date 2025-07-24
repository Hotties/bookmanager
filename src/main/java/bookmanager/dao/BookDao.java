package bookmanager.dao;

import bookmanager.model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BookDao {

    void addBook(Book book) throws SQLException;

    Book getBookById(int id) throws SQLException;

    Book getBookByIsbn(String isbn) throws SQLException;

    List<Book> getAllBooks() throws SQLException;

    void updateBook(Book book) throws SQLException;

    void updateBookAvailability(int bookId, boolean isAvailable, Connection conn) throws SQLException;

    void deleteBook(int bookId) throws SQLException;

}
