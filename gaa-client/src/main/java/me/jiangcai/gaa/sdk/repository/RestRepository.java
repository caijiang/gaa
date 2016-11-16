package me.jiangcai.gaa.sdk.repository;

import java.io.IOException;

/**
 * @author CJ
 */
public interface RestRepository<T> {

    /**
     * @return 它的内容类型
     */
    Class<T> itemClass();

    /**
     * 获取资源
     *
     * @param href         资源绝对URL
     * @param exceptedType 期待的返回类型
     * @return 可能是一个单独items 依赖于exceptedType
     * @throws IOException
     */
    Object resource(String href, Class<?> exceptedType) throws IOException;
}
