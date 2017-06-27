package ru.javacourse;


import ru.javacourse.dao.RegionDao;
import ru.javacourse.model.Region;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;


@Stateless
public class RegionDaoImpl implements RegionDao {

    @PersistenceContext(unitName = "testPU")
    private EntityManager em;


    @Override
    public Region getRegion(Integer id) {
        return em.find(Region.class, id);
    }

    @Override
    public List<Region> getAllRegions() {
        List<Region> allRegions = em.createNamedQuery("Region.GetAll").getResultList();
        return allRegions;
    }

    @Override
    public Serializable createRegion(Region region) {
        em.persist(region);
        return region.getRegionId();
    }

    @Override
    public void removeRegion(Region region) {
        region = em.merge(region);
        em.remove(region);
    }


}
