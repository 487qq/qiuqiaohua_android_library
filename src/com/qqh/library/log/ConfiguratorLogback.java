package com.qqh.library.log;

import android.os.Environment;
import ch.qos.logback.classic.Level;
//import de.mindpipe.android.logging.log4j.LogCatAppender;
//import org.apache.log4j.*;
//import org.apache.log4j.helpers.LogLog;

import java.io.File;


/**
 * Created with IntelliJ IDEA.
 * User: qiuqiaohua
 * Date: 12/17/13
 * Time: 11:28 AM
 *
 * Need
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *
 */
public class ConfiguratorLogback {
    public static void configure() {
         LogConfigurator logConfigurator = new LogConfigurator();
         String filePattern = "%d - [%p::%C] - %m%n";
        logConfigurator.setFileNamePattern(Environment.getExternalStorageDirectory() + File.separator + "log.%d{yyyy-MM-dd}.log");
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setFilePattern(filePattern);
        // Set log level of a specific logger
        logConfigurator.setUseTimeBasedRollingPolicy(true);    //每天生成一个版本
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.setMaxHistory(30);
        logConfigurator.setAppend(true);     //文件覆盖
        logConfigurator.setStatusPrint(true);
        logConfigurator.configure();
    }


    public static void configure_noWriteSdCard() {
        LogConfigurator logConfigurator = new LogConfigurator();
        String filePattern = "%d - [%p::%C] - %m%n";
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.configure();
    }


}
