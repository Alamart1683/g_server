package g_server.g_server.application.repository.messanger;

import g_server.g_server.application.entity.messanger.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Integer> {
    @Override
    List<Messages> findAll();

    @Override
    <S extends Messages> S save(S s);

    @Override
    void deleteById(Integer integer);

    Optional<Messages> findById(Integer id);

    List<Messages> findBySender(String sender);

    List<Messages> findBySendDate(String sendDate);

    List<Messages> findByReceivers(String receivers);
}
