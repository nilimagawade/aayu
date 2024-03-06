package com.ebixcash.aayu.exception;

// Source code is decompiled from a .class file using FernFlower decompiler.

public class ConfigException extends ApplicationException {
   private static final long serialVersionUID = 1L;

   protected ConfigException(Exception exception) {
      super(exception);
   }

   protected ConfigException(String exception) {
      super(new Exception(exception));
   }

   protected ConfigException(String[] as, Exception exception) {
      super(as, exception);
   }

   public Throwable getCause() {
      return super.getCause();
   }
}
