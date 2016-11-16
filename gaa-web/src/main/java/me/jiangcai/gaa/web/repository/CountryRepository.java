package me.jiangcai.gaa.web.repository;

import me.jiangcai.gaa.web.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Locale;

/**
 * @author CJ
 */
//@RestResource(exported = false)
public interface CountryRepository extends JpaRepository<Country, Long> {

    @RestResource(exported = false)
    @Override
    <S extends Country> S save(S entity);

    @RestResource(exported = false)
    @Override
    void delete(Long aLong);

    @RestResource(exported = false)
    @Override
    void delete(Country entity);

    @RestResource(exported = false)
    @Override
    void delete(Iterable<? extends Country> entities);

    @RestResource(exported = false)
    @Override
    void deleteAll();

    Country findByRegionAndCallingCode(Locale region, String code);

    Country findByRegionAndPostalCode(Locale region, String code);

}
