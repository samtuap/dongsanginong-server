package org.samtuap.inong.domain.favorites.repository;

import org.samtuap.inong.domain.favorites.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
}
