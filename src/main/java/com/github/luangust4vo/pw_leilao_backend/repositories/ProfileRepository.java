package com.github.luangust4vo.pw_leilao_backend.repositories;

import com.github.luangust4vo.pw_leilao_backend.models.enums.ProfileType;
import com.github.luangust4vo.pw_leilao_backend.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByType(ProfileType tipo);
}