package com.android.launcher3.common.db.pullwidget;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "widget")
public class WidgetBean {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo
    private String name;        // 控件名称

    @ColumnInfo
    private boolean added;      // 记录控件是否已添加

    @ColumnInfo
    private int type;           // 控件类型，用于判断是哪个控件

    @ColumnInfo
    private boolean select;     // 是否被选中

    public WidgetBean() {

    }

    public WidgetBean(Builder builder) {
        this.name = builder.name;
        this.added = builder.added;
        this.type = builder.type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public static class Builder {
        private String name;
        private boolean added;
        private int type;
        private boolean select;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAdded(boolean added) {
            this.added = added;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setSelect(boolean select) {
            this.select = select;
            return this;
        }

        public WidgetBean build() {
            return new WidgetBean(this);
        }
    }

    @Override
    public String toString() {
        return "WidgetBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", added=" + added +
                ", type=" + type +
                ", select=" + select +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        WidgetBean bean = (WidgetBean) o;
        return bean.getType() == this.getType();
    }
}
