package bookmanager.dao;

import bookmanager.model.Book;
import bookmanager.util.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String isbn = rs.getString("isbn");
        boolean  available = rs.getBoolean("is_available");
        return  new Book(id, title, author, isbn, available);
    }
    @Override
    public void addBook(Book book) throws SQLException {
        String query = "INSERT INTO Books(title,author,isbn,is_available) VALUES(?,?,?,?)";
        // try-with-resources 구문 시작
        try (Connection conn = ConnectionPoolManager.getConnection(); // 1. 여기서 Connection을 얻음
             PreparedStatement pstmt = conn.prepareStatement(query)) { // 2. 여기서 PreparedStatement를 얻음

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setBoolean(4, book.isAvailable());

            pstmt.executeUpdate();

        } // try 블록이 끝나면 conn.close()와 pstmt.close()가 자동으로 호출됩니다.
        // Connection.close()는 커넥션 풀에 연결을 반환합니다.
        catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Book getBookById(int id) throws SQLException {
        Book book = null;
        String query = "SELECT * FROM Books WHERE id=?";

        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    book = createBookFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw e;
        }
        return book;
    }

    @Override
    public Book getBookByIsbn(String isbn) throws SQLException {
        Book book = null;
        String query = "SELECT * FROM Books WHERE isbn=?";

        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, isbn);
            //반환되는 query를 읽는 과정

            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    book = createBookFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw e;
        }
        return book;
    }

    @Override
    public Book getBookByAuthor(String author) throws SQLException {

        Book book = null;
        String query = "SELECT * FROM Books WHERE author=?";

        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, author);
            //반환되는 query를 읽는 과정

            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    book = createBookFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw e;
        }
        return book;
    }

    @Override
    public Book getBookByTitle(String title) throws SQLException {

        Book book = null;
        String query = "SELECT * FROM Books WHERE title=?";

        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            //반환되는 query를 읽는 과정

            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    book = createBookFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw e;
        }
        return book;
    }

    @Override
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT id,title,author,isbn,is_available FROM Books";
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();// 쿼리를 실행하고 ResultSet을 얻습니다.

            // ResultSet의 모든 행을 순회합니다.
            while (rs.next()) {
                Book book = createBookFromResultSet(rs);
                // 생성된 Book 객체를 리스트에 추가합니다.
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw e;
        }
        return books;
    }

    @Override
    public void updateBook(Book book) throws SQLException {
        String query = "UPDATE Books SET title=?,author=?,isbn=?,is_available=? WHERE id=?";
        try(Connection conn = ConnectionPoolManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setBoolean(4, book.isAvailable());
            pstmt.setInt(5, book.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void updateBookAvailability(int bookId, boolean isAvailable, Connection conn) throws SQLException {

        String query = "UPDATE Books SET is_available=? WHERE id=?";

        try(PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error changing availability book: " + e.getMessage());

            throw e;
        }
    }

    @Override
    public void deleteBook(int bookId) throws SQLException {

        String query = "DELETE FROM Books WHERE id=?";
        try(Connection conn = ConnectionPoolManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            throw e;
        }

    }
}
