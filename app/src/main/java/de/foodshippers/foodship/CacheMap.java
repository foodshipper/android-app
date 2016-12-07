package de.foodshippers.foodship;

import org.parceler.apache.commons.collections.map.LinkedMap;

/**
 * Created by hannes on 07.12.16.
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


    public V get(K o) {
        return (V) map.get(o);
    }


    public V put(K k, V v) {
        if (map.containsKey(k)) {
            return null;
        } else if (map.size() >= maxsize) {
            map.remove(map.firstKey());
        }
        map.put(k, v);
        return v;
    }
}
