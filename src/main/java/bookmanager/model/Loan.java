package bookmanager.model;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Objects;

public class Loan {
    private int id;
    private int BookId;
    private int memberId;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;

    public Loan(int id, int bookId, int memberId,LocalDateTime loanDate, LocalDateTime returnDate) {
        this.id = id;
        BookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return BookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBookId(int bookId) {
        BookId = bookId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", BookId=" + BookId +
                ", memberId=" + memberId +
                ", loanDate=" + loanDate +
                ", returnDate=" + returnDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return id == loan.id && BookId == loan.BookId && memberId == loan.memberId && Objects.equals(loanDate, loan.loanDate) && Objects.equals(returnDate, loan.returnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, BookId, memberId, loanDate, returnDate);
    }
}
