package g_server.g_server.application.entity.system_data;

import g_server.g_server.application.entity.users.Roles;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "economy_consultants_student_group")
public class GroupsConsultants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "consultantID")
    private int consultantID;

    @Column(name = "groupID")
    private int groupID;

    public GroupsConsultants() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConsultantID() {
        return consultantID;
    }

    public void setConsultantID(int consultantID) {
        this.consultantID = consultantID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
