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
        int id = rs.getInt("member_id");
        String name = rs.getString("name");
        String contact = rs.getString("contact_info");
        return new Member(name, id, contact);
    }
    @Override
    public void addMember(Member member) throws SQLException {
        String query = "INSERT INTO Members(name, contact_info) VALUES (?, ?)";

        try(Connection conn = ConnectionPoolManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getContactInfo());

            pstmt.executeUpdate();

        }catch (SQLException e){
            System.err.println("Error adding Member"+e.getMessage());
            throw e;
        }
    }

    @Override
    public Member getMember(int id) throws SQLException {
        String query = "SELECT * FROM Members WHERE id = ?";
        Member member = null;

        try(Connection conn = ConnectionPoolManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    member = createMemberFromResultSet(rs);
                }
            }
        }catch (SQLException e){
            System.err.println("Error getting member: " + e.getMessage());
            throw e;
        }
        return member;
    }

    @Override
    public Member getMemberByLoginName(String name) throws SQLException {
        String query = "SELECT * FROM Members WHERE name = ?";
        Member member = null;

        try(Connection conn = ConnectionPoolManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);

            try(ResultSet rs = pstmt.executeQuery()) {
                member = createMemberFromResultSet(rs);
            }

        }catch (SQLException e){
            System.err.println("Error getting member: " + e.getMessage());
            throw e;
        }
        return member;
    }

    @Override
    public List<Member> getAllMembers() throws SQLException{
        String query = "SELECT name, id, contact_info FROM Members";
        List<Member> members = new ArrayList<>();

        try(Connection conn = ConnectionPoolManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Member member = createMemberFromResultSet(rs);
                members.add(member);
            }
        }catch (SQLException e){
            System.err.println("Error getting members: " + e.getMessage());
            throw e;
        }
        return members;
    }

    @Override
    public void updateMember(Member member) throws SQLException {
        String query = "UPDATE Members SET name = ?, contact_info = ? WHERE id = ?";

        try(Connection conn = ConnectionPoolManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getContactInfo());
            pstmt.setInt(3, member.getId());

            pstmt.executeUpdate();

        }catch (SQLException e){
            System.err.println("Error updating member: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteMember(int id) throws SQLException {
        String query = "DELETE FROM Members WHERE id = ?";
        try(Connection conn = ConnectionPoolManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        }catch (SQLException e){
            System.err.println("Error deleting member: " + e.getMessage());
            throw e;
        }

    }
}
