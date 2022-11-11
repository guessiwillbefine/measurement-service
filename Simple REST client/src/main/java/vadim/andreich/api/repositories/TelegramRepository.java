package vadim.andreich.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vadim.andreich.api.model.Telegram;

import java.util.Optional;

@Repository
public interface TelegramRepository extends JpaRepository<Telegram, Integer> {

    @Modifying
    @Query("""
            update Telegram t set t.alertEnabled = case t.alertEnabled
            when true then false
            else true end
            where t.chatId = ?1""")
    int setUpAlertsByChatId(int id);

    Optional<Telegram> findDistinctByChatId(int chatId);
}
