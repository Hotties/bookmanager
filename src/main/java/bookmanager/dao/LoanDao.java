package bookmanager.dao;

import bookmanager.model.Loan;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface LoanDao {

    void addLoan(Loan loan, Connection conn) throws SQLException;

    Loan getLoanById(int id) throws SQLException;

    List<Loan> getLoanByBookId(int bookId) throws SQLException;

    List<Loan> getLoanByMemberId(int memberId) throws SQLException;

    List<Loan> getAllActiveLoans() throws SQLException;

    List<Loan> getAllLoans() throws SQLException;

    void updateLoan(Loan loan) throws SQLException;

    void updateLoanReturnDate(int loanId, LocalDateTime returnDate, Connection conn) throws SQLException;

    void deleteLoan(int id) throws SQLException;
    // --- LoanService의 비즈니스 로직을 위한 추가 메서드 ---
    Loan getActiveLoansByBookId(int bookId, Connection conn) throws SQLException;
    Loan getActiveLoanByBookIdAndMemberId(int bookId, int memberId, Connection conn) throws SQLException;
    int getActiveLoanCountByMemberId(int memberId, Connection conn) throws SQLException;
}
