package regex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.sempre.Builder;
import edu.stanford.nlp.sempre.Derivation;
import edu.stanford.nlp.sempre.Master;
import edu.stanford.nlp.sempre.Parser;
import edu.stanford.nlp.sempre.Session;
import fig.basic.LogInfo;
import fig.exec.Execution;

public class TestSimple implements Runnable {

  public String dataset = null;
  public int beam = -1;

  public void runPrediction(
      //
      Master master, Session session,
      // 
      String srcFilePath,
      //
      String specFilePath,
      // 
      String outputFilePath) {

    int currIndx = 1;


    try (PrintWriter outputFile = new PrintWriter(outputFilePath)) {
        try (BufferedReader srcFile = new BufferedReader(new FileReader(new File(srcFilePath)))) {
          
          // read # of lines for this utterance
          for (String lineCount = srcFile.readLine(); lineCount != null;) {
            
            outputFile.println("=======================================================");
            
            outputFile.println("Test on utterance " + currIndx + ":");
            
            int indent = LogInfo.getIndLevel();
            
            {
              try {
                
                int lineCount_int = Integer.parseInt(lineCount);
                
                if (lineCount_int == 0)
                  System.out.println("Error: lineCount is 0");
                
                for (int line_idx = 0; line_idx < lineCount_int; line_idx ++) {
                  
                  {
                    String line = srcFile.readLine();
                    
                    outputFile.println("  " + "Line " + line_idx + ": " + line);
                    
                    Map<String, Integer> derivToCount = new HashMap<>();
                    Map<String, Integer> derivToOrder = new HashMap<>();
                    String topPred = "";
                    
                    try{

                      Master.Response response = master.processQuery(session, line);

                      List<Derivation> derivs = response.ex.getPredDerivations();

                      for (int i = 0; i < derivs.size(); i ++) {

                        String derivString = ((derivs.get(i)).value).toString();
                        String subDeriv = "";
                        if (derivString.contains("\"")) {
                          subDeriv = derivString.split("\"")[1];
                        } else {
                          subDeriv = derivString.split(" ")[1];
                          subDeriv = subDeriv.substring(0, subDeriv.indexOf(")"));
                        }

                        // update derivation-to-count map 
                        if (derivToCount.containsKey(subDeriv)) {
                          derivToCount.put(subDeriv, derivToCount.get(subDeriv) + 1);
                        } else {
                          derivToCount.put(subDeriv, 1);
                        }
                        
                        if (!derivToOrder.containsKey(subDeriv)) {
                          derivToOrder.put(subDeriv, i);
                        }

                        // update top prediction 
                        if (i == 0) {
                          topPred = subDeriv;
                        }

                      }

                      // prints 
                      outputFile.println("    " +  "Top prediction: " + topPred);
                      
                      if (derivs.size() >= beam) {
                        outputFile.println("derivSize == beamSize");
                      }

                    }
			catch(Throwable t) {

                		System.out.println("Exception");

		              while (LogInfo.getIndLevel() > indent) LogInfo.end_track();
		                t.printStackTrace();

				continue;
				
			}
                    
                    if ((derivToCount.isEmpty()) || (topPred.equals(""))) continue;
                    
                    //
                    // more prints 
                    // 
                    {
                      outputFile.println("    " + "All " + derivToCount.size() + " derivations and their counts: ");
                      for (String k : derivToCount.keySet()) {
                        int v = derivToCount.get(k);
                        outputFile.println("      " + k + " : " + v);
                      }
                    }
                    
                  }
                  
                  
                }
                
              }
              catch(Throwable t) {
                System.out.println("Exception");

                while (LogInfo.getIndLevel() > indent)
                  LogInfo.end_track();
                t.printStackTrace();

		continue;
              }
              
              
            }
            
            lineCount = srcFile.readLine();
            
            if (lineCount == null || lineCount.isEmpty()) break;
            currIndx ++;
            
          }

          //
          {
            outputFile.println("=======================================================");
            outputFile.println("=======================================================");
            outputFile.println("=======================SUMMARY=========================");
            outputFile.println("=======================================================");
            outputFile.println("Total: " + currIndx);

          }

        }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public void run() {

    // parse our arguments 
    {
      this.beam = Parser.opts.beamSize;
    }

    // run prediction 
    {
      Builder builder = new Builder();
      builder.build();

      Master master = new Master(builder);

      Session session = master.getSession("stdin");
      
      {
        String srcFilePath = "regex/data/" + this.dataset + "/src-labeled-chunk.txt";
        String specFilePath = "";
        String outputFilePath = "regex/data/" + this.dataset + "/" + this.beam + "-pred.txt";
        runPrediction(master, session, srcFilePath, specFilePath, outputFilePath);
      }

//      // test on train set 
//      {
//        String srcFilePath = "regex/data/" + this.dataset + "/src-train.txt";
//        String specFilePath = "regex/data/" + this.dataset + "/spec-train.txt";
//        String outputFilePath = "regex/data/" + this.dataset + "/" + this.beam + "-pred-train.txt";
//        runPrediction(master, session, srcFilePath, specFilePath, outputFilePath);
//      }
//
//      //test on test set 
//      {
//        String srcFilePath = "regex/data/" + this.dataset + "/src-test.txt";
//        String specFilePath = "regex/data/" + this.dataset + "/spec-test.txt";
//        String outputFilePath = "regex/data/" + this.dataset + "/" + this.beam + "-pred-test.txt";
//        runPrediction(master, session, srcFilePath, specFilePath, outputFilePath);
//      }

    }

  }

  public static void main(String[] args) {

    TestSimple t = new TestSimple();

    // parse our own arguments 
    {
      String dataset = args[args.length - 1];
      t.dataset = dataset;
    }

    // parse sempre arguments and run 
    {
      String[] args1 = new String[args.length - 1];
      System.arraycopy(args, 0, args1, 0, args.length - 1);
      Execution.run(args1, "Test", t, Master.getOptionsParser());
    }

  }

}
