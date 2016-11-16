package me.jiangcai.gaa.model;

/**
 * @author CJ
 */
public interface District {
    String getName();

    String getShortName();

    int getWeight();

    String getCallingCode();

    String getPostalCode();

    String getChanpayCode();

    District getSuperior();

    Country getCountry();
}
