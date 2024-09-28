package com.android.launcher3.moudle.touchup.view;

/**
 * Create by pengmin on 2024/9/27 .
 */
public class ViewInfo {
        int position = 0;
        int height = 0;
        int width = 0;
        int totalHeight = 0;
        int totalWidth = 0;
        int overDist = 0;

        @Override
        public String toString() {
            return "ViewInfo{" +
                    "position=" + position +
                    "height=" + height +
                    ", width=" + width +
                    ", totalHeight=" + totalHeight +
                    ", totalWidth=" + totalWidth +
                    ", overDist=" + overDist +
                    '}';
        }
}
