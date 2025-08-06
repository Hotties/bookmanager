package bookmanager.dao;

import bookmanager.model.Member;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface MemberDao {

    void addMember(Member member, Connection conn) throws SQLException;

    Member getMemberById(int id) throws SQLException;

    Member getMemberByLoginName(String name) throws SQLException;

    List<Member> getAllMembers() throws SQLException;

    void updateMember(Member member, Connection conn) throws SQLException;

    void deleteMember(int id, Connection conn) throws SQLException;

}
