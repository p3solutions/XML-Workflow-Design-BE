package com.p3.archon.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Map Builder class is a fluent utility for building hashmaps with dynamic contentd
 *
 * @param <X> is the Map key type
 * @param <Y> is the Map value type
 * @author omjigupta
 */
public final class MapBuilder<X, Y> {
  private MapBuilder() {}

  private Map<X, Y> dataMap;

  /**
   * Initiates map builder instance and returns it
   *
   * @param <X> Map key type
   * @param <Y> Map value type
   * @return new MapBuilder<X,Y> isntance
   */
  public static <X, Y> MapBuilder<X, Y> builder() {
    Map<X, Y> map = new HashMap<>();
    final MapBuilder<X, Y> builder = new MapBuilder<>();
    builder.dataMap = map;
    return builder;
  }

  /**
   * Builds a hashmap from given key and value
   *
   * @param key is the map-key value
   * @param value is the value against map-key
   * @param <X> is the map key type
   * @param <Y> is the map value type
   * @return Map<X,Y> with given key-value added
   */
  public static <X, Y> Map<X, Y> of(X key, Y value) {
    Map<X, Y> map = new HashMap<>(1);
    map.put(key, value);
    return map;
  }

  /**
   * Builds a hashmap from given key and value
   *
   * @param key1 is the first map-key value
   * @param value1 is the value against first map-key
   * @param key2 is the second map-key value
   * @param value2 is the value against second map-key
   * @param <X> is the map key type
   * @param <Y> is the map value type
   * @return Map<X,Y> with given key-value added
   */
  public static <X, Y> Map<X, Y> of(X key1, Y value1, X key2, Y value2) {
    Map<X, Y> map = new HashMap<>(2);
    map.put(key1, value1);
    map.put(key2, value2);
    return map;
  }

  /**
   * Adds given key value pair to the map. Once the addition operation is complete, invoke {@link
   * #build()} to get Map.
   *
   * @param key is the key for the pair
   * @param value is the value for the pair
   * @return MapBuilder instance
   */
  public MapBuilder<X, Y> add(X key, Y value) {
    dataMap.put(key, value);
    return this;
  }

  /**
   * Builds the map from the added key value pairs and return built map.
   *
   * @return Built map
   */
  public Map<X, Y> build() {
    return dataMap;
  }
}

