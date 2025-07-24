package bookmanager.dao;

import bookmanager.model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BookDao {

    void addBook(Book book) throws SQLException;

    Book getBookById(int id) throws SQLException;

    Book getBookByIsbn(String isbn) throws SQLException;

    List<Book> getAllBooks();

    void updateBook(Book book);

    boolean updateBookAvailability(int bookId, boolean isAvailable, Connection conn);

    void deleteBook(int bookId);

}
