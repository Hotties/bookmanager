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
    public Book getBookById(int id) {
        Book book = null;
        String query = "SELECT * FROM Books WHERE id=?";

        try (Connection conn = ConnectionPoolManager.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            //반환되는 query를 읽는 과정
            book = getBook(book, pstmt);

        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return book;
    }

    @Override
    public Book getBookByIsbn(String isbn) {
        Book book = null;
        String query = "SELECT * FROM Books WHERE isbn=?";

        try (Connection conn = ConnectionPoolManager.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, isbn);
            //반환되는 query를 읽는 과정
            book = getBook(book, pstmt);

        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return book;
    }

    private Book getBook(Book book, PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {

                int bookId = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                boolean isAvailable = rs.getBoolean("is_available");

                book = new Book(bookId, title, author, isbn, isAvailable);
            }
        }
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT id,title,author,isbn,isavailable FROM Books";
        try (Connection conn = ConnectionPoolManager.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);

            ResultSet rs = pstmt.executeQuery();// 쿼리를 실행하고 ResultSet을 얻습니다.

            // ResultSet의 모든 행을 순회합니다.
            while (rs.next()) {
                // 각 행에서 데이터를 추출하여 새로운 Book 객체를 생성합니다.
                int bookId = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                boolean isAvailable = rs.getBoolean("is_available");
                Book book = new Book(bookId, title, author, isbn, isAvailable);
                // 생성된 Book 객체를 리스트에 추가합니다.
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return books;
    }

    @Override
    public void updateBook(Book book) {

    }

    @Override
    public boolean updateBookAvailability(int bookId, boolean isAvailable, Connection conn) {
        return false;
    }

    @Override
    public void deleteBook(int bookId) {

    }
}
