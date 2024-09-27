package com.android.launcher3.common.network.resp;


import java.util.List;

public class ConfigResp {



    /**
     * 账号id
     */
    private String waAcctId;
    /**
     * 验证码
     */
    private String code;

    /**
     * sos 列表
     */
    private List<String> sosList;

    /**
     * 周期性任务
     */
    private  RepeatTaskBean repeatTask;

    /**
     * 上课禁用列表
     */
    private List<CourseDisabledBean> courseDisabledList;

    /**
     * 应用监督列表
     */
    private List<AppSwitchJsonDTOSBean> appSwitchJsonDTOS;

    public List<AppSwitchJsonDTOSBean> getAppSwitchJsonDTOS() {
        return appSwitchJsonDTOS;
    }

    public void setAppSwitchJsonDTOS(List<AppSwitchJsonDTOSBean> appSwitchJsonDTOS) {
        this.appSwitchJsonDTOS = appSwitchJsonDTOS;
    }

    public List<String> getSosList() {
        return sosList;
    }
    public String getWaAcctId() {
        if(waAcctId == null){
            return "";
        }
        return waAcctId;
    }

    public void setWaAcctId(String waAcctId) {
        this.waAcctId = waAcctId;
    }

    public String getCode() {
        if (code == null){
            return "";
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSosList(List<String> sosList) {
        this.sosList = sosList;
    }

    public RepeatTaskBean getRepeatTask() {
        return repeatTask;
    }

    public void setRepeatTask(RepeatTaskBean repeatTask) {
        this.repeatTask = repeatTask;
    }

    public List<CourseDisabledBean> getCourseDisabledList() {
        return courseDisabledList;
    }

    public void setCourseDisabledList(List<CourseDisabledBean> courseDisabledList) {
        this.courseDisabledList = courseDisabledList;
    }

    public class AppSwitchJsonDTOSBean {
        private String applicationId;
        private String appName;
        private Integer switchStatus;

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public Integer getSwitchStatus() {
            return switchStatus;
        }

        public void setSwitchStatus(Integer switchStatus) {
            this.switchStatus = switchStatus;
        }
    }

    public class RepeatTaskBean {
        /**
         * 定位周期（单位：秒，-1 未设置，最小60）
         */
        private Integer locationSec;
        /**
         * 血氧测量周期（单位：秒，-1 未设置，最小60）
         */
        private Integer sao2Sec;
        /**
         * 血压测量周期（单位：秒，-1 未设置，最小60）
         */
        private Integer bpSec;
        /**
         * 心率测量周期（单位：秒，-1 未设置，最小60）
         */
        private Integer bpmSec;
        /**
         * 开机时间（HH:mm，-1 未设置)
         */
        private String bootOnTime;
        /**
         * 关机时间（HH:mm，-1 未设置)
         */
        private String bootOffTime;

        public Integer getLocationSec() {
            return locationSec;
        }

        public void setLocationSec(Integer locationSec) {
            this.locationSec = locationSec;
        }

        public Integer getSao2Sec() {
            return sao2Sec;
        }

        public void setSao2Sec(Integer sao2Sec) {
            this.sao2Sec = sao2Sec;
        }

        public Integer getBpSec() {
            return bpSec;
        }

        public void setBpSec(Integer bpSec) {
            this.bpSec = bpSec;
        }

        public Integer getBpmSec() {
            return bpmSec;
        }

        public void setBpmSec(Integer bpmSec) {
            this.bpmSec = bpmSec;
        }

        public String getBootOnTime() {
            return bootOnTime;
        }

        public void setBootOnTime(String bootOnTime) {
            this.bootOnTime = bootOnTime;
        }

        public String getBootOffTime() {
            return bootOffTime;
        }

        public void setBootOffTime(String bootOffTime) {
            this.bootOffTime = bootOffTime;
        }
    }

    public class CourseDisabledBean {
        /**
         * 重复周期
         */
        private String weekSwitches;
        /**
         * 状态
         */
        private Integer status;
        /**
         * 开始时间（HH:mm)
         */
        private String startTime;
        /**
         * 禁用时间（HH:mm)
         */
        private String endTime;

        public String getWeekSwitches() {
            return weekSwitches;
        }

        public void setWeekSwitches(String weekSwitches) {
            this.weekSwitches = weekSwitches;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }

}
