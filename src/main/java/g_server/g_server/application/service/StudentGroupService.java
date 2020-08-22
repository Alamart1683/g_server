package g_server.g_server.application.service;

import g_server.g_server.application.entity.StudentGroup;
import g_server.g_server.application.repository.StudentGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentGroupService {
    @Autowired
    StudentGroupRepository studentGroupRepository;

    public void save(StudentGroup studentGroup) {
        studentGroupRepository.save(studentGroup);
    }

    public List<StudentGroup> findAll() {
        return studentGroupRepository.findAll();
    }

    public void delete(int id) {
        studentGroupRepository.deleteById(id);
    }

    public Optional<StudentGroup> findById(int id) {
        return studentGroupRepository.findById(id);
    }

    public StudentGroup findByStudentGroup(String student_group) {
        return studentGroupRepository.findByStudentGroup(student_group);
    }
}
