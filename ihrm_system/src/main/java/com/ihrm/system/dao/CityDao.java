package com.ihrm.system.dao;

import com.ihrm.domain.system.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityDao extends JpaRepository<City, String> {
}
