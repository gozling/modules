package com.gaozl.logback;

import org.slf4j.Logger;


public class Log {
  public final static String EXCEPTION = "EXCEPTION ";
  public final static String IGNORED = "IGNORED ";


  public static Logger getLogger() {
    return org.slf4j.LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2]
        .getClassName());
  }
}
