package me.jiangcai.gaa.web.service;

import me.jiangcai.chanpay.Dictionary;
import me.jiangcai.chanpay.model.Province;
import me.jiangcai.gaa.web.entity.Country;
import me.jiangcai.gaa.web.entity.District;
import me.jiangcai.gaa.web.repository.CountryRepository;
import me.jiangcai.gaa.web.repository.DistrictRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author CJ
 */
@Service
public class InitService {

    private static final Log log = LogFactory.getLog(InitService.class);

    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private DistrictService districtService;

    @PostConstruct
    @Transactional
    public void init() throws SQLException, ClassNotFoundException, IOException {
        if (countryRepository.count() == 0) {
            long startTime = System.currentTimeMillis();
            // 构建中国默认数据
            // Delimiter
            String delimiter = ";";
            try (InputStream data = new ClassPathResource("/china.sql").getInputStream()) {
                Scanner scanner = new Scanner(data, "UTF-8").useDelimiter(delimiter);

                try (Connection connection = tempDatabaseConnection()) {
                    // 插入数据
                    try (Statement statement = connection.createStatement()) {
                        while (scanner.hasNext()) {
                            // Get statement
                            String rawStatement = scanner.next() + delimiter;
                            statement.addBatch(rawStatement);
                        }
                        statement.executeBatch();
                    }
                    // 显示数据
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleDataSource(connection));

//                    int x = jdbcTemplate.queryForObject("SELECT count(*) FROM fi_dict_area", (rs, rowNum) -> {
//                        return rs.getInt(1);
//                    });
//
//                    log.info("fi_dict_area size:" + x);
                    //添加国家先
                    Country country = new Country();
                    country.setRegion(Locale.CHINA);
                    country.setCallingCode("86");
                    country.setPostalCode("86");
                    country.setName("中国");
                    country.setShortName("中国");
                    Country china = countryRepository.save(country);

                    RowMapper<District> toDistrict = new RowMapper<District>() {

                        @Override
                        public District mapRow(ResultSet rs, int rowNum) throws SQLException {
                            String postalCode = rs.getString("code").trim();
                            District district = districtService.byCode(china.getRegion(), postalCode);
                            if (district == null) {
                                district = new District();
                                district.setCountry(china);
                                district.setShortName(rs.getString("name_cn").trim());
                                district.setName(rs.getString("name_cn").trim());
                                district.setPostalCode(postalCode);
                                String pCode = rs.getString("temp_pcode");
                                if (pCode != null) {
                                    district.setSuperior(districtService.byCode(china.getRegion(), pCode.trim()));
                                } else
                                    return null;// 只加中国
                                district = districtRepository.save(district);
                            }
                            return district;
                        }
                    };

                    // 加载数据库
                    jdbcTemplate.query("SELECT * FROM fi_dict_area", toDistrict);
                    // 加载chanpay的模块
                    // 一些数据补丁
                    Map<String, String> chanpayBatch = chanpayShortNameBatch();
                    List<String> ignoreChanpayIds = ignoreChanpayIds();


                    Dictionary.findAll(Province.class).forEach(province -> {
                        //寻找这个省
                        try {
                            if (ignoreChanpayIds.contains(province.getId()))
                                return;
                            String provinceName = province.getShortName();
                            if (chanpayBatch.containsKey(provinceName))
                                provinceName = chanpayBatch.get(provinceName);
                            District district
                                    = districtRepository.findBySuperiorAndName(china, provinceName);
                            if (district == null) {
                                throw new IllegalStateException("unknown province of " + province);
//                                log.error("unknown province of " + province);
                            } else {
                                district.setChanpayCode(province.getId());
                                districtRepository.save(district);
                                //寻找城市
                                province.getCityList().forEach(city -> {
                                    if (ignoreChanpayIds.contains(city.getId()))
                                        return;
                                    //宣城市 宣城地区
                                    String cityName = city.getShortName();
                                    if (chanpayBatch.containsKey(cityName))
                                        cityName = chanpayBatch.get(cityName);
                                    try {
                                        District subDistrict = districtRepository.findBySuperiorAndName(district, cityName);
                                        if (subDistrict == null)
                                            subDistrict = districtRepository.findBySuperior_SuperiorAndName(district, cityName);

                                        if (subDistrict == null) {
                                            throw new IllegalStateException("unknown city of " + city);
//                                            log.error("unknown city of " + city);
                                        } else {
                                            subDistrict.setChanpayCode(city.getId());
                                            districtRepository.save(subDistrict);
                                        }

                                    } catch (IncorrectResultSizeDataAccessException ex) {
//                                        log.error("bad city of " + city, ex);
                                        throw ex;
                                    }

                                });
                            }
                        } catch (IncorrectResultSizeDataAccessException ex) {
//                            log.error("bad province of " + province, ex);
                            throw ex;
                        }


                    });

//                    districtRepository.findAll().stream()
//                            .map(District::getChanpayCode)
//                            .forEach(System.out::println);
                    log.debug("cost " + (System.currentTimeMillis() - startTime) + " ms");
                }
            }


        }
    }

    public static List<String> ignoreChanpayIds() {
//        11:29:05 [main] ERROR me.jiangcai.gaa.web.service.InitService - unknown city of City(super=AbstractGeographyModel(super=AbstractModel(id=12714, name=樟木口岸镇), shortName=樟木口岸镇))
//        11:29:05 [main] ERROR me.jiangcai.gaa.web.service.InitService - unknown city of City(super=AbstractGeographyModel(super=AbstractModel(id=12817, name=图木舒克市), shortName=图木舒克市))
//        11:29:05 [main] ERROR me.jiangcai.gaa.web.service.InitService - unknown city of City(super=AbstractGeographyModel(super=AbstractModel(id=12816, name=阿拉尔市), shortName=阿拉尔市))
//        11:29:05 [main] ERROR me.jiangcai.gaa.web.service.InitService - unknown province of Province(super=AbstractGeographyModel(super=AbstractModel(id=132, name=香港特别行政区 ), shortName=香港), cityList=[City(super=AbstractGeographyModel(super=AbstractModel(id=13106, name=九龙), shortName=九龙)), City(super=AbstractGeographyModel(super=AbstractModel(id=13107, name=新界), shortName=新界)), City(super=AbstractGeographyModel(super=AbstractModel(id=13105, name=香港岛), shortName=香港岛))])
//        11:29:05 [main] ERROR me.jiangcai.gaa.web.service.InitService - unknown province of Province(super=AbstractGeographyModel(super=AbstractModel(id=133, name=澳门特别行政区), shortName=澳门), cityList=[City(super=AbstractGeographyModel(super=AbstractModel(id=13108, name=澳门半岛), shortName=澳门半岛)), City(super=AbstractGeographyModel(super=AbstractModel(id=13109, name=离岛), shortName=离岛))])
//        11:29:05 [main] ERROR me.jiangcai.gaa.web.service.InitService - unknown province of Province(super=AbstractGeographyModel(super=AbstractModel(id=134, name=台湾省), shortName=台湾省), cityList=[City(super=AbstractGeographyModel(super=AbstractModel(id=13126, name=云林县), shortName=云林县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13115, name=南投县), shortName=南投县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13128, name=台东县), shortName=台东县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13113, name=台中市), shortName=台中市)), City(super=AbstractGeographyModel(super=AbstractModel(id=13110, name=台北市), shortName=台北市)), City(super=AbstractGeographyModel(super=AbstractModel(id=13112, name=台南市), shortName=台南市)), City(super=AbstractGeographyModel(super=AbstractModel(id=13125, name=嘉义县), shortName=嘉义县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13118, name=嘉义市), shortName=嘉义市)), City(super=AbstractGeographyModel(super=AbstractModel(id=13116, name=基隆市), shortName=基隆市)), City(super=AbstractGeographyModel(super=AbstractModel(id=13120, name=宜兰县), shortName=宜兰县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13127, name=屏东县), shortName=屏东县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13124, name=彰化县), shortName=彰化县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13119, name=新北市), shortName=新北市)), City(super=AbstractGeographyModel(super=AbstractModel(id=13121, name=新竹县), shortName=新竹县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13117, name=新竹市), shortName=新竹市)), City(super=AbstractGeographyModel(super=AbstractModel(id=13122, name=桃园县), shortName=桃园县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13130, name=澎湖县), shortName=澎湖县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13129, name=花莲县), shortName=花莲县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13123, name=苗栗县), shortName=苗栗县)), City(super=AbstractGeographyModel(super=AbstractModel(id=13114, name=金门市), shortName=金门市)), City(super=AbstractGeographyModel(super=AbstractModel(id=13111, name=高雄市), shortName=高雄市))])
        return Arrays.asList(
                "12714"
                , "12817"
                , "12816"
                , "132"
                , "133"
                , "134"
        );
    }

    public static Map<String, String> chanpayShortNameBatch() {
        HashMap<String, String> chanpayBatch = new HashMap<>();
        chanpayBatch.put("西藏", "西藏自治区");
        chanpayBatch.put("宣城市", "宣城地区");
        chanpayBatch.put("池州市", "池州地区");
        chanpayBatch.put("定西市", "定西地区");
        chanpayBatch.put("庆阳市", "庆阳地区");
        chanpayBatch.put("陇南市", "陇南地区");
        chanpayBatch.put("崇左市", "崇左县");
        chanpayBatch.put("来宾市", "来宾县");
        chanpayBatch.put("黔东南州", "黔东南");
        chanpayBatch.put("黔南州", "黔南");
        chanpayBatch.put("黔西南州", "黔西南");
        chanpayBatch.put("恩施州", "恩施");
        chanpayBatch.put("株州市", "株洲市");
        chanpayBatch.put("湘西州", "湘西");
        chanpayBatch.put("延边州", "延边");
        chanpayBatch.put("抚州", "抚州地区");
        chanpayBatch.put("乌兰察布市", "乌兰察布盟");
        chanpayBatch.put("呼伦贝尔市", "呼伦贝尔盟");
        chanpayBatch.put("巴彦淖尔市", "巴彦淖尔盟");
        chanpayBatch.put("鄂尔多斯市", "伊克昭盟");
        chanpayBatch.put("中卫市", "中卫县");
        chanpayBatch.put("固原市", "固原县");
        chanpayBatch.put("果洛藏族自治州", "果洛州");
        chanpayBatch.put("海北藏族自治州", "海北州");
        chanpayBatch.put("海南藏族自治州", "海南州");
        chanpayBatch.put("海西蒙古族藏族自治州", "海西州");
        chanpayBatch.put("玉树藏族自治州", "玉树州");
        chanpayBatch.put("黄南藏族自治州", "黄南州");
        chanpayBatch.put("吕梁市", "吕梁地区");
        chanpayBatch.put("晋中市", "晋中地区");
        chanpayBatch.put("商洛市", "商洛地区");
        chanpayBatch.put("眉山市", "眉山地区");
        chanpayBatch.put("达州市", "达州地区");
        chanpayBatch.put("伊犁哈萨克自治州", "伊犁");
        chanpayBatch.put("克孜勒苏柯尔克孜自治州", "克孜勒苏柯尔");
        chanpayBatch.put("博尔塔拉蒙古自治州", "博尔塔拉州");
//                    chanpayBatch.put("图木舒克市", "池州地区");
        chanpayBatch.put("巴音郭楞蒙古自治州", "巴音郭楞州");
        chanpayBatch.put("昌吉回族自治州", "昌吉州");
//                    chanpayBatch.put("阿拉尔市", "池州地区");
        chanpayBatch.put("万州市", "万州区");
        chanpayBatch.put("黔江市", "黔江区");
        chanpayBatch.put("涪陵市", "涪陵区");

        chanpayBatch.put("恩施州", "恩施市");
        chanpayBatch.put("抚州市", "抚州地区");
        chanpayBatch.put("达州市", "达州市地区");
        chanpayBatch.put("山南地区（地区所在地）", "山南地区");
        chanpayBatch.put("日喀则地区(地区所在地)", "日喀则地区");
        chanpayBatch.put("昌都地区(地区所在地)", "昌都地区");
        chanpayBatch.put("林芝地区(地区所在地)", "林芝地区");
        chanpayBatch.put("那曲地区（地区所在地）", "那曲地区");
        chanpayBatch.put("阿里地区(地区所在地)", "阿里地区");

        chanpayBatch.put("达州市", "达川地区");
        return chanpayBatch;
    }

    private Connection tempDatabaseConnection() throws ClassNotFoundException, SQLException {

        Class.forName("org.h2.Driver");

        Connection connection
                = DriverManager.getConnection("jdbc:h2:mem:init;DB_CLOSE_DELAY=-1", "", "");

        connection.setAutoCommit(true);

        // 建表
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE fi_dict_area(`id` INT NULL " +
                    ",`pid` INT NULL " +
                    ",`code` VARCHAR(100) NULL " +
                    ",`temp_pcode` VARCHAR(100) NULL" +
                    ",`name_cn` VARCHAR(100) NULL " +
                    ",`all_name_cn` VARCHAR(100) NULL " +
                    ",`name_en` VARCHAR(100) NULL " +
                    ",`all_name_en` VARCHAR(100) NULL " +
                    ",`display_order` INT NULL " +
                    ",`status` INT NULL " +
                    ",`level` INT NULL " +
                    ")");
        }

        return connection;
    }

    private class SingleDataSource implements DataSource {
        private final Connection connection;

        public SingleDataSource(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return connection;
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return connection;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {

        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {

        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }
    }
}
