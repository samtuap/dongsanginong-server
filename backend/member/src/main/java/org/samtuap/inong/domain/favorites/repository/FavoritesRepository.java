package org.samtuap.inong.domain.favorites.repository;

import org.samtuap.inong.domain.favorites.entity.Favorites;
import org.samtuap.inong.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    List<Favorites> findAllByMember(Member member);
    List<Favorites> findByFarmId(Long farmId);
    List<Favorites> findByMember(Member member);

    Optional<Favorites> findByMemberAndFarmId(Member member, Long farmId);

}
