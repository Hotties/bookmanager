package bookmanager.service;

import bookmanager.dao.BookDao;
import bookmanager.dao.LoanDao;
import bookmanager.dao.MemberDao;
import bookmanager.model.Book;
import bookmanager.util.ConnectionPoolManager;

public class BookService {

    private BookDao bookDao;
    private MemberDao memberDao;
    private LoanDao loanDao;
    private ConnectionPoolManager connectionPool;

    public void addBook(Book book) {

    }

    public void removeBook(Book book) {

    }

    public void updateBookInfo(Book book) {

    }

    public void getAllBooks() {

    }

    public Book searchBooks(String query) {
        return null;
    }

    public boolean borrowBook(int bookId, int memberId) {
        return true;
    }

    public boolean returnBook(int bookId, int memberId) {
        return true;
    }


}
