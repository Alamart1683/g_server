package g_server.g_server.application.repository.system_data;

import g_server.g_server.application.entity.system_data.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Integer> {
    @Override
    List<Speciality> findAll();

    @Override
    <S extends Speciality> S save(S s);

    @Override
    Optional<Speciality> findById(Integer integer);

    @Override
    void deleteById(Integer integer);

    Speciality findByCode(String code);

    Speciality findByPrefix(String code);

    Speciality findBySpeciality(String code);
}
