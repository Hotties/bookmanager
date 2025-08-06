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

        Timestamp dueTimestamp = rs.getTimestamp("due_date");
        LocalDateTime dueDate = (dueTimestamp != null) ? dueTimestamp.toLocalDateTime() : null;

        return new Loan(id, bookId, memberId, loanDate, returnDate,dueDate);
    }

    private void setTimestampOrNull(PreparedStatement pstmt, int index, LocalDateTime timestamp) throws SQLException {
        if (timestamp != null) {
            pstmt.setTimestamp(index, Timestamp.valueOf(timestamp));
        }else{
            pstmt.setNull(index, Types.TIMESTAMP);
        }
    }

    @Override
    public void addLoan(Loan loan, Connection conn) throws SQLException { // Connection conn 추가
        String query = "INSERT INTO Loans(book_id, member_id, loan_date, return_date) VALUES(?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) { // ID 가져오기 옵션 추가

            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getMemberId());
            setTimestampOrNull(pstmt, 3, loan.getLoanDate());
            setTimestampOrNull(pstmt, 4, loan.getReturnDate());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("대출 기록 추가 실패, 영향 받은 행 없음.");
            }

            // 생성된 ID를 Loan 객체에 다시 설정
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("대출 기록 추가 실패, 자동 생성된 ID를 가져오지 못함.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding loan: " + e.getMessage());
            throw e;
        }
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
    public List<Loan> getLoanByBookId(int bookId) throws SQLException {
        String query = "SELECT * FROM Loans WHERE book_id = ?";
        return executeLoanQuery(query, bookId); // 수정된 헬퍼 메서드 호출
    }

    @Override
    public List<Loan> getLoanByMemberId(int memberId) throws SQLException {
        String query = "SELECT * FROM Loans WHERE member_id = ?";
        return executeLoanQuery(query, memberId); // 수정된 헬퍼 메서드 호출
    }

    private List<Loan> executeLoanQuery(String query, int paramId) throws SQLException { // 메서드 이름 변경, paramId로 통일
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = ConnectionPoolManager.getConnection(); // 트랜잭션 필요 없는 조회
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, paramId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) { // <-- 핵심 수정: if -> while
                    Loan loan = createLoanfromResultSet(rs);
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting Loans: " + e.getMessage());
            throw e;
        }
        return loans;
    }

    @Override
    public List<Loan> getAllActiveLoans() throws SQLException{
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM Loans WHERE return_date IS NULL";
        try(Connection conn = ConnectionPoolManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery()) {

            while(rs.next()){
                loans.add(createLoanfromResultSet(rs));
            }

        }catch(SQLException e){
            System.out.println("Error getting Loans: " + e.getMessage());
            throw e;
        }
        return loans;
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

    @Override
    public void deleteLoan(int id) throws SQLException { // 트랜잭션에 묶일 경우 Connection conn 매개변수 추가 고려
        String query = "DELETE FROM Loans WHERE id = ?";
        try (Connection conn = ConnectionPoolManager.getConnection(); // 독립적인 Connection
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting loans: " + e.getMessage());
            throw e;
        }
    }

    // --- LoanDao 인터페이스에 추가 필요할 메서드 (LoanService에서 사용할 가능성) ---
    // 특정 책이 현재 대출 중인지 확인 (BookService.removeBook에서 사용 가능)
    @Override
    public Loan getActiveLoansByBookId(int bookId, Connection conn) throws SQLException {

        String query = "SELECT COUNT(*) FROM Loans WHERE book_id = ? AND return_date IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createLoanfromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking active loans for book: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // 특정 회원이 특정 책을 현재 대출 중인지 확인 (LoanService.borrowBook에서 중복 대출 방지)
    @Override
    public Loan getActiveLoanByBookIdAndMemberId(int bookId, int memberId, Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM Loans WHERE book_id = ? AND member_id = ? AND return_date IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createLoanfromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking active loan for book by member: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // 특정 회원의 현재 대출 중인 책 수량 조회 (LoanService.borrowBook에서 대출 한도 체크)
    @Override
    public int getActiveLoanCountByMemberId(int memberId, Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM Loans WHERE member_id = ? AND return_date IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active loan count for member: " + e.getMessage());
            throw e;
        }
        return 0;
    }
}