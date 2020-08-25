package g_server.g_server.application.entity.documents;

import javax.persistence.*;

@Entity
@Table(name = "view_rights")
public class ViewRights {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private boolean is_only_for_me;

    @Column
    private boolean is_only_for_scientific_advisors;

    @Column
    private boolean is_only_for_my_students;

    @Column
    private boolean is_for_all_students;

    @Column
    private boolean is_for_all;

    public ViewRights() { }

    public ViewRights(boolean iofm, boolean iofsa, boolean iofms,
           boolean ifas, boolean ifa) {
        this.is_only_for_me = iofm;
        this.is_only_for_scientific_advisors = iofsa;
        this.is_only_for_my_students = iofms;
        this.is_for_all_students = ifas;
        this.is_for_all = ifa;
    }

    public boolean isIs_only_for_me() {
        return is_only_for_me;
    }

    public void setIs_only_for_me(boolean is_only_for_me) {
        this.is_only_for_me = is_only_for_me;
    }

    public boolean isIs_only_for_scientific_advisors() {
        return is_only_for_scientific_advisors;
    }

    public void setIs_only_for_scientific_advisors(boolean is_only_for_scientific_advisors) {
        this.is_only_for_scientific_advisors = is_only_for_scientific_advisors;
    }

    public boolean isIs_only_for_my_students() {
        return is_only_for_my_students;
    }

    public void setIs_only_for_my_students(boolean is_only_for_my_students) {
        this.is_only_for_my_students = is_only_for_my_students;
    }

    public boolean isIs_for_all_students() {
        return is_for_all_students;
    }

    public void setIs_for_all_students(boolean is_for_all_students) {
        this.is_for_all_students = is_for_all_students;
    }

    public boolean isIs_for_all() {
        return is_for_all;
    }

    public void setIs_for_all(boolean is_for_all) {
        this.is_for_all = is_for_all;
    }
}
