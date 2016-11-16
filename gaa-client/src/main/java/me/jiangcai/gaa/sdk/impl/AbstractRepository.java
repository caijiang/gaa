package me.jiangcai.gaa.sdk.impl;

import me.jiangcai.gaa.sdk.impl.response.SingleItemHandler;
import me.jiangcai.gaa.sdk.repository.RestRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;

/**
 * @author CJ
 */
public abstract class AbstractRepository<T> implements RestRepository<T> {

    private static final Log log = LogFactory.getLog(AbstractRepository.class);

    private static final RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();

    private CloseableHttpClient newClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder = builder.setDefaultRequestConfig(defaultRequestConfig);
        if (environment.acceptsProfiles("test")) {
            builder.setSSLHostnameVerifier(new NoopHostnameVerifier());
        }

        return builder.build();
    }

    @Autowired
    private Environment environment;
    @Autowired
    private ApplicationContext applicationContext;
    private final String collectionUri;
    private final Class<T> type;

    private String collectionUrl;

    /**
     * @param collectionUri 比如 /peoples
     */
    @SuppressWarnings("unchecked")
    protected AbstractRepository(String collectionUri) {
        this.collectionUri = collectionUri;
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @PostConstruct
    public void init() {
        collectionUrl = environment.getProperty("me.jiangcai.gaa.url", "http://gaa.jiangcai.me") + collectionUri;
    }

    @Override
    public Object resource(String href, Class<?> exceptedType) throws IOException {
        // TODO 可能会有其他的什么 什么
        try (CloseableHttpClient client = newClient()) {
            HttpGet search = new HttpGet(href);
            return client.execute(search, applicationContext.getBean(SingleItemHandler.class, type));
        }
    }

    /**
     * 单个资源搜索获取
     *
     * @param searchUri  搜索URI
     * @param parameters 查询参数
     * @return 唯一的资源
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected T searchItem(String searchUri, NameValuePair... parameters) throws IOException {
        try (CloseableHttpClient client = newClient()) {
            HttpGet search = newGet("/search" + searchUri, parameters);
            return client.execute(search, (SingleItemHandler<T>) applicationContext.getBean(SingleItemHandler.class, type));
        }
    }

    private HttpGet newGet(String uri, NameValuePair... parameters) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder(collectionUrl);
        stringBuilder.append(uri);
        if (parameters.length > 0) {
            stringBuilder.append("?");
            for (NameValuePair nameValuePair : parameters) {
                stringBuilder.append(nameValuePair.getName())
                        .append("=")
                        .append(URLEncoder.encode(nameValuePair.getValue(), "UTF-8"))
                        .append("&");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
        }

        return new HttpGet(stringBuilder.toString());
    }

    @Override
    public Class<T> itemClass() {
        return type;
    }
}
