package resnax.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetMultiMap<K, V> extends MultiMap<K, V> {

  public Map<K, Set<V>> map;

  public SetMultiMap() {
    this.map = new HashMap<>();
  }

  public SetMultiMap(SetMultiMap<K, V> other) {
    this.map = new HashMap<>();
    for (K key : other.keySet()) {
      Collection<V> set = other.get(key);
      Set<V> set1 = new HashSet<>(set);
      this.map.put(key, set1);
    }
  }

  @Override
  public boolean put(K key, V value) {
    Set<V> set = map.get(key);
    if (set == null) {
      set = new HashSet<>();
      map.put(key, set);
    }
    return set.add(value);
  }

  @Override
  public boolean putAll(K key, Collection<V> values) {
    Set<V> set = map.get(key);
    if (set == null) {
      set = new HashSet<>();
      map.put(key, set);
    }
    return set.addAll(values);
  }

  @Override
  public Collection<V> get(K key) {
    return map.get(key);
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public Set<K> keySet() {
    return map.keySet();
  }

  @Override
  public boolean containsKey(K key) {
    return map.containsKey(key);
  }

  @Override
  public boolean contains(K key, V value) {
    Set<V> set = map.get(key);
    return set != null && set.contains(value);
  }

  @Override
  public boolean equals(Object o) {
    assert false;
    return false;
  }

  @Override
  public int hashCode() {
    assert false;
    return 1;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ \n");
    for (K key : map.keySet()) {
      sb.append("  " + key + " -> ");
      Set<V> set = map.get(key);
      sb.append("[ ");
      for (V value : set) {
        sb.append(value + "; ");
      }
      sb.append("] \n");
    }
    sb.append("} \n");
    return sb.toString();
  }

  @Override
  public int size() {
    int ret = 0;
    for (K key : map.keySet()) {
      ret += map.get(key).size();
    }
    return ret;
  }

}
