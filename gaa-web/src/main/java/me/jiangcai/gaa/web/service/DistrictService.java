package me.jiangcai.gaa.web.service;

import me.jiangcai.gaa.web.entity.District;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * 区域服务
 *
 * @author CJ
 */
public interface DistrictService {

    /**
     * @param locale 国家语言
     * @param code   可能是电话编码也可能是邮政编码
     * @return 国家或者可能是相关的一个行政区域
     */
    @Transactional(readOnly = true)
    District byCode(Locale locale, String code);

}
