package bookmanager.service;

import bookmanager.dao.MemberDao;
import bookmanager.model.Member;

import java.sql.SQLException;
import java.util.List;

public interface MemberService {

    void registerMember(Member member) throws SQLException;

    Member getMemberDetails(int id) throws SQLException;

    void updateMemberInfo(Member member) throws SQLException;

    void deleteMember(int id) throws SQLException;

    List<Member> getAllMembers() throws SQLException;
}
