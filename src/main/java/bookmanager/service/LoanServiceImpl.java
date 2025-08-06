package bookmanager.service;

import bookmanager.dao.*;
import bookmanager.model.Book;
import bookmanager.model.Loan;
import bookmanager.model.Member;
import bookmanager.util.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class LoanServiceImpl implements LoanService {

    private BookDao bookDao;
    private LoanDao loanDao;
    private MemberDao memberDao;

    public LoanServiceImpl(BookDaoImpl bookDao, LoanDaoImpl loanDao, MemberDaoImpl memberDao) {
        this.bookDao = bookDao;
        this.loanDao = loanDao;
        this.memberDao = memberDao;
    }

    @Override
    public void borrowBook(int bookId, int memberId) throws SQLException { // memberId로 변수명 수정
        Connection conn = null; // Connection을 try 블록 외부에서 선언
        try {
            conn = ConnectionPoolManager.getConnection();
            conn.setAutoCommit(false); // ★ 트랜잭션 시작: 자동 커밋 비활성화

            // 1. 책 존재 확인
            Book existingBook = bookDao.getBookById(bookId); // conn 매개변수 필요 없음 (단순 조회)
            if (existingBook == null) {
                throw new SQLException("책 ID " + bookId + "를 찾을 수 없습니다.");
            }

            // 2. 책 가용 재고 확인
            if (existingBook.getAvailableCopies() <= 0) {
                throw new SQLException("책 '" + existingBook.getTitle() + "'(ID: " + bookId + ")의 재고가 없습니다.");
            }

            // 3. 회원 존재 확인
            Member existingMember = memberDao.getMemberById(memberId);// memberDao.getMember(MemberId) -> getMemberById(memberId)
            if (existingMember == null) {
                throw new SQLException("회원 ID " + memberId + "를 찾을 수 없습니다.");
            }

            // 4. 회원의 대출 한도 확인
            final int MAX_LOANS_PER_MEMBER = 5; // 최대 대출 가능 권수
            int activeLoans = loanDao.getActiveLoanCountByMemberId(memberId, conn); // conn 매개변수 필요
            if (activeLoans >= MAX_LOANS_PER_MEMBER) {
                throw new SQLException("회원 '" + existingMember.getName() + "'(ID: " + memberId + ")의 대출 한도(" + MAX_LOANS_PER_MEMBER + "권)를 초과했습니다.");
            }

            // 5. (선택적) 동일 책 중복 대출 방지
            // '한 회원이 같은 책 종류를 동시에 한 권만 대출 가능' 정책 가정
            Loan existingActiveLoan = loanDao.getActiveLoanByBookIdAndMemberId(bookId, memberId, conn);
            if (existingActiveLoan != null) {
                throw new SQLException("회원 '" + existingMember.getName() + "'(ID: " + memberId + ")은 이미 책 '" + existingBook.getTitle() + "'(ID: " + bookId + ")을 대출 중입니다.");
            }

            // 모든 유효성 검사 통과 후 실제 대출 로직 실행
            // 6. DB 내 책 정보 수정: 가용 재고 감소
            bookDao.decrementAvailableCopies(bookId,1,conn); // BookDao에 conn 매개변수 필요

            // 7. 대출 기록 추가
            LocalDateTime loanTime = LocalDateTime.now();
            // Loan ID는 DB에서 자동 생성되므로 0으로 초기화
            // returnDate는 null로 시작, dueDate는 반납 예정일
            Loan newLoan = new Loan(0, bookId, memberId, loanTime, null, loanTime.plusDays(7)); // Loan 생성자에 dueDate 포함
            loanDao.addLoan(newLoan, conn); // LoanDao에 conn 매개변수 필요

            conn.commit(); // ★ 트랜잭션 커밋

        } catch (SQLException e) {
            System.err.println("대출 처리 중 오류 발생: " + e.getMessage()); // 오류 메시지 출력
            if (conn != null) {
                try {
                    conn.rollback(); // ★ 오류 발생 시 롤백
                    System.err.println("borrowBook 트랜잭션 롤백됨.");
                } catch (SQLException rbEx) {
                    System.err.println("롤백 실패: " + rbEx.getMessage());
                }
            }
            throw e; // 호출한 쪽으로 SQLException 다시 던짐
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // ★ 자동 커밋 모드 복원
                    conn.close(); // Connection 반환 (풀에)
                } catch (SQLException closeEx) {
                    System.err.println("Connection 닫기 실패: " + closeEx.getMessage());
                }
            }
        }
    }

    @Override
    public void returnBook(int bookId, int memberId) throws SQLException {

        Connection conn = null;
        try {
            conn = ConnectionPoolManager.getConnection();
            conn.setAutoCommit(false);
            //1.멤버 확인
            Member existingMember = memberDao.getMemberById(memberId);
            if (existingMember == null) {
                throw new SQLException("member not found");
            }

            //2.책 존재 확인
            Book existingBook = bookDao.getBookById(bookId);
            if (existingBook == null) {
                throw new SQLException("book not found");
            }

            //3. 해당 회원의 대출 기록 조회
            //하나의 책 종류는 한 회원이 한권만 대출 가능 정책
            Loan activeLoan = loanDao.getActiveLoanByBookIdAndMemberId(bookId, memberId, conn);
            if (activeLoan == null) {
                throw new SQLException("회원 ID " + memberId + "가 책 ID " + bookId + "를 대출 중이 아니거나 이미 반납되었습니다.");
            }

            //4. 대출 기록 반납일 업데이트
            loanDao.updateLoanReturnDate(activeLoan.getId(), LocalDateTime.now(), conn);
            bookDao.incrementAvailableCopies(bookId, 1, conn);
            conn.commit();

        }catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 오류 발생 시 롤백
                    System.err.println("Transaction rolled back for returnBook: " + e.getMessage());
                } catch (SQLException rbEx) {
                    System.err.println("Rollback failed during returnBook rollback: " + rbEx.getMessage());
                }
            }
            throw e; // 예외 다시 던지기
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 자동 커밋 모드 복원
                    conn.close(); // Connection 반환
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection after returnBook: " + closeEx.getMessage());
                }
            }
        }
    }

    @Override
    public List<Loan> getActiveLoans() throws SQLException {
        try(Connection conn = ConnectionPoolManager.getConnection();){
            return loanDao.getAllActiveLoans();
        }catch (SQLException e){
            throw new SQLException("failed to get Loans by BookId: " + e.getMessage());
        }
    }

    @Override
    public Loan getLoanDetails(int loanId) throws SQLException {
        try(Connection conn = ConnectionPoolManager.getConnection();){
            return loanDao.getLoanById(loanId);
        }catch (SQLException e){
            throw new SQLException("failed to get loan details: " + e.getMessage());
        }
    }

    @Override
    public List<Loan> getLoansByBookId(int bookId) throws SQLException {
        // LoanDao.getLoanByBookId는 이미 독립적인 Connection을 얻으므로 서비스에서 별도로 Connection을 다룰 필요 없음
        // 책 존재 유효성 검사 추가 (선택 사항이지만 좋은 습관)
        Book existingBook = bookDao.getBookById(bookId);
        if (existingBook == null) {
            throw new SQLException("책 ID " + bookId + "를 찾을 수 없습니다.");
        }
        return loanDao.getLoanByBookId(bookId); // 이미 구현된 DAO 메서드 활용
    }

    @Override
    public List<Loan> getLoansByMemberId(int memberId) throws SQLException {
        // 회원 존재 유효성 검사 추가 (선택 사항이지만 좋은 습관)
        Member existingMember = memberDao.getMemberById(memberId); // MemberDao 필요
        if (existingMember == null) {
            throw new SQLException("회원 ID " + memberId + "를 찾을 수 없습니다.");
        }
        return loanDao.getLoanByMemberId(memberId); // 이미 구현된 DAO 메서드 활용
    }

}
