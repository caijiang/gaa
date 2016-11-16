package me.jiangcai.gaa.sdk.impl;

import me.jiangcai.gaa.model.Country;
import me.jiangcai.gaa.sdk.repository.CountryRepository;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class CountryRepositoryImpl extends AbstractRepository<Country> implements CountryRepository {

    public CountryRepositoryImpl() {
        super("/countries");
    }

}
