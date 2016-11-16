package me.jiangcai.gaa.sdk.impl.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.gaa.sdk.repository.RestRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author CJ
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SingleItemHandler<T> extends AbstractResponseHandler<T> {

    private static final Log log = LogFactory.getLog(SingleItemHandler.class);

    private final Class<T> type;
    @Autowired
    private ObjectMapper clientObjectMapper;
    @Autowired
    private ApplicationContext applicationContext;

    public SingleItemHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    public T handleResponse(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() == 404)
            return null;
        return super.handleResponse(response);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T handleEntity(HttpEntity entity) throws IOException {
        JsonNode jsonItem = clientObjectMapper.readTree(entity.getContent());
//        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(type);

        Function<Method, String> toProperty = method -> {
            final String name = method.getName();
            if (!name.startsWith("get"))
                throw new IllegalAccessError(name + " is not a getter");
            return Introspector.decapitalize(name.substring(3));
//            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
//                if (method.equals(propertyDescriptor.getReadMethod()))
//                    return propertyDescriptor.getName();
//            }
//            return null;
        };

        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object data, Method method, Object[] args) throws Throwable {
                // 这个方法是 T 的 我就应该给予关注
                try {
                    type.getMethod(method.getName(), (Class[]) method.getParameterTypes());
                    // from method to property
                    String name = toProperty.apply(method);
                    if (name == null)
                        throw new IllegalAccessError("unknown property for method:" + method);
                    //
                    JsonNode jsonProperty = jsonItem.get(name);
                    if (jsonProperty != null) {
                        // 给予应该的类型
                        return clientObjectMapper.readValue(clientObjectMapper.treeAsTokens(jsonProperty), method.getReturnType());
                    }
                    // 去链接哪里找
                    JsonNode jsonLink = jsonItem.get("_links").get(name);
                    if (jsonLink == null) {
                        throw new IllegalAccessError(name + " can not find in " + jsonItem);
                    }
                    String href = jsonLink.get("href").asText();
                    RestRepository targetRepository = applicationContext.getBeansOfType(RestRepository.class).values().stream()
                            .filter(restRepository -> restRepository.itemClass() == method.getReturnType())
                            .findFirst()
                            .orElseThrow(IllegalAccessError::new);

                    return targetRepository.resource(href, method.getReturnType());

                } catch (Exception ignored) {
                    //与本系统无关
                }
                return method.invoke(jsonItem, args);
            }
        };
        return (T) Enhancer.create(type, invocationHandler);
    }
}
