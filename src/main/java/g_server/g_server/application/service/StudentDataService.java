package g_server.g_server.application.service;

import g_server.g_server.application.entity.StudentData;
import g_server.g_server.application.repository.StudentDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentDataService {
    @Autowired
    private StudentDataRepository studentDataRepository;

    public List<StudentData> findAll() {
        return studentDataRepository.findAll();
    }

    public Optional<StudentData> findById(int id) {
        return studentDataRepository.findById(id);
    }

    public void save(StudentData studentData) {
        studentDataRepository.save(studentData);
    }

    public void delete(int id) {
        studentDataRepository.deleteById(id);
    }
}
