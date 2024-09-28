package com.android.launcher3.netty;

import com.baehug.lib.nettyclient.command.CmdService;

import java.util.ArrayList;
import java.util.List;

public class ServiceImplManager {

    private List<CmdService> services = new ArrayList<>();

    public List<CmdService> getServices() {
        return services;
    }

    public static ServiceImplManager getInstance() {
        return new ServiceImplManager();
    }

    public void registerListener(CmdService service) {
        System.out.println("开始注册:" + service.getCommandType());
        if (!services.contains(service)) {
            services.add(service);
        }
    }

    public void unregisterListener(CmdService service) {
        if (services.contains(service)) {
            services.remove(service);
        }
    }

}
