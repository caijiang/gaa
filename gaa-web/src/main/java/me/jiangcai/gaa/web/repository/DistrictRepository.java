package me.jiangcai.gaa.web.repository;

import me.jiangcai.gaa.web.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Locale;

/**
 * @author CJ
 */
public interface DistrictRepository extends JpaRepository<District, Long> {

    @RestResource(exported = false)
    @Override
    void delete(Long aLong);

    @RestResource(exported = false)
    @Override
    void delete(District entity);

    @RestResource(exported = false)
    @Override
    void delete(Iterable<? extends District> entities);

    @RestResource(exported = false)
    @Override
    void deleteAll();

    @RestResource(exported = false)
    @Override
    <S extends District> S save(S entity);

    List<District> findByChanpayCodeNotNull();

    //    @Query("select d from District as d where (d.superior=?1 or d.superior.superior=?1) and d.name = ?2")
    District findBySuperior_SuperiorAndName(District district, String name);

    District findByCountry_RegionAndName(Locale country, String name);

    District findBySuperiorAndName(District district, String name);

    District findByCountry_RegionAndId1(Locale country, String id);

    District findByCountry_RegionAndId2(Locale country, String id);

    District findByCountry_RegionAndChanpayCode(@Param("country") Locale country,@Param("id") String id);

    District findByCountry_RegionAndPostalCode(Locale country, String id);

    District findByCountry_RegionAndCallingCode(Locale country, String id);

}
