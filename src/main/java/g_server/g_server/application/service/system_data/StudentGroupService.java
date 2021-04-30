package g_server.g_server.application.service.system_data;

import g_server.g_server.application.entity.system_data.StudentGroup;
import g_server.g_server.application.repository.system_data.StudentGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentGroupService {
    StudentGroupRepository studentGroupRepository;

    @Autowired
    public void setStudentGroupRepository(StudentGroupRepository studentGroupRepository) {
        this.studentGroupRepository = studentGroupRepository;
    }

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
}
