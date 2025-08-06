package bookmanager.dao;

import bookmanager.model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BookDao {

    void addBook(Book book) throws SQLException;

    void incrementBookCopies(int bookId, int count, Connection conn) throws SQLException;

    void decrementBookCopies(int bookId, int count, Connection conn) throws SQLException;

    void incrementAvailableCopies(int bookId, int count, Connection conn) throws SQLException;

    void decrementAvailableCopies(int bookId, int count, Connection conn) throws SQLException;

    Book getBookById(int id) throws SQLException;

    Book getBookByIsbn(String isbn) throws SQLException;

    List<Book> getBooksByAuthor(String author) throws SQLException;

    List<Book> getBooksByTitle(String title) throws SQLException;

    List<Book> getBooksByKeyword(String keyword) throws SQLException;

    List<Book> getAllBooks() throws SQLException;

    void updateBook(Book book) throws SQLException;

    void deleteBook(int bookId) throws SQLException;
}
