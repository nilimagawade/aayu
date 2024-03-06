package com.ebixcash.aayu.exception;

// Source code is decompiled from a .class file using FernFlower decompiler.


import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractException extends Exception {
   private static final long serialVersionUID = 1347818243665843226L;
   private static final Logger logger = LoggerFactory.getLogger(AbstractException.class);
   private static final String CRLF = System.getProperty("line.separator");
   private Exception exception;
   private String exceptionClass;
   private String[] messageArguments;
   private String vendorErrorCode = null;
   private String constraintName = null;

   protected AbstractException(Exception exp) {
      this.exception = exp;
      this.messageArguments = null;
      String s = this.getClass().getName();
      s = s.replace('.', '/');
      int i = s.lastIndexOf(36);
      if (i != -1) {
         this.exceptionClass = s.substring(i + 1);
         s.substring(0, i);
      } else {
         this.exceptionClass = "Unknown";
      }

   }

   protected AbstractException(String userDefinedErrorCode, String message) {
      this.exception = new Exception(message);
      this.messageArguments = null;
      this.exceptionClass = userDefinedErrorCode;
   }

   protected AbstractException(String cause, SQLException exp) {
      StringBuffer keyValue = new StringBuffer();
      this.exception = exp;
      this.messageArguments = null;
      String msg = exp.getMessage();
      this.vendorErrorCode = null;
      this.constraintName = null;
      int pos1 = 0;
      if (null != cause) {
         keyValue.append(cause);
      }

      int pos2 = msg.indexOf(":");
      if (pos1 > 0) {
         this.vendorErrorCode = msg.substring(0, pos1);
         if (null != cause) {
            keyValue.append(".");
         }

         keyValue.append(this.vendorErrorCode);
         msg = msg.substring(pos1);
      }

      if (logger.isDebugEnabled()) {
         logger.debug("Vendor Error Code=" + this.vendorErrorCode);
      }

      if (exp.getSQLState().startsWith("23")) {
         pos1 = msg.indexOf("(");
         if (pos1 > 0) {
            int pos3 = msg.indexOf(")");
            this.constraintName = msg.substring(pos1 + 1, pos2);
            pos2 = this.constraintName.indexOf(".");
            if (pos2 > 0) {
               this.constraintName = this.constraintName.substring(pos2 + 1);
            }

            if (null != this.vendorErrorCode) {
               keyValue.append(".");
            }

            keyValue.append(this.constraintName);
         }
      }

      this.exceptionClass = keyValue.toString();
      if (logger.isDebugEnabled()) {
         logger.debug("Key Code for Localize Message=" + this.exceptionClass);
      }

   }

  /* protected AbstractException(String cause, OracleXMLSQLException exp) {
      StringBuffer keyValue = new StringBuffer();
      this.exception = exp;
      this.messageArguments = null;
      String msg = exp.getMessage();
      if (logger.isDebugEnabled()) {
         logger.debug("Exception Message=" + msg);
      }

      this.vendorErrorCode = null;
      this.constraintName = null;
      int pos1 = true;
      if (null != cause) {
         keyValue.append(cause);
      }

      int pos1 = msg.indexOf(":");
      if (pos1 > 0) {
         msg = msg.substring(pos1 + 1);
         pos1 = msg.indexOf(":");
         if (pos1 > 0) {
            this.vendorErrorCode = msg.substring(0, pos1);
            this.vendorErrorCode = this.vendorErrorCode.trim();
            if (null != cause) {
               keyValue.append(".");
            }

            keyValue.append(this.vendorErrorCode);
            msg = msg.substring(pos1);
         }
      }

      if (logger.isDebugEnabled()) {
         logger.debug("Vendor Error Code=" + this.vendorErrorCode);
      }

      pos1 = msg.indexOf("(");
      if (pos1 > 0) {
         int pos2 = msg.indexOf(")");
         this.constraintName = msg.substring(pos1 + 1, pos2);
         pos2 = this.constraintName.indexOf(".");
         if (pos2 > 0) {
            this.constraintName = this.constraintName.substring(pos2 + 1);
         }

         if (null != this.vendorErrorCode) {
            keyValue.append(".");
         }

         keyValue.append(this.constraintName);
      }

      this.exceptionClass = keyValue.toString();
      if (logger.isDebugEnabled()) {
         logger.debug("Key Code for Localize Message=" + this.exceptionClass);
      }

   }*/

   protected AbstractException(String[] arguments, Exception exp) {
      this.exception = exp;
      this.messageArguments = arguments;
      String className = this.getClass().getName();
      className = className.replace('.', '/');
      int i = className.lastIndexOf(36);
      if (i != -1) {
         this.exceptionClass = className.substring(i + 1);
         className.substring(0, i);
      } else {
         this.exceptionClass = "Unknown";
      }

   }

   protected AbstractException(String expClass, String[] arguments, Exception exp) {
      this.messageArguments = arguments;
      this.exceptionClass = expClass;
      this.exception = exp;
   }

   public String toString() {
      String message = null;

      try {
         if (this.exception != null) {
            message = super.toString() + CRLF + "cause:" + this.exception.toString() + CRLF + "detail:" + this.getDetail();
         } else {
            message = super.toString();
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return message;
   }

   public String toXML() {
      String language = "en";
      return this.toXML(language);
   }

   public String toXML(String language) {
      StringBuffer xml = new StringBuffer();
      String detail = null;
      String errorcode = null;
      if (null != this.vendorErrorCode) {
         errorcode = "SQLERROR";
      } else {
         errorcode = this.exceptionClass;
      }

      detail = this.getDetail(language);
      if (null == detail) {
         detail = this.exceptionClass;
      }

      xml.append("<root><ERROR><ROW>");
      xml.append("<ERRORNO>");
      xml.append(errorcode);
      xml.append("</ERRORNO><TYPE>E</TYPE>");
      xml.append("<DESC><![CDATA[");
      xml.append(detail);
      xml.append("]]></DESC>");
      xml.append("</ROW></ERROR></root>");
      if (logger.isDebugEnabled()) {
         logger.debug("Return XML=" + xml.toString());
      }

      return xml.toString();
   }

   protected String[] getArguments() {
      return this.messageArguments;
   }

   public Throwable getCause() {
      return this.exception;
   }

   public String getDetail() {
      return this.getDetail("en");
   }

   public String getDetail(String locale) {
      String message = null;

      try {
 //        message = this.getMessage(locale);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return message;
   }

//   public String getMessage(Locale locale) {
//      ApplicationResources objResources = null;
//      String message = null;
//
//      try {
//         objResources = ApplicationResources.getInstance();
//         if (locale == null) {
//            locale = Locale.ENGLISH;
//         }
//
//         message = objResources.getResource(locale, this.exceptionClass);
//         if (null != message && this.messageArguments != null) {
//            message = MessageFormat.format(message, (Object[])this.messageArguments);
//         }
//      } catch (Exception var5) {
//         var5.printStackTrace();
//      }
//
//      return message;
//   }

//   public String getMessage(String locale) {
//      Locale loc = new Locale(locale, "");
//      return this.getMessage(loc);
//   }
}
