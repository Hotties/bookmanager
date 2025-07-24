package bookmanager.dao;

import bookmanager.model.Member;

public interface MemberDao {

    void addMember(Member member);

    Member getMember(int id);

    Member getMemberByLoginName(String name);

    Member getAllMembers();

    void updateMember(Member member);

    void deleteMember(int id);

}
