package g_server.g_server.application.controller.system_data;

import g_server.g_server.application.entity.system_data.StudentGroup;
import g_server.g_server.application.service.system_data.StudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
public class StudentGroupController {
    @Autowired
    private StudentGroupService studentGroupService;

    @GetMapping("/student_group/all")
    public List<StudentGroup> getAll() {
        return studentGroupService.findAll();
    }

    @GetMapping("/student_group/{id}")
    public Optional<StudentGroup> getById(@PathVariable int id) {
        return studentGroupService.findById(id);
    }

    @PostMapping("/student_group/save/")
    public void save(@RequestParam String student_group) {
        studentGroupService.save(new StudentGroup(student_group));
    }

    @PutMapping("/student_group/update/")
    public void update(
            @RequestParam int id,
            @RequestParam String student_group
    ) {
        StudentGroup studentGroup = studentGroupService.findById(id).get();
        studentGroup.setStudentGroup(student_group);
        studentGroupService.save(studentGroup);
    }

    @DeleteMapping("/student_group/delete/{id}")
    public void delete(@PathVariable int id) {
        studentGroupService.delete(id);
    }
}
