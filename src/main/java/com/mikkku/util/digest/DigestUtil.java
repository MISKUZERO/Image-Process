package com.mikkku.util.digest;

/**
 * @author MiskuZero
 * @date 2024/8/4 1:08
 */
public interface DigestUtil<T, H> {

    /**
     * 计算对象的哈希值。
     *
     * @param t 计算对象
     * @return 哈希值
     */
    H hash(T t);
}
