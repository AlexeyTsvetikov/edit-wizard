package ru.tsvetikov.editwizard.model.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsvetikov.editwizard.model.db.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
