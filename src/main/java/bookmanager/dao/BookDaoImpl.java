package bookmanager.dao;

import bookmanager.model.Book;
import bookmanager.util.ConnectionPoolManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {


    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        // totalCopies와 availableCopies 필드 추가에 맞게 수정
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String isbn = rs.getString("isbn");
        int totalCopies = rs.getInt("total_copies");
        int availableCopies = rs.getInt("available_copies");
        return new Book(id, title, author, isbn, totalCopies, availableCopies);

    }

    @Override
    public void addBook(Book book) throws SQLException {
        // total_copies와 available_copies 컬럼 추가
        String query = "INSERT INTO Books(title, author, isbn, total_copies, available_copies) VALUES(?,?,?,?,?)";

        try (Connection conn = ConnectionPoolManager.getConnection();
             // 자동 생성된 ID를 가져오기 위해 Statement.RETURN_GENERATED_KEYS 옵션 사용
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getTotalCopies());    // Book 객체의 totalCopies 값 설정
            pstmt.setInt(5, book.getAvailableCopies()); // Book 객체의 availableCopies 값 설정

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("책 추가 실패, 영향 받은 행 없음.");
            }

            // 자동 생성된 ID를 Book 객체에 다시 설정 (선택 사항이지만 좋은 관행)
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1)); // 첫 번째 자동 생성 키 (ID)를 가져옴
                } else {
                    throw new SQLException("책 추가 실패, 자동 생성된 ID를 가져오지 못함.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            throw e; // 예외를 다시 던져 서비스 계층에서 처리하도록 함
        }
    }
    @Override
    public void incrementBookCopies(int bookId, int count, Connection conn) throws SQLException {
        String query = "UPDATE Books SET " +
                "total_copies= total_copies + ?," +
                "availalbe_copies = available_copies + ? " +
                "WHERE id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, count);
            pstmt.setInt(2, count);
            pstmt.setInt(3, bookId);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.err.println("Error incrementing book copies: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void decrementBookCopies(int bookId, int count, Connection conn) throws SQLException {
        String query = "UPDATE Books SET " +
                "total_copies= total_copies - ?," +
                "availalbe_copies = available_copies - ? " +
                "WHERE id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, count);
            pstmt.setInt(2, count);
            pstmt.setInt(3, bookId);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.err.println("Error decrementing book copies: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void incrementAvailableCopies(int bookId, int count, Connection conn) throws SQLException {
        String query = "UPDATE Books SET available_copies = available_copies + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, count);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error incrementing book copies: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void decrementAvailableCopies(int bookId, int count, Connection conn) throws SQLException {
        String query = "UPDATE Books SET available_copies = available_copies - ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, count);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error decrementing book copies: " + e.getMessage());
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

    private List<Book> getBooks(String info, List<Book> books, String query) throws SQLException {
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, info);
            //반환되는 query를 읽는 과정

            try(ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()){
                    Book book = createBookFromResultSet(rs);
                    books.add(book);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error bringing book: " + e.getMessage());
            throw e;
        }
        return books;
    }

    @Override
    public List<Book> getBooksByAuthor(String author) throws SQLException {

        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE author=?";

        return getBooks(author, books, query);
    }

    @Override
    public List<Book> getBooksByTitle(String title) throws SQLException {

        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE title=?";

        return getBooks(title, books, query);
    }

    public List<Book> getBooksByKeyword(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE title LIKE ?";
        keyword = "%" + keyword + "%";
        return getBooks(keyword, books, query);
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
        // total_copies, available_copies 필드도 함께 업데이트
        String query = "UPDATE Books SET title = ?, author = ?, isbn = ?, total_copies = ?, available_copies = ? WHERE id = ?";
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getTotalCopies());
            pstmt.setInt(5, book.getAvailableCopies());
            pstmt.setInt(6, book.getId()); // WHERE 절의 ID

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                // 업데이트할 책이 없거나, 이미 같은 값으로 업데이트 시도
                throw new SQLException("책 정보 업데이트 실패: ID " + book.getId() + "를 찾을 수 없거나 변경 사항 없음.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
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
