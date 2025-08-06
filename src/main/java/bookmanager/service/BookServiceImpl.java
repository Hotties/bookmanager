package bookmanager.service;

import bookmanager.dao.BookDao;
import bookmanager.dao.BookDaoImpl;
import bookmanager.dao.LoanDaoImpl;
import bookmanager.model.Book;
import bookmanager.util.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookServiceImpl implements BookService {

    private final BookDaoImpl bookDao;
    private final LoanDaoImpl loanDao;
    public BookServiceImpl(BookDaoImpl bookDao, LoanDaoImpl loanDao) {

        this.bookDao = bookDao;

        this.loanDao = loanDao;
    }

    @Override
    public void registerBook(Book book) throws SQLException {

        Connection conn = ConnectionPoolManager.getConnection();

        Book existingBook = bookDao.getBookByIsbn(book.getIsbn());
        //중복 책이 있다면
        if (existingBook != null) {
            bookDao.incrementBookCopies(book.getId(),1, conn);
        }else{
            bookDao.addBook(book);
        }
    }

    @Override
    public Book getBookDetails(int id) throws SQLException {
        Book book = bookDao.getBookById(id);
        if (book == null) {
            // 책이 없으면 비즈니스 로직에 맞는 예외를 던져 상위 계층에 알림
            throw new SQLException("ID " + id + "에 해당하는 책을 찾을 수 없습니다.");
            // 혹은 throw new BookNotFoundException("ID " + id + "에 해당하는 책을 찾을 수 없습니다.");
        }
        return book;
    }

    @Override
    public Book getBookDetailsByISBN(String isbn) throws SQLException {
        Book book = bookDao.getBookByIsbn(isbn); // DAO에서 단일 Book 반환으로 가정
        if (book == null) {
            // 책이 없으면 비즈니스 로직에 맞는 예외를 던져 상위 계층에 알림
            throw new SQLException("ISBN " + isbn + "에 해당하는 책을 찾을 수 없습니다.");
            // 혹은 throw new BookNotFoundException("ISBN " + isbn + "에 해당하는 책을 찾을 수 없습니다.");
        }
        // System.out.println() 출력 제거
        return book;
    }

    @Override
    public List<Book> searchBooks(String keyword) throws SQLException {
        return bookDao.getBooksByKeyword(keyword);
    }

    @Override
    public void updateBookDetails(Book book) throws SQLException {
        // 1. **업데이트하려는 책이 실제로 존재하는지 확인**
        Book existingBook = bookDao.getBookById(book.getId());
        if (existingBook == null) {
            throw new SQLException("ID " + book.getId() + "에 해당하는 책을 찾을 수 없어 업데이트할 수 없습니다.");
            // 혹은 throw new BookNotFoundException("ID " + book.getId() + "에 해당하는 책을 찾을 수 없어 업데이트할 수 없습니다.");
        }

        // 2. **ISBN 변경 시 중복 검사:**
        //    새로운 ISBN이 기존 ISBN과 다를 경우, 다른 책에 이미 할당되어 있는지 확인
        if (!book.getIsbn().equals(existingBook.getIsbn())) {
            Book bookWithNewIsbn = bookDao.getBookByIsbn(book.getIsbn());
            // 새로운 ISBN이 다른 기존 책에 이미 할당되어 있다면 (bookWithNewIsbn != null)
            // (existingBook의 ID와 book의 ID가 같더라도, bookDao.getBookByIsbn은 현재 unique ISBN이므로 문제가 되지 않음)
            if (bookWithNewIsbn != null) {
                throw new SQLException("새로운 ISBN " + book.getIsbn() + "은 이미 다른 책에 등록되어 있습니다.");
                // 혹은 throw new DuplicateIsbnException("새로운 ISBN " + book.getIsbn() + "은 이미 다른 책에 등록되어 있습니다.");
            }
        }
        bookDao.updateBook(book);
    }
    @Override
    public void removeBook(int id) throws SQLException {
        // 매개변수 이름을 Java 컨벤션에 따라 'id'로 변경
        // 1. **삭제하려는 책이 실제로 존재하는지 확인**
        Book bookToDelete = bookDao.getBookById(id);
        if (bookToDelete == null) {
            throw new SQLException("삭제할 책을 찾을 수 없습니다: ID " + id);
            // 혹은 throw new BookNotFoundException("삭제할 책을 찾을 수 없습니다: ID " + id);
        }

        // 2. **핵심 비즈니스 로직: 해당 책이 대출 중인지 확인**
        //    이 부분은 LoanDao 또는 LoanService에 대한 의존성이 필요합니다.
        //    예시:
        //    if (loanDao.hasActiveLoansByBookId(id)) { // LoanDao에 이런 메서드가 있다고 가정
        //        throw new SQLException("해당 책은 현재 대출 중이므로 삭제할 수 없습니다.");
        //        // 혹은 throw new BookCurrentlyBorrowedException("해당 책은 현재 대출 중이므로 삭제할 수 없습니다.");
        //    }

        // 모든 비즈니스 규칙을 통과하면 DAO를 통해 삭제
        bookDao.deleteBook(id);
    }
}
