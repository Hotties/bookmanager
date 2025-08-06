package bookmanager.service;

import bookmanager.dao.*;
import bookmanager.model.Member;
import bookmanager.util.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final LoanDao loanDao;

    public MemberServiceImpl(MemberDaoImpl memberDao, LoanDaoImpl loanDao) {
        this.memberDao = memberDao;
        this.loanDao = loanDao;
    }

    @Override
    public void addMember(Member member) throws SQLException {
        Connection conn = null;

        try {
            conn = ConnectionPoolManager.getConnection();
            conn.setAutoCommit(false);

            // 중복확인
            Member existingMember = memberDao.getMemberById(member.getId());
            if (existingMember != null) {
                throw new SQLException("이미 존재하는 멤버입니다");
            }

            memberDao.addMember(member, conn);

            conn.commit();


        } catch (SQLException e) {
            System.out.println("failed to add Member" + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("트랜잭션 롤백");
                } catch (SQLException rbEx) {
                    System.out.println("롤백 실패" + rbEx.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException rbEx) {
                    System.out.println("failed to close Connection" + rbEx.getMessage());
                }
            }
        }

    }

    @Override
    public Member getMemberById(int memberId) throws SQLException {

        Member member = memberDao.getMemberById(memberId);
        if (member == null) {
            throw new SQLException("해당 멤버가 존재하지 않습니다.");
        } else {
            return member;
        }
    }

    @Override
    public void updateMember(Member member) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionPoolManager.getConnection();
            conn.setAutoCommit(false);

            // 1. 회원 존재 확인
            Member existingMember = memberDao.getMemberById(member.getId());
            if (existingMember == null) {
                throw new SQLException("ID " + member.getId() + "에 해당하는 회원이 존재하지 않아 업데이트할 수 없습니다.");
            }

            // 2. DAO 호출
            // memberDao.updateMember 메서드는 conn 매개변수를 받도록 수정되어야 합니다.
            memberDao.updateMember(member, conn);

            conn.commit();
        } catch (SQLException e) {
            System.err.println("회원 업데이트 실패: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("updateMember 트랜잭션 롤백됨.");
                } catch (SQLException rbEx) {
                    System.err.println("롤백 실패: " + rbEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Connection 닫기 실패: " + closeEx.getMessage());
                }
            }
        }
    }

    @Override
    public void deleteMember(int memberId) throws SQLException {
        Connection conn = null; // Connection을 try 블록 외부에서 선언
        try {
            conn = ConnectionPoolManager.getConnection();
            conn.setAutoCommit(false); // ★ 트랜잭션 시작

            // 1. 회원 존재 확인
            Member existingMember = memberDao.getMemberById(memberId);
            if (existingMember == null) {
                throw new SQLException("ID " + memberId + "에 해당하는 회원이 존재하지 않습니다.");
            }

            // 2. 해당 회원의 활성 대출 여부 확인
            // 이 로직이 트랜잭션에 포함되어야 하므로 DAO 메서드에 conn을 전달합니다.
            int loanCount = loanDao.getActiveLoanCountByMemberId(memberId, conn);
            if (loanCount > 0) {
                // 활성 대출이 있을 경우 삭제를 중단하고 예외를 던짐
                throw new SQLException("회원 '" + existingMember.getName() + "'(ID: " + memberId + ")에게는 현재 대출 중인 책이 " + loanCount + "권 있습니다. 모든 책을 반납한 후에 삭제할 수 있습니다.");
            }

            // 3. 모든 검사 통과 후 회원 삭제
            // MemberDao의 deleteMember 메서드도 Connection을 받도록 수정했어야 합니다.
            memberDao.deleteMember(memberId, conn);

            conn.commit(); // ★ 모든 작업 성공 시 커밋

        } catch (SQLException e) {
            System.err.println("회원 삭제 처리 중 오류 발생: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // ★ 오류 발생 시 롤백
                    System.err.println("deleteMember 트랜잭션 롤백됨.");
                } catch (SQLException rbEx) {
                    System.err.println("롤백 실패: " + rbEx.getMessage());
                }
            }
            throw e; // 호출한 쪽으로 예외 다시 던지기
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
    public List<Member> getAllMembers() throws SQLException {


        return memberDao.getAllMembers();
    }
}
