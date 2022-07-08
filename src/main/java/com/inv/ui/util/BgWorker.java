package com.inv.ui.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author XDSSWAR
 * This Class is used to create a single Service Instance
 */
public class BgWorker {

    private static ScheduledExecutorService instance =null;

    /**
     * Private Constructor
     */
    private BgWorker(){}

    /**
     * Get Service Instance
     * @return ScheduledExecutorService
     */
    public static synchronized ScheduledExecutorService getInstance(){
        if (instance ==null){
            instance = Executors.newSingleThreadScheduledExecutor();
        }
        return instance;
    }
}
