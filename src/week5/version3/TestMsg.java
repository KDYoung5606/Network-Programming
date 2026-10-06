package week5.version3;

import java.io.Serializable;

public class TestMsg implements Serializable {
  String msg;

  public TestMsg(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "[ " + msg + " ]";
  }
}
