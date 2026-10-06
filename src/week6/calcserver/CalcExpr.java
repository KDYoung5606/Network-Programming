// 2271323 김동영

package week6.calcserver;

import java.io.Serializable;

public class CalcExpr implements Serializable {
  double operand1, operand2;
  char operator;

  public CalcExpr(double operand1, double operand2, char operator) {
    this.operand1 = operand1;
    this.operand2 = operand2;
    this.operator = operator;
  }
}
