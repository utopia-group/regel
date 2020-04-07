package regex;

import java.io.File;
import java.io.PrintWriter;

import edu.stanford.nlp.sempre.Builder;
import edu.stanford.nlp.sempre.Dataset;
import edu.stanford.nlp.sempre.Learner;
import edu.stanford.nlp.sempre.Master;
import fig.exec.Execution;

public class Train implements Runnable {
    
  @Override
  public void run() {

    Builder builder = new Builder();
    builder.build();
    
    System.out.println(builder.params);

    Dataset dataset = new Dataset();
    dataset.read();

    Learner learner = new Learner(builder.parser, builder.params, dataset);
    learner.learn();

  }

  public static void main(String[] args) {

    Train t = new Train();
    // parse our own arguments 
    {
      String execDir = args[args.length - 1];

      Execution.execDir = execDir;
    }

    // parse sempre arguments and run 
    {
      String[] args1 = new String[args.length - 1];
      System.arraycopy(args, 0, args1, 0, args.length - 1);
      Execution.run(args1, "Train", t, Master.getOptionsParser());
    }

  }

}
