package resnax.synthesizer.solver;

import com.microsoft.z3.Model;

public class SolveResult {

  public boolean satisfiable;
  public Model model;

  public SolveResult(boolean satisfiable, Model model) {
    this.satisfiable = satisfiable;
    this.model = model;
  }
}
