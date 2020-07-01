package com.ihrm.system.service.impl;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.system.City;
import com.ihrm.system.dao.CityDao;
import com.ihrm.system.service.CityService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {
    @Autowired
    private CityDao cityDao;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void add(City city) {
        city.setId(idWorker.nextId()+"");
        cityDao.save(city);
    }

    @Override
    public void update(City city) {
        cityDao.save(city);
    }

    @Override
    public void deleteById(String id) {
        cityDao.deleteById(id);
    }

    @Override
    public City findById(String id) {
        return cityDao.findById(id).get();
    }

    @Override
    public List<City> findAll() {
        return cityDao.findAll();
    }
}
