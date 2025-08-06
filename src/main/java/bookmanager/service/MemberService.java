package bookmanager.service;

import bookmanager.dao.MemberDao;
import bookmanager.model.Member;

import java.sql.SQLException;
import java.util.List;

public interface MemberService {


   void addMember(Member member) throws SQLException;

   Member getMemberById(int memberId) throws SQLException;

   void updateMember(Member member) throws SQLException;

   void deleteMember(int memberId) throws SQLException;

   List<Member> getAllMembers() throws SQLException;


}
