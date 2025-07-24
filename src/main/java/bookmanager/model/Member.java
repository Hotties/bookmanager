

package bookmanager.model;

import java.util.*;

public class Member {
    private int id;
    private String name;
    private String contactInfo;

    public Member(String name, int id, String contactInfo) {
        this.name = name;
        this.id = id;
        this.contactInfo = contactInfo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id && Objects.equals(name, member.name) && Objects.equals(contactInfo, member.contactInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contactInfo);
    }


}