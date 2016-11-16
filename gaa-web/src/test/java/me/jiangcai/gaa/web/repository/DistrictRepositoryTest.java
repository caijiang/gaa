package me.jiangcai.gaa.web.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import me.jiangcai.chanpay.Dictionary;
import me.jiangcai.chanpay.model.Province;
import me.jiangcai.gaa.web.BaseTest;
import me.jiangcai.gaa.web.service.InitService;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class DistrictRepositoryTest extends BaseTest {

    @Test
    public void abc() throws Exception {
        // 匿名用户 只可以GET 不可以修改
        // findByCountry_RegionAndChanpayCode
        mockMvc.perform(get("/"))
                .andDo(print());

        mockMvc.perform(get("/districts"))
                .andDo(print())
                .andExpect(status().isOk());

        // 12714 12817 12816 132 133 134
        Province province = null;
        while (province == null || InitService.ignoreChanpayIds().contains(province.getId()))
            province = Dictionary.findAll(Province.class).stream()
                    .max(new RandomComparator()).orElse(null);

        String shortName = province.getShortName();
        if (InitService.chanpayShortNameBatch().containsKey(shortName))
            shortName = InitService.chanpayShortNameBatch().get(shortName);

        String selfUrl = JsonPath.read(mockMvc.perform(get("/districts/search/findByCountry_RegionAndChanpayCode")
                .param("country", "zh_CN")
                .param("id", province.getId()))
//                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.shortName").value(shortName))
                .andReturn().getResponse().getContentAsString(), "$._links.self.href");

        assertNotWritable("/districts", selfUrl);
    }

    private void assertNotWritable(String collectionsUri, String itemUri) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        if (collectionsUri != null) {
            HashMap<String, Object> data = new HashMap<>();
            mockMvc.perform(post(collectionsUri).contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsBytes(data)))
                    .andExpect(status().isMethodNotAllowed());
        }

        // 修改某一个属性? 不会
        if (itemUri != null) {
            String propertyName = null;
            if (itemUri.contains("/districts/"))
                propertyName = "name";

            HashMap<String, Object> data = new HashMap<>();
            data.put(propertyName,randomEmailAddress());

            mockMvc.perform(put(itemUri).content(objectMapper.writeValueAsBytes(data)))
                    .andDo(print())
                    .andExpect(status().isMethodNotAllowed());

            mockMvc.perform(delete(itemUri))
                    .andDo(print())
                    .andExpect(status().isMethodNotAllowed());
        }
    }

    private class RandomComparator implements java.util.Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            return random.nextInt();
        }
    }
}