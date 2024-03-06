package com.ebixcash.aayu.util;

import com.ebixcash.aayu.util.Encoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashMapUtils {
  private static Logger logger = LoggerFactory.getLogger(HashMapUtils.class);
  
  public static HashMap addHashMap(HashMap mainHashMap, HashMap childHashMap) {
    HashMap returnHashMap = mainHashMap;
    try {
      if (mainHashMap != null && childHashMap != null)
        for (Iterator<Map.Entry> i = childHashMap.entrySet().iterator(); i.hasNext(); ) {
          Map.Entry e = i.next();
          returnHashMap.put(e.getKey(), e.getValue());
          if (logger.isInfoEnabled())
            logger.debug("Added to main hashmap. Key=" + e.getKey() + ", value=" + e.getValue()); 
        }  
      if (logger.isDebugEnabled())
        logger.debug("Added Hashmap values into the main Hashmap. Size=" + returnHashMap.size()); 
    } catch (Exception exception) {
      if (logger.isDebugEnabled())
        logger.debug("Exception occured while adding values into main Hashmap. Exception=" + exception.toString()); 
    } finally {
      mainHashMap = null;
      childHashMap = null;
    } 
    return returnHashMap;
  }

  public static HashMap getReturnHashMap(String successFlag, Object key, Object value) {
    HashMap<Object, Object> returnMap = new HashMap<>();
    returnMap.put("successFlag", successFlag);
    returnMap.put(key, value);
    return returnMap;
  }
  
  public static HashMap getReturnHashMap(String successFlag) {
    HashMap<Object, Object> returnMap = new HashMap<>();
    returnMap.put("successFlag", successFlag);
    return returnMap;
  }
  
  public static String HashMapToURL(HashMap valuesMap) {
    StringBuffer url = new StringBuffer();
    Object key = null;
    Object value = null;
    if (valuesMap != null) {
      Set keyset = valuesMap.keySet();
      Iterator iterator = keyset.iterator();
      while (iterator.hasNext()) {
        key = iterator.next();
        if (null != key) {
          value = valuesMap.get(key);
          if (value != null && value instanceof String) {
            if (url.length() != 0)
              url.append("&"); 
            url.append(key.toString());
            url.append("=");
            url.append(value.toString());
          } 
        } 
      } 
    } 
    return Encoder.encodeURL(url.toString());
  }
 
  public static final int calcCapacity(int size) {
    return (size * 4 + 3) / 3;
  }
  
  public static HashMap merge(Map<?, ?> map1, Map<?, ?> map2) {
    HashMap<Object, Object> retval = new HashMap<>(calcCapacity(map1.size() + map2.size()));
    retval.putAll(map1);
    retval.putAll(map2);
    return retval;
  }
  
  public static String getValue(String key, HashMap p_hMap) {
    String r_szFieldValue = null;
    Object object = p_hMap.get(key);
    String[] szArray = null;
    try {
      if (null == object)
        return null; 
      if (object instanceof String[]) {
        szArray = (String[])object;
        r_szFieldValue = szArray[0];
      } else {
        r_szFieldValue = (String)object;
      } 
    } finally {
      object = null;
      szArray = null;
    } 
    return r_szFieldValue;
  }
  
  public static boolean isNullOrEmpty(Map pMap) {
    if (null != pMap && 
      !pMap.isEmpty())
      return false; 
    return true;
  }
}