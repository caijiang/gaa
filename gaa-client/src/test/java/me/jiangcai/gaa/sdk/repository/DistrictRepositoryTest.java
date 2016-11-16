package me.jiangcai.gaa.sdk.repository;

import me.jiangcai.gaa.model.District;
import me.jiangcai.gaa.sdk.GAASpringConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Locale;

/**
 * @author CJ
 */
@ContextConfiguration(classes = GAASpringConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DistrictRepositoryTest {

    @Autowired
    private DistrictRepository districtRepository;

    @Test
    public void byId() throws IOException {
        District district = districtRepository.byChanpayCode(Locale.CHINA, "13001");
        System.out.println(district.toString());
        System.out.println(district.getName());
        System.out.println(district.getShortName());
        System.out.println(district.getCallingCode());
        System.out.println(district.getChanpayCode());
        System.out.println(district.getPostalCode());
        System.out.println(district.getWeight());
        final District superior = district.getSuperior();
        if (superior!=null){
            System.out.println(superior.getName());
        }

        System.out.println(district.getCountry().getName());
    }

}