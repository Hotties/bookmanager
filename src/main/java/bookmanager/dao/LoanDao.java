package bookmanager.dao;

import bookmanager.model.Loan;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public interface LoanDao {

    void addLoan(Loan loan);

    void addLoan(int bookId, int MemberId, Connection conn);

    Loan getLoanById(int id);

    Loan getLoanByBookId(int bookId);

    Loan getLoanByMemberId(int memberId);

    Loan getAllLoans();

    void updateLoan(Loan loan);

    LocalDateTime updateLoanReturnDate(int LoanId, Timestamp returnDate, Connection conn);
}
