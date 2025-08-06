package bookmanager.dao;

import bookmanager.model.Member;
import bookmanager.util.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDaoImpl implements MemberDao {

    private Member createMemberFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String contact = rs.getString("contact_info");
        return new Member(name, id, contact);
    }

    @Override
    public void addMember(Member member, Connection conn) throws SQLException {
        String query = "INSERT INTO Members(name, contact_info) VALUES (?, ?)";
        // 자동 생성된 ID를 가져오기 위해 Statement.RETURN_GENERATED_KEYS 추가
        try (PreparedStatement pstmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getContactInfo());

            int affectedRows = pstmt.executeUpdate(); // 영향을 받은 행 수 확인

            if (affectedRows == 0) {
                throw new SQLException("회원 추가 실패, 영향 받은 행 없음.");
            }

            // 자동 생성된 ID를 가져와 Member 객체에 다시 설정
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setId(generatedKeys.getInt(1)); // 'id'가 첫 번째 자동 생성 키라고 가정
                } else {
                    throw new SQLException("회원 추가 실패, 자동 생성된 ID를 가져오지 못함.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding Member: " + e.getMessage()); // 더 명확한 오류 메시지
            throw e;
        }
    }

    @Override
    public Member getMemberById(int id) throws SQLException {
        String query = "SELECT * FROM Members WHERE id = ?";
        Member member = null;

        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    member = createMemberFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member: " + e.getMessage());
            throw e;
        }
        return member;
    }

    @Override
    public Member getMemberByLoginName(String name) throws SQLException {
        String query = "SELECT * FROM Members WHERE name = ?";
        Member member = null;

        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    member = createMemberFromResultSet(rs);

                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting member: " + e.getMessage());
            throw e;
        }
        return member;
    }

    @Override
    public List<Member> getAllMembers() throws SQLException {
        String query = "SELECT name, id, contact_info FROM Members";
        List<Member> members = new ArrayList<>();

        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Member member = createMemberFromResultSet(rs);
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error getting members: " + e.getMessage());
            throw e;
        }
        return members;
    }

    @Override
    public void updateMember(Member member, Connection conn) throws SQLException { // Connection conn 추가
        String query = "UPDATE Members SET name = ?, contact_info = ? WHERE member_id = ?"; // PK가 member_id라면 이렇게 사용

        try (PreparedStatement pstmt = conn.prepareStatement(query)) { // 전달받은 conn 사용
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getContactInfo());
            pstmt.setInt(3, member.getId()); // WHERE 절에 member.getId() 사용

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteMember(int id, Connection conn) throws SQLException {
        String query = "DELETE FROM Members WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            throw e;
        }

    }
}
