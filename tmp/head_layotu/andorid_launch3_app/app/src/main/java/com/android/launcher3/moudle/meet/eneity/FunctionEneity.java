package com.android.launcher3.moudle.meet.eneity;

public class FunctionEneity {

    private int funcIcon;
    private String funcName;
    private int flag;

    @Override
    public String toString() {
        return "FunctionEneity{" +
                "funcIcon='" + funcIcon + '\'' +
                ", funcName='" + funcName + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }

    public int getFuncIcon() {
        return funcIcon;
    }

    public void setFuncIcon(int funcIcon) {
        this.funcIcon = funcIcon;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public FunctionEneity(int funcIcon, String funcName, int flag) {
        this.funcIcon = funcIcon;
        this.funcName = funcName;
        this.flag = flag;
    }

    public FunctionEneity() {
    }
}
