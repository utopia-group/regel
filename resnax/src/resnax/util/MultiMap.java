package resnax.util;

import java.util.Collection;
import java.util.Set;

public abstract class MultiMap<K, V> {

  public abstract boolean put(K key, V value);

  public abstract boolean putAll(K key, Collection<V> values);

  public abstract Collection<V> get(K key);

  public abstract boolean isEmpty();

  public abstract Set<K> keySet();

  public abstract boolean containsKey(K key);

  public abstract boolean contains(K key, V value);

  public abstract int size();

}
