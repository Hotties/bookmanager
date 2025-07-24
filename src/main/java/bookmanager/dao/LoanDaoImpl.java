package bookmanager.dao;

import bookmanager.model.Loan;
import bookmanager.util.ConnectionPoolManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanDaoImpl implements LoanDao {

    private Loan createLoanfromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int bookId = rs.getInt("book_id");
        int memberId = rs.getInt("member_id");

        Timestamp loanTimestamp = rs.getTimestamp("loan_date");
        LocalDateTime loanDate = (loanTimestamp != null) ? loanTimestamp.toLocalDateTime() : null;

        Timestamp returnTimestamp = rs.getTimestamp("return_date");
        LocalDateTime returnDate = (returnTimestamp != null) ? returnTimestamp.toLocalDateTime() : null;

        return new Loan(id, bookId, memberId, loanDate, returnDate);
    }
    private void setTimestampOrNull(PreparedStatement pstmt, int index, LocalDateTime timestamp) throws SQLException {
        if (timestamp != null) {
            pstmt.setTimestamp(index, Timestamp.valueOf(timestamp));
        }else{
            pstmt.setNull(index, Types.TIMESTAMP);
        }
    }
    @Override
    public void addLoan(Loan loan) throws SQLException {
        // id 컬럼을 쿼리에서 제외합니다.
        String query = "INSERT INTO Loans(book_id, member_id, loan_date, return_date) VALUES(?,?,?,?)";
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) { // 필요시 이 옵션 추가

            pstmt.setInt(1, loan.getBookId()); // 인덱스가 하나씩 당겨짐
            pstmt.setInt(2, loan.getMemberId());
            setTimestampOrNull(pstmt, 3, loan.getLoanDate());
            setTimestampOrNull(pstmt, 4, loan.getReturnDate());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding loan: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void addLoan(int bookId, int MemberId, Connection conn) {

    }

    @Override
    public Loan getLoanById(int id) throws SQLException {
        Loan loan = null;
        String query = "SELECT * FROM Loans WHERE id = ?";
        try(Connection conn = ConnectionPoolManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loan = createLoanfromResultSet(rs);
                }
            }
        } catch (SQLException e){
            System.err.println("Error getting Loan: " + e.getMessage());
            throw e;
        }
        return loan;
    }

    @Override
    public Loan getLoanByBookId(int bookId) throws SQLException {
        Loan loan = null;
        String query = "SELECT * FROM Loans WHERE book_id = ?";
        try(Connection conn = ConnectionPoolManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loan = createLoanfromResultSet(rs);
                }
            }
        } catch (SQLException e){
            System.err.println("Error getting Loan: " + e.getMessage());
            throw e;
        }
        return loan;
    }

    @Override
    public Loan getLoanByMemberId(int memberId) throws SQLException {
        Loan loan = null;
        String query = "SELECT * FROM Loans WHERE member_id = ?";
        try(Connection conn = ConnectionPoolManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loan = createLoanfromResultSet(rs);
                }
            }
        } catch (SQLException e){
            System.err.println("Error getting Loan: " + e.getMessage());
            throw e;
        }
        return loan;
    }

    @Override
    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT " +
                "id, book_id, member_id, loan_date, return_date " +
                "FROM Loans;";

        try(Connection conn = ConnectionPoolManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = createLoanfromResultSet(rs);
                    loans.add(loan);
                }
            }
        } catch (SQLException e){
            System.err.println("Error getting Loans: " + e.getMessage());
            throw e;
        }
        return loans;
    }

    @Override
    public void updateLoan(Loan loan) throws SQLException{
        String query = "UPDATE Loans SET book_id=?,member_id=?,loan_date=?,return_date=? WHERE id=?";
        try(Connection conn = ConnectionPoolManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getMemberId());
            setTimestampOrNull(pstmt,3,loan.getLoanDate());
            setTimestampOrNull(pstmt,4,loan.getReturnDate());
            pstmt.setInt(5, loan.getId());

            pstmt.executeUpdate();

        }catch (SQLException e){
            System.err.println("Error updating loans: " + e.getMessage());
            throw e;
        }

    }

    @Override
    public void updateLoanReturnDate(int loanId, LocalDateTime returnDate, Connection conn) throws SQLException {
        String query = "UPDATE Loans SET return_date=? WHERE id=?";
        try(PreparedStatement pstmt = conn.prepareStatement(query)) {
            setTimestampOrNull(pstmt, 1, returnDate); // 헬퍼 메서드 활용
            pstmt.setInt(2, loanId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            System.err.println("Error updating return_date: " + e.getMessage());
            throw e;
        }
    }
}
