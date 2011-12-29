/**
 * =================================================================================================================
 * File Name            : CacheManager.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.common;

import java.util.Hashtable;

public class CacheManager {

        /**
         * Single Instance Created.
         */
        private static final CacheManager cacheManager = new CacheManager();
        
        /**
         * Maintains the Cache of the Application.
         */
        private Hashtable<String,Object> cache = new Hashtable<String,Object>();
        
        public static boolean loggedIn = false;
        
        /**
         * Private Access constructor.
         */
        private CacheManager(){
                if(cache == null){
                        cache = new Hashtable<String,Object>();
                }
        }
        
        /**
         * Returns the singleton Instance of the Cache Manager
         * @return
         */
        public static CacheManager getInstance(){
                return cacheManager;
        }
        
        
        /**
         * Stores a Key/Value Pair in the Cache
         * @param key
         * @param value
         */
        public void store(String key, Object value){
                
                if(cache == null){
                        cache = new Hashtable<String,Object>();
                }
                cache.put(key, value);
        }
        
        
        /**
         * Gets a Object from the cache
         * @param key
         * @return
         */
        public Object get(Object key){
                return cache.get(key);
        }
        
        /**
         * Removes an element from the cache. 
         * @param key
         */
        public void removeFromCache(Object key){
                if(cache != null && key != null){
                        cache.remove(key);
                }
        }
        
        /**
         * Removes the Cache
         */
        public void clearCache(){
                if(cache != null){
                        cache.clear();
                }
        }
        
}


