package org.manifold.compiler;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;

public class Attributes {
  private final Map<String, Value> data;

  public Attributes(Map<String, TypeValue> types, Map<String, Value> data)
      throws UndeclaredAttributeException, InvalidAttributeException,
      TypeMismatchException {
    validateAttrNames(types.keySet(), data.keySet());
    validateAttrTypes(types, data);
    this.data = ImmutableMap.copyOf(data);
  }

  private static void validateAttrNames(Set<String> typeNames,
      Set<String> attrNames)
      throws UndeclaredAttributeException, InvalidAttributeException {
    for (String name : attrNames) {
      if (!typeNames.contains(name)) {
        throw new InvalidAttributeException(name);
      }
    }
    for (String name : typeNames) {
      if (!attrNames.contains(name)) {
        throw new UndeclaredAttributeException(name);
      }
    }
  }

  private static void validateAttrTypes(Map<String, TypeValue> types,
      Map<String, Value> data) throws TypeMismatchException {
    for (Map.Entry<String, TypeValue> entry : types.entrySet()) {
      String attrName = entry.getKey();
      TypeValue expectedType = data.get(attrName).getType();
      TypeValue actualType = entry.getValue();
      if (!expectedType.isSubtypeOf(actualType)) {
        throw new TypeMismatchException(expectedType, actualType);
      }
    }
  }

  public Value get(String attrName) throws UndeclaredAttributeException {
    if (data.containsKey(attrName)) {
      return data.get(attrName);
    } else {
      throw new UndeclaredAttributeException(attrName);
    }
  }
}
