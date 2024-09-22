package org.samtuap.inong.domain.favorites.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.favorites.dto.FollowersGetResponse;
import org.samtuap.inong.domain.favorites.repository.FavoritesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;

    public FollowersGetResponse getFollowers(Long farmId) {
        List<Long> followers = favoritesRepository.findByFarmId(farmId).stream()
                .map(favorite -> favorite.getMember().getId()).toList();
        return new FollowersGetResponse(followers);
    }
}
