package g_server.g_server.application.service.system_data;

import g_server.g_server.application.entity.system_data.StudentType;
import g_server.g_server.application.repository.system_data.StudentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentTypeService {
    @Autowired
    StudentTypeRepository studentTypeRepository;

    public void save(StudentType studentType) {
        studentTypeRepository.save(studentType);
    }

    public List<StudentType> findAll() {
        return studentTypeRepository.findAll();
    }

    public void delete(int id) {
        studentTypeRepository.deleteById(id);
    }

    public Optional<StudentType> findById(int id) {
        return studentTypeRepository.findById(id);
    }
}