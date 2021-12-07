package com.vg.raiddataparser.repository;

import com.vg.raiddataparser.model.champion.Champion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChampionRepository extends JpaRepository<Champion, Integer> {
}