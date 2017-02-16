package org.manifold.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

public class Attributes {
  private final Map<String, Value> data;

  @Override
  public boolean equals(Object aThat) {
    if (this == aThat) {
      return true;
    }
    if (!(aThat instanceof Attributes)) {
      return false;
    }
    Attributes that = (Attributes) aThat;
    return (this.getAll().equals(that.getAll()));
  }

  public Attributes(Map<String, Value> data) {
    this.data = ImmutableMap.copyOf(data);
  }

  public Attributes(Map<String, TypeValue> types,
      Map<String, Value> data) throws UndeclaredAttributeException,
      InvalidAttributeException, TypeMismatchException {
    data = validateAttrsExistForTypesAndAddInferredValues(types, data);
    validateAttrNamesInTypes(types.keySet(), data.keySet());
    validateAttrTypes(types, data);
    this.data = ImmutableMap.copyOf(data);
  }

  private Map<String, Value> validateAttrsExistForTypesAndAddInferredValues(
      Map<String, TypeValue> types,
      Map<String, Value> originalData) throws UndeclaredAttributeException {
    Map<String, Value> data = originalData;
    for (Map.Entry<String, TypeValue> e : types.entrySet()) {
      String name = e.getKey();
      if (!data.containsKey(name)) {
        TypeValue type = UserDefinedTypeValue.getUnaliasedType(e.getValue());
        if (!(type instanceof InferredTypeValue)) {
          throw new UndeclaredAttributeException(name);
        } else {
          // Copy and edit the hash map in rare case inferred type is added
          if (data == originalData) {
            data = new HashMap<>(originalData);
          }
          // Add the inferred value with no value set.
          data.put(name, new InferredValue((InferredTypeValue) type));
        }
      }
    }
    return data;
  }

  private static void validateAttrNamesInTypes(Set<String> typeNames,
      Set<String> attrNames) throws UndeclaredAttributeException,
      InvalidAttributeException {
    for (String name : attrNames) {
      if (!typeNames.contains(name)) {
        throw new InvalidAttributeException(name);
      }
    }
  }

  private static void validateAttrTypes(Map<String, TypeValue> types,
      Map<String, Value> data) throws TypeMismatchException {
    for (Map.Entry<String, TypeValue> entry : types.entrySet()) {
      String attrName = entry.getKey();
      TypeValue dataType = data.get(attrName).getType();
      TypeValue expectedType = UserDefinedTypeValue.getUnaliasedType(entry.getValue());
      if (!dataType.isSubtypeOf(expectedType)) {
        throw new TypeMismatchException(expectedType, dataType);
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

  public Map<String, Value> getAll() {
    return ImmutableMap.copyOf(data);
  }
}
