package resnax;

public class EvalResult {

  public boolean result;
  public int reason;      // 0 = match some positive, not all; 1 = don't match any positive; 2 = don't match some negative

  public EvalResult(boolean result, int reason) {
    this.result = result;
    this.reason = reason;
  }

  public EvalResult(boolean result) {
    this.result = result;
  }
}
