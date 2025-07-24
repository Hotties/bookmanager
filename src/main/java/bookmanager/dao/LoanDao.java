package bookmanager.dao;

import bookmanager.model.Loan;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface LoanDao {

    void addLoan(Loan loan) throws SQLException;

    void addLoan(int bookId, int MemberId, Connection conn);

    Loan getLoanById(int id) throws SQLException;

    Loan getLoanByBookId(int bookId) throws SQLException;

    Loan getLoanByMemberId(int memberId) throws SQLException;

    List<Loan> getAllLoans() throws SQLException;

    void updateLoan(Loan loan) throws SQLException;

    void updateLoanReturnDate(int loanId, LocalDateTime returnDate, Connection conn) throws SQLException;
}
