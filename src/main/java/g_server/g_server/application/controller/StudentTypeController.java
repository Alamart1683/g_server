package g_server.g_server.application.controller;

import g_server.g_server.application.entity.system_data.StudentType;
import g_server.g_server.application.service.crud.StudentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
public class StudentTypeController {
    @Autowired
    private StudentTypeService studentTypeService;

    @GetMapping("/student_type/all")
    public List<StudentType> getAll() {
        return studentTypeService.findAll();
    }

    @GetMapping("/student_type/{id}")
    public Optional<StudentType> getById(@PathVariable int id) {
        return studentTypeService.findById(id);
    }

    @PostMapping("/student_type/save/")
    public void save(@RequestParam String student_type) {
        studentTypeService.save(new StudentType(student_type));
    }

    @PutMapping("/student_type/update/")
    public void update(
            @RequestParam int id,
            @RequestParam String student_type
    ) {
        StudentType studentType = studentTypeService.findById(id).get();
        studentType.setStudentType(student_type);
        studentTypeService.save(studentType);
    }

    @DeleteMapping("/student_type/delete/{id}")
    public void delete(@PathVariable int id) {
        studentTypeService.delete(id);
    }
}
