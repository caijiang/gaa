package me.jiangcai.gaa.web.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.util.Objects;

/**
 * 行政单位
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String name;
    @Column(length = 50)
    private String shortName;
    /**
     * 排序的权重,越大排序越靠前
     */
    private int weight;

    // 行政单位经常会有不同的id对应他们
    @Column(length = 20)
    private String callingCode;
    @Column(length = 20)
    private String postalCode;
    @Column(length = 20)
    private String chanpayCode;
    @Column(length = 20)
    private String id1;
    @Column(length = 20)
    private String id2;

    @ManyToOne
    private Country country;

    /**
     * 上级行政单位
     */
    @ManyToOne
    private District superior;

    public String toFullName(boolean includeCountry) {
        District current = this;
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            stringBuilder.insert(0, current.name);
            // 然后去上级
            current = current.superior;
            if (current == null)
                break;
            if (!includeCountry && current instanceof Country)
                break;
        }
        return stringBuilder.toString();
    }

    public String toFullName() {
        return toFullName(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof District)) return false;
        District district = (District) o;
        return Objects.equals(callingCode, district.callingCode) &&
                Objects.equals(postalCode, district.postalCode) &&
                Objects.equals(chanpayCode, district.chanpayCode) &&
                Objects.equals(id1, district.id1) &&
                Objects.equals(id2, district.id2) &&
                Objects.equals(country, district.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callingCode, postalCode, chanpayCode, id1, id2, country);
    }

    @Override
    public String toString() {
        return "District{" +
                "superior=" + superior +
                ", country=" + country +
                ", postalCode='" + postalCode + '\'' +
                ", chanpayCode='" + chanpayCode + '\'' +
                ", callingCode='" + callingCode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
