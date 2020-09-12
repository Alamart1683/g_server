package g_server.g_server.application.entity.view;

import g_server.g_server.application.entity.users.Users;

// Представление студента который не определился с проектом
public class AssociatedStudentViewWithoutProject extends AssociatedRequestView {
    public AssociatedStudentViewWithoutProject(Users user, String theme, int systemID) {
        this.setSystemID(systemID);
        this.setFIO(user.getSurname() + ' ' + user.getName() + ' ' + user.getSecond_name());
        this.setGroup(user.getStudentData().getStudentGroup().getStudentGroup());
        this.setType(user.getStudentData().getStudentType().getStudentType());
        this.setCathedra(user.getStudentData().getCathedras().getCathedraName());
        this.setTheme(theme);
    }
}