package com.ihrm.system.service;

import com.ihrm.domain.system.City;

import java.util.List;

public interface CityService {
    void add(City city);
    void update(City city);
    void deleteById(String id);
    City findById(String id);
    List<City> findAll();
}
