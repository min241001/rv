package com.renny.contractgridview.bean;

import android.view.View;

/**
 * Create by pengmin on 2024/9/27 .
 */
public class ViewInfo {
        public int position = 0;
        public int height = 0;
        public int width = 0;
        public int totalHeight = 0;
        public int totalWidth = 0;
        public int overDist = 0;
        public View v;

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
