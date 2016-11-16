package me.jiangcai.gaa.web.service.impl;

import me.jiangcai.gaa.web.entity.District;
import me.jiangcai.gaa.web.repository.CountryRepository;
import me.jiangcai.gaa.web.repository.DistrictRepository;
import me.jiangcai.gaa.web.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * @author CJ
 */
@Service
public class DistrictServiceImpl implements DistrictService {

    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private CountryRepository countryRepository;

    @Override
    public District byCode(Locale locale, String code) {
        District district = countryRepository.findByRegionAndCallingCode(locale, code);
        if (district != null)
            return district;
        district = countryRepository.findByRegionAndPostalCode(locale, code);
        if (district != null)
            return district;

        district = districtRepository.findByCountry_RegionAndCallingCode(locale, code);
        if (district != null)
            return district;

        district = districtRepository.findByCountry_RegionAndPostalCode(locale, code);
        if (district != null)
            return district;

        return null;
    }
}
