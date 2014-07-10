package org.manifold.compiler;

public class BooleanValue extends Value {

  private static final BooleanValue highInstance = new BooleanValue(true);
  private static final BooleanValue lowInstance = new BooleanValue(false);

  public static BooleanValue getInstance(boolean value) {
    if (value) {
      return highInstance;
    } else {
      return lowInstance;
    }
  }

  private final boolean value;

  private BooleanValue(boolean value) {
    super(BooleanTypeValue.getInstance());
    this.value = value;
  }

  public boolean toBoolean() {
    return value;
  }

  @Override
  public void verify() { }

  @Override
  public boolean isCompiletimeEvaluable() {
    return true;
  }

  @Override
  public boolean isSynthesizable() {
    return true;
  }
}
