package de.foodshippers.foodship;

import org.parceler.apache.commons.collections.map.LinkedMap;

/**
 * Created by hannes on 07.12.16.
 * A class that represents a Cache. Internal it is a orderd map with a fixed size.
 */
public class CacheMap<K, V> {

    private final int maxsize;
    private LinkedMap map;

    public CacheMap(int Size) {
        if (Size <= 0) {
            throw new IllegalArgumentException("Cachegröße zu klein");
        }
        this.maxsize = Size;
        this.map = new LinkedMap();
    }


    /**
     * Gets the value refernced with the given key
     *
     * @param o key to the value
     * @return returns the value, returns null if there is no value refernced with the given key
     */
    public V get(K o) {
        return (V) map.get(o);
    }


    /**
     * Method to put an object in the Cache. if the Cache is full the oldest Objects is deleted.
     *
     * @param k Key to reference the Object
     * @param v Value to save
     * @return the given value
     */
    public V put(K k, V v) {
        if (map.containsKey(k)) {
            return null;
        } else if (map.size() >= maxsize) {
            map.remove(map.firstKey());
        }
        map.put(k, v);
        return v;
    }

    /**
     * Clears all objects from cache
     */
    public void clear() {
        map.clear();
    }
}
