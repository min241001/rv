package com.android.launcher3.moudle.selector.view;

import com.android.launcher3.common.bean.SelectorBean;

import java.util.List;

/**
 * @Author: shensl
 * @Description：
 * @CreateDate：2023/12/15 18:30
 * @UpdateUser: shensl
 */
public interface CommomSelectorView {

    void scrollToPosition(int position);

    void setData(List<SelectorBean> selectorBeans, int defaultId);
}
