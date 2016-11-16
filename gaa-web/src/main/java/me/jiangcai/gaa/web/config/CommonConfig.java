package me.jiangcai.gaa.web.config;

import org.springframework.context.annotation.Configuration;

/**
 * 核心服务 加载者
 *
 * @author CJ
 */
@Configuration
//@Import({UpgradeSpringConfig.class, JdbcSpringConfig.class})
class CommonConfig {

//    @SuppressWarnings("SpringJavaAutowiringInspection")
//    @Autowired
//    private SystemStringRepository systemStringRepository;
//
//    @Bean
//    @SuppressWarnings("unchecked")
//    public VersionInfoService versionInfoService() {
//        final String versionKey = "version.database";
//        return new VersionInfoService() {
//
//            @Override
//            public <T extends Enum> T currentVersion(Class<T> type) {
//                SystemString systemString = systemStringRepository.findOne(versionKey);
//                if (systemString == null)
//                    return null;
//                return (T) Version.valueOf(systemString.getValue());
//            }
//
//            @Override
//            public <T extends Enum> void updateVersion(T currentVersion) {
//                SystemString systemString = systemStringRepository.findOne(versionKey);
//                if (systemString == null) {
//                    systemString = new SystemString();
//                    systemString.setId(versionKey);
//                }
//                systemString.setValue(currentVersion.name());
//                systemStringRepository.save(systemString);
//            }
//        };
//    }


}
