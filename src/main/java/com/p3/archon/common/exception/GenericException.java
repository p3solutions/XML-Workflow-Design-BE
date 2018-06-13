package com.p3.archon.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericException extends RuntimeException {

  private static final Logger logger = LoggerFactory.getLogger(GenericException.class);

  public GenericException(final String message, final Throwable exception) {
    super(message, exception);
    logger.error(message, exception);
  }

  public GenericException(final Throwable exception) {
    super(exception.getMessage(), exception);
    logger.error(exception.getMessage(), exception);
  }

  public GenericException(final String message) {
    super(message);
    logger.error(message);
  }

  public static void throwIfFalse(boolean expression, final String message) {
    if (!expression) {
      throw new GenericException(message);
    }
  }

  public static void throwIfTrue(boolean expression, final String message) {
    if (expression) {
      throw new GenericException(message);
    }
  }
}
