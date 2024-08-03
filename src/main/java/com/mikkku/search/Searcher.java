package com.mikkku.search;


/**
 * @author MiskuZero
 * @date 2024/8/4 0:35
 */
public interface Searcher<C, E, I> {

    /**
     * 在集合中搜索出特定的元素，并调用函数式接口执行操作。
     *
     * @param collections 被搜索的集合
     * @param element           搜索的元素
     * @param consumer    进行的操作（消费型函数式接口）
     */
    void search(C collections, E element, I consumer);

}
