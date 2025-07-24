package bookmanager.dao;

import bookmanager.model.Loan;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LoanDaoImpl implements LoanDao {

    @Override
    public void addLoan(Loan loan) {

    }

    @Override
    public void addLoan(int bookId, int MemberId, Connection conn) {

    }

    @Override
    public Loan getLoanById(int id) {
        return null;
    }

    @Override
    public Loan getLoanByBookId(int bookId) {
        return null;
    }

    @Override
    public Loan getLoanByMemberId(int memberId) {
        return null;
    }

    @Override
    public Loan getAllLoans() {
        return null;
    }

    @Override
    public void updateLoan(Loan loan) {

    }

    @Override
    public LocalDateTime updateLoanReturnDate(int LoanId, Timestamp returnDate, Connection conn) {
        return null;
    }
}
