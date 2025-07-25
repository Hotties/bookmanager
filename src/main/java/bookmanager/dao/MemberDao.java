package bookmanager.dao;

import bookmanager.model.Member;

import java.sql.SQLException;
import java.util.List;

public interface MemberDao {

    void addMember(Member member) throws SQLException;

    Member getMember(int id) throws SQLException;

    Member getMemberByLoginName(String name) throws SQLException;

    List<Member> getAllMembers() throws SQLException;

    void updateMember(Member member) throws SQLException;

    void deleteMember(int id) throws SQLException;

}
