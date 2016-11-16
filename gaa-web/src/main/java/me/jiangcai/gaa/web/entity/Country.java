package me.jiangcai.gaa.web.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Locale;
import java.util.Objects;

/**
 * 最顶级的行政单位--国家
 *
 * @author CJ
 */
@Setter
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "region")
})
@Entity
public class Country extends District {
    private Locale region;

    @Override
    public String toString() {
        return "Country{" +
                "region=" + region +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;
        if (!super.equals(o)) return false;
        Country country = (Country) o;
        return Objects.equals(region, country.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), region);
    }
}
