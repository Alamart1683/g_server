package g_server.g_server.application.service;

import g_server.g_server.application.entity.StudentType;
import g_server.g_server.application.repository.StudentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentTypeService {
    @Autowired
    StudentTypeRepository studentTypeRepository;

    public void save(StudentType studentGroup) {
        studentTypeRepository.save(studentGroup);
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