package com.csc380.teame.airbornecpsserver;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusData;
import org.apache.logging.log4j.status.StatusListener;
//import java.lang.module.Configuration;
import java.net.URI;
import java.net.URLClassLoader;

public class loggercheck {
    public Level getStatusLevel() {
        return Level.ERROR;
    }

    public void log(StatusData data) {
        try {
            throw new Exception("Internal log4j error detected: "
                    + data.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        //var x =  ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
//        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//        Configuration config = ctx.getConfiguration();
//        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
//        loggerConfig.setLevel(Level.ALL);
//        ctx.updateLoggers();
        Logger logger = LogManager.getLogger(loggercheck.class);

        logger.debug("Debug Message Logged !!!");
        logger.info("Info Message Logged !!!");
        logger.error("Error Message Logged !!!", new NullPointerException("NullError"));
    }
}
