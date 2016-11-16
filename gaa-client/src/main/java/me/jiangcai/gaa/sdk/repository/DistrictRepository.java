package me.jiangcai.gaa.sdk.repository;

import me.jiangcai.gaa.model.Country;
import me.jiangcai.gaa.model.District;

import java.io.IOException;
import java.util.Locale;

/**
 * @author CJ
 */
public interface DistrictRepository extends RestRepository<District>{

    District byChanpayCode(Country country,String code) throws IOException;
    District byCallingCode(Country country,String code) throws IOException;
    District byPostalCode(Country country,String code) throws IOException;
    District byChanpayCode(Locale country, String code) throws IOException;
    District byCallingCode(Locale country,String code) throws IOException;
    District byPostalCode(Locale country,String code) throws IOException;

}
