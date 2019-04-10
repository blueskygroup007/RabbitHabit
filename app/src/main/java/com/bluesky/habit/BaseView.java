package com.bluesky.habit;

/**
 * @author BlueSky
 * @date 2019/3/4
 */
//Todo 这里的泛型继承了BasePresenter
public interface BaseView<T extends BasePresenter> {
    /**
     * 给V指定P
     *
     * @param presenter
     */
    void setPresenter(T presenter);
}
