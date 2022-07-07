package resnax.util;

public class Utils {

  public static <K, V> SetMultiMap<K, V> unionSetMultiMaps(SetMultiMap<K, V> map1, SetMultiMap<K, V> map2) {
    SetMultiMap<K, V> ret = new SetMultiMap<K, V>(map1);
    for (K key : map2.keySet()) {
      ret.putAll(key, map2.get(key));
    }
    return ret;
  }

}
