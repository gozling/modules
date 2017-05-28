package com.gaozl.logback;

public class LoggerFactory {
  private static Logger logger = null;
  private static final NullLogger nullLogger = new NullLogger();

  public static synchronized void setOpLogger(Logger logger) {
    LoggerFactory.logger = logger;
  }

  public static synchronized Logger getLogger() {
    if (logger != null) {
      return logger;
    }
    return nullLogger;
  }

}
