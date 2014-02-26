package com.qqh.library.log;


import android.os.Environment;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.*;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: qiuqiaohua
 * Date: 12/17/13
 * Time: 2:03 PM
 */
public class LogConfigurator {
    private Level rootLevel = Level.DEBUG;
    private String filePattern = "%d - [%p::%c::%C] - %m%n";
    private String logCatPattern = "%m%n";
    private String fileNamePattern = Environment.getExternalStorageDirectory() + File.separator+"log.%d.%i.log";
    private int maxHistory=7;
    private String maxFileSize = "10MB";
    private int minIndex=0;
    private int maxIndex=10;
    private boolean useLogCatAppender = false;
    private boolean useSizeAndTimeBasedFNATP = false;
    private boolean useSizeBasedTriggeringPolicy = false;
    private boolean useTimeBasedRollingPolicy = false;
    private boolean useFileAppender=false;
    private boolean statusPrint=false;
    private boolean append=false;

    public LogConfigurator() {
    }

    /**
     * @param fileNamePattern Name of the log file
     */
    public LogConfigurator(final String fileNamePattern) {
        setFilePattern(fileNamePattern);
    }

    /**
     * @param fileNamePattern  Name of the log file
     * @param rootLevel Log level for the root logger
     */
    public LogConfigurator(final String fileNamePattern, final Level rootLevel) {
        this(fileNamePattern);
        setRootLevel(rootLevel);
    }

    /**
     * @param fileNamePattern Name of the log file
     * @param rootLevel Log level for the root logger
     * @param filePattern Log pattern for the file appender
     */
    public LogConfigurator(final String fileNamePattern, final Level rootLevel, final String filePattern) {
        this(fileNamePattern);
        setRootLevel(rootLevel);
        setFilePattern(filePattern);
    }

    /**
     * @param fileNamePattern Name of the log file
     * @param maxHistory maxHistory of backed up log files
     * @param maxFileSize Maximum size of log file until rolling
     * @param filePattern  Log pattern for the file appender
     * @param rootLevel Log level for the root logger
     */
    public LogConfigurator(final String fileNamePattern, final int maxHistory,
                           final String maxFileSize, final String filePattern, final Level rootLevel) {
        this(fileNamePattern, rootLevel, filePattern);
        setMaxHistory(maxHistory);
        setMaxFileSize(maxFileSize);
    }

    public void configure() {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);



        if(isUseSizeAndTimeBasedFNATP()) {
            configureRollingFileAppenderWithSizeAndTimeBasedFNATP(context, root);
        }
        if(isUseSizeBasedTriggeringPolicy()) {
            configureRollingFileAppenderWithSizeBasedTriggeringPolicy(context, root);
        }
        if(isUseTimeBasedRollingPolicy()) {
            configureRollingFileAppenderWithTimeBasedRollingPolicy(context,root);
        }
        if(isUseFileAppender()){
            configureFileAppender(context, root);
        }

        if(isUseLogCatAppender()) {
            configureLogCatAppender(context,root);
        }

        root.setLevel(getRootLevel());
        // print any status messages (warnings, etc) encountered in logback config
        if(isStatusPrint())
        StatusPrinter.print(context);
    }

    /**
     * Sets the level of logger with name <code>loggerName</code>.
     * Corresponds to log4j.properties <code>log4j.logger.org.apache.what.ever=ERROR</code>
     * @param loggerName
     * @param level
     */
    public void setLevel(final String loggerName, final Level level) {
        ((Logger)LoggerFactory.getLogger(loggerName)).setLevel(level);
    }

    /**
     * 限制time和每个文件的大小
     * @param context
     * @param root
     */
    private void configureRollingFileAppenderWithSizeAndTimeBasedFNATP(LoggerContext context, Logger root ) {
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(isAppend());
        rollingFileAppender.setContext(context);
        // OPTIONAL: Set an active log file (separate from the rollover files).
        // If rollingPolicy.fileNamePattern already set, you don't need this.
//        rollingFileAppender.setFile(getFileName());

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        rollingPolicy.setFileNamePattern(getFileNamePattern());
        rollingPolicy.setMaxHistory(getMaxHistory());
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        SizeAndTimeBasedFNATP<ILoggingEvent>  sizeAndTimeBasedFNATP=new SizeAndTimeBasedFNATP<ILoggingEvent>();
        sizeAndTimeBasedFNATP.setMaxFileSize(getMaxFileSize());
        sizeAndTimeBasedFNATP.setTimeBasedRollingPolicy(rollingPolicy);
        sizeAndTimeBasedFNATP.setContext(context);
        sizeAndTimeBasedFNATP.start();

        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(getFilePattern());
        encoder.setContext(context);
        encoder.start();

        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        root.addAppender(rollingFileAppender);
    }

    /**
     * 限制时间
     * @param context
     * @param root
     */
    private void configureRollingFileAppenderWithTimeBasedRollingPolicy(LoggerContext context, Logger root ) {



        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(isAppend());
        rollingFileAppender.setContext(context);
        // OPTIONAL: Set an active log file (separate from the rollover files).
        // If rollingPolicy.fileNamePattern already set, you don't need this.
//        rollingFileAppender.setFile(getFileName());


        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        rollingPolicy.setFileNamePattern(getFileNamePattern());
        rollingPolicy.setMaxHistory(getMaxHistory());
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.setContext(context);
        rollingPolicy.start();
        rollingFileAppender.setRollingPolicy(rollingPolicy);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(getFilePattern());
        encoder.setContext(context);
        encoder.start();

        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        root.addAppender(rollingFileAppender);
    }

    /**
     * 限制大小和下标
     * @param context
     * @param root
     */
    private void configureRollingFileAppenderWithSizeBasedTriggeringPolicy(LoggerContext context, Logger root ) {
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(isAppend());
        rollingFileAppender.setContext(context);
        // OPTIONAL: Set an active log file (separate from the rollover files).
        // If rollingPolicy.fileNamePattern already set, you don't need this.

        FixedWindowRollingPolicy fixedWindowRollingPolicy=new FixedWindowRollingPolicy();
        fixedWindowRollingPolicy.setFileNamePattern(getFileNamePattern());
        fixedWindowRollingPolicy.setMaxIndex(getMaxIndex());
        fixedWindowRollingPolicy.setMinIndex(getMinIndex());
        fixedWindowRollingPolicy.setParent(rollingFileAppender);
        fixedWindowRollingPolicy.start();
        SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggeringPolicy=new SizeBasedTriggeringPolicy<ILoggingEvent>();
        sizeBasedTriggeringPolicy.setMaxFileSize(getMaxFileSize());
        sizeBasedTriggeringPolicy.start();
        rollingFileAppender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
        rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(getFilePattern());
        encoder.setContext(context);
        encoder.start();

        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        root.addAppender(rollingFileAppender);
    }


    /**
     * 限制大小和下标
     * @param context
     * @param root
     */
    private void configureFileAppender(LoggerContext context, Logger root ) {
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setAppend(isAppend());
        fileAppender.setContext(context);
        // OPTIONAL: Set an active log file (separate from the rollover files).
        // If rollingPolicy.fileNamePattern already set, you don't need this.

        fileAppender.setFile(getFileNamePattern());
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(getFilePattern());
        encoder.setContext(context);
        encoder.start();

        fileAppender.setEncoder(encoder);
        fileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        root.addAppender(fileAppender);
    }

    private void configureLogCatAppender(LoggerContext context,ch.qos.logback.classic.Logger root ) {
        // setup LogcatAppender
        PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
        encoder2.setContext(context);
        encoder2.setPattern("[%thread] %msg%n");
        encoder2.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(context);
        logcatAppender.setEncoder(encoder2);
        logcatAppender.start();
        root.addAppender(logcatAppender);
    }

    /**
     * Return the log level of the root logger
     * @return Log level of the root logger
     */
    public Level getRootLevel() {
        return rootLevel;
    }

    /**
     * Sets log level for the root logger
     * @param level Log level for the root logger
     */
    public void setRootLevel(final Level level) {
        this.rootLevel = level;
    }


    public String getFilePattern() {
        return filePattern;
    }

    /**
     * 设置文件内的输出格式
     * 默认是  "%d - [%p::%c::%C] - %m%n"
     * @param filePattern
     */
    public void setFilePattern(final String filePattern) {
        this.filePattern = filePattern;
    }

    public String getLogCatPattern() {
        return logCatPattern;
    }

    /**
     *  设置logcat内输出的格式
     * @param logCatPattern
     */
    public void setLogCatPattern(final String logCatPattern) {
        this.logCatPattern = logCatPattern;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    /**
     *设置文件名的pattern
     * @param fileNamePattern
     */
    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }



    public String getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * 设置每个文件的最大尺寸 KB,MB,G
     * @param maxFileSize
     */
    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public boolean isUseSizeAndTimeBasedFNATP() {
        return useSizeAndTimeBasedFNATP;
    }

    public void setUseSizeAndTimeBasedFNATP(boolean useSizeAndTimeBasedFNATP) {
        if(useSizeAndTimeBasedFNATP){
        this.useSizeBasedTriggeringPolicy=false;
        this.useTimeBasedRollingPolicy=false;
        this.useFileAppender=false;
        }
        this.useSizeAndTimeBasedFNATP = useSizeAndTimeBasedFNATP;
    }

    public boolean isUseSizeBasedTriggeringPolicy() {
        return useSizeBasedTriggeringPolicy;
    }

    public void setUseSizeBasedTriggeringPolicy(boolean useSizeBasedTriggeringPolicy) {
        if(useSizeBasedTriggeringPolicy){
        this.useSizeAndTimeBasedFNATP=false;
        this.useTimeBasedRollingPolicy=false;
        this.useFileAppender=false;
        }
        this.useSizeBasedTriggeringPolicy = useSizeBasedTriggeringPolicy;
    }

    public boolean isUseTimeBasedRollingPolicy() {
        return useTimeBasedRollingPolicy;
    }

    public void setUseTimeBasedRollingPolicy(boolean useTimeBasedRollingPolicy) {
        if(useTimeBasedRollingPolicy){
        this.useSizeAndTimeBasedFNATP=false;
        this.useSizeBasedTriggeringPolicy=false;
        this.useFileAppender=false;
        }
        this.useTimeBasedRollingPolicy = useTimeBasedRollingPolicy;
    }

    public boolean isUseFileAppender() {
        return useFileAppender;
    }

    public void setUseFileAppender(boolean useFileAppender) {
        if(useFileAppender){
        this.useSizeAndTimeBasedFNATP=false;
        this.useSizeBasedTriggeringPolicy=false;
        this.useTimeBasedRollingPolicy=false;
        }
        this.useFileAppender = useFileAppender;
    }

    /**
     * Returns true, if LogcatAppender should be used
     * @return True, if LogcatAppender should be used
     */
    public boolean isUseLogCatAppender() {
        return useLogCatAppender;
    }

    /**
     * If set to true, LogCatAppender will be used for logging
     * @param useLogCatAppender If true, LogCatAppender will be used for logging
     */
    public void setUseLogCatAppender(final boolean useLogCatAppender) {
        this.useLogCatAppender = useLogCatAppender;
    }


    public int getMaxHistory() {
        return maxHistory;
    }

    /**
     * 设置日志最大保存时间
     * @param maxHistory
     */
    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }


    public int getMinIndex() {
        return minIndex;
    }

    /**
     * 最小下标
     * @param minIndex
     */
    public void setMinIndex(int minIndex) {
        this.minIndex = minIndex;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    /**
     * 最大下标
     * @param maxIndex
     */
    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }


    public boolean isStatusPrint() {
        return statusPrint;
    }

    /**
     * 打印状态信息
     * @param statusPrint
     */
    public void setStatusPrint(boolean statusPrint) {
        this.statusPrint = statusPrint;
    }


    public boolean isAppend() {
        return append;
    }

    /**
     * 文件覆盖还是结尾加入
     * @param append
     */
    public void setAppend(boolean append) {
        this.append = append;
    }
}