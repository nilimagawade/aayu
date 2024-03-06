package com.ebixcash.aayu.exception;

// Source code is decompiled from a .class file using FernFlower decompiler.

import java.sql.SQLException;
import java.util.Locale;

public class ApplicationException extends AbstractException {
   private static final long serialVersionUID = 1L;

   protected ApplicationException(Exception exp) {
      super(exp);
   }

   protected ApplicationException(String cause, SQLException exp) {
      super(cause, exp);
   }

   /*protected ApplicationException(String cause, OracleXMLSQLException exp) {
      super(cause, exp);
   }*/

  /* protected (String userDefinedErrorCode, String message) {
      super(userDefinedErrorCode, message);
   }*/

   protected ApplicationException(String exp) {
      super(new Exception(exp));
   }

   protected ApplicationException(String[] arguments, Exception exception) {
      super(arguments, exception);
   }

   protected String[] getArguments() {
      return super.getArguments();
   }

   public Throwable getCause() {
      return super.getCause();
   }

   public String getDetail() {
      return super.getDetail();
   }

   public String getDetail(String locale) {
      return super.getDetail(locale);
   }

   public String getMessage() {
      return super.getMessage();
   }

//   public String getMessage(Locale locale) {
//      return super.getMessage(locale);
//   }
}
