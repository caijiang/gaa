package me.jiangcai.gaa.sdk.impl;

import me.jiangcai.gaa.model.Country;
import me.jiangcai.gaa.model.District;
import me.jiangcai.gaa.sdk.repository.DistrictRepository;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;

/**
 * @author CJ
 */
@Service
public class DistrictRepositoryImpl extends AbstractRepository<District> implements DistrictRepository {

    public DistrictRepositoryImpl() {
        super("/districts");
    }

    @Override
    public District byChanpayCode(Country country, String code) throws IOException {
        return byChanpayCode(country.getRegion(), code);
    }

    @Override
    public District byCallingCode(Country country, String code) throws IOException {
        return null;
    }

    @Override
    public District byPostalCode(Country country, String code) throws IOException {
        return null;
    }

    @Override
    public District byChanpayCode(Locale country, String code) throws IOException {
        return searchItem("/findByCountry_RegionAndChanpayCode"
                , new BasicNameValuePair("country", country.toString())
                , new BasicNameValuePair("id", code));
    }

    @Override
    public District byCallingCode(Locale country, String code) throws IOException {
        return null;
    }

    @Override
    public District byPostalCode(Locale country, String code) throws IOException {
        return null;
    }
}
