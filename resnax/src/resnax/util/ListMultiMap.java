package resnax.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListMultiMap<K, V> extends MultiMap<K, V> {

  public Map<K, List<V>> map = new HashMap<>();

  @Override
  public boolean put(K key, V value) {
    List<V> list = map.get(key);
    if (list == null) {
      list = new ArrayList<>();
      map.put(key, list);
    }
    return list.add(value);
  }

  @Override
  public boolean putAll(K key, Collection<V> values) {
    List<V> list = map.get(key);
    if (list == null) {
      list = new ArrayList<>();
      map.put(key, list);
    }
    return list.addAll(values);
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
    List<V> list = map.get(key);
    return list != null && list.contains(value);
  }

  @Override
  public int size() {
    int ret = 0;
    for (K key : map.keySet()) {
      ret += map.get(key).size();
    }
    return ret;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ \n");
    for (K key : map.keySet()) {
      sb.append("  " + key + " -> ");
      List<V> set = map.get(key);
      sb.append("[ ");
      for (V value : set) {
        sb.append(value + "; ");
      }
      sb.append("] \n");
    }
    sb.append("} \n");
    return sb.toString();
  }

}
