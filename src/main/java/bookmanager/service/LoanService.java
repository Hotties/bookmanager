package bookmanager.service;

import bookmanager.model.Loan;

import java.sql.SQLException;
import java.util.List;

public interface LoanService {

    void borrowBook(int bookId, int MemberId) throws SQLException;

    void returnBook(int bookId, int MemberId) throws SQLException;

    List<Loan> getActiveLoans() throws SQLException;

    List<Loan> getLoansByBookId(int id) throws SQLException;

    List<Loan> getLoansByMemberId(int id) throws SQLException;

}
