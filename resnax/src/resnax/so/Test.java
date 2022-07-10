package resnax.so;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import resnax.BenchmarkRes;
import resnax.Learner;
import resnax.Main;
import resnax.synthesizer.DSL.CFG;
import resnax.Example;
import resnax.synthesizer.RegexProgram;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates") public class Test {

  public String benchmarkPath = System.getProperty("user.dir") + "/exp/so/benchmark/";

  public Test() {
  }

  public Test(String benchmarkPath) {
    this.benchmarkPath = benchmarkPath;
  }

  public void benchmarkTest() {
    //    b1();
//    b2();
//    b3();
//    b5();
//    b6();
//    b7();
//    b8();
//    b9();
//    b11();
//    b14();
//    b15();
//    b18();
//    b19();
//    b20();
//    b21();
//    b22();
//    b24();
//    b25();
//    b26();
//    b31();
//    b32();
//    b35();
//    b36();
//    b37();
//    b38();
//    b39();
//    b40();
//    b41();
//    b44();
//    b45();
//    b46();
//    b47();
//    b48();
//    b52();
//    b55();
//    b56();
//    b57();
//    b58();
//    b60();
//    b61();
//    b62();
//    b64();
//    b67();
//    b69();
//    b72();
//    b73();
//    b74();
//    b75();
//    b76();
//    b77();
//    b78();
//    b81();
//    b83();
//    b88();
//    b91();
//    b93();
//    b96();
//    b98();
//    b104();
//    b105();
//    b106();
//    b107();
//    b110();
//    b111();
//    b112();
//    b114();
//    b115();
    b124();
  }

  public void benchmarkTest(String b) {

    String function = "b" + b;

    System.out.println("function:" + function);

    try {
      Method m = this.getClass().getMethod(function);
      m.invoke(this);
      return;
    } catch (Exception e) {
      System.out.println("No such method");
      System.exit(1);
    }
  }

  // connector to the sempre
  // also get rid of the boring test setup stuff
  // allow adding additionalExample
  public BenchmarkRes run_test(String sketch, String benchmark) {
    return run_test(sketch, benchmark, null, -1);
  }

  public BenchmarkRes run_test(String sketch, String benchmark, List<Example> additionalExample) {
    return run_test(sketch, benchmark, additionalExample, -1);
  }

  public BenchmarkRes run_test(String sketch, String benchmark, List<Example> additionalExample, int max) {

    System.out.println("======= Test b" + benchmark + " ======= ");

    Benchmark b = Benchmark.read(this.benchmarkPath + benchmark, sketch);
      if (max != -1) {
          Main.K_MAX = max;
      }
    Learner learner = new Learner(new CFG("", b.appliedTerminalsNoCost, b.appliedTerminalsCost));
    BenchmarkRes r;
    if (additionalExample != null) {
      additionalExample.addAll(b.examples);
      System.out.println(additionalExample);
      r = learner.learn(sketch, additionalExample, b.gt);
    } else {
      r = learner.learn(sketch, b.examples, b.gt);
    }

    if (r != null) System.out.println(r.program.pp.getRegex());

    return r;
  }

  private BenchmarkRes b(String sketch, List<Example> examples, List<String> terminalNoCost, List<String> terminalCost, int max, String gt) {

    System.out.println("====== Test customize ======");
    Learner learner = new Learner(new CFG("", terminalNoCost, terminalCost));
    BenchmarkRes r = learner.learn(sketch, examples, gt);

    return r;
  }

  public void b1() {
//    String sketch = "repeatatleast(?,1)"; // repeatatleast(id_low(<low>),1)
    String sketch = "?";
    run_test(sketch, "1");
  }

  public void b2() {

//    String sketch = "not(contain(?{<space>}))";
    String sketch = "?{contain(< >)}";
    run_test(sketch, "2");

  }

  public void b3() {

//    String sketch = "?{concat(?{concat(repeatrange(<num>,1,18),<.>)},?{<num>,<.>})}";
    String sketch = "?{<num>,or(<&>,or(<|>,or(<.>,or(<(>,or(<)>,< >)))))}";
//    String sketch = "?{or(<num>,or(<&>,or(<|>,or(<.>,or(<(>,or(<)>,< >))))))}";
//    String sketch = "repeatatleast(or(<num>,or(<&>,or(<|>,or(<.>,or(<(>,or(<)>,< >)))))),1)";

    run_test(sketch, "3");

  }

  public void b5() {

    String sketch = "?{concat(?{<0>,<num>},?{<0>,<num>})}";

    run_test(sketch, "5");

  }

  public void b6() {

//    String sketch = "concat(?{repeatatleast(<num>,1),<num>,<let>,<:>},?{repeatatleast(<num>,1),<num>,<let>,<:>})";
//    String sketch = "sep(?{<num>,repeatatleast(<num>,1),repeatatleast(<let>,1)},<:>)";
    String sketch = "sep(?{<num>,repeatatleast(<num>,1),<let>},<:>)";

    run_test(sketch, "6");

  }

  public void b7() {


    String sketch = "concat(concat(sketch(?{or(<=>,<;>)},1),concat(<=>,sketch(?{or(<=>,<;>)},1))),star(concat(<;>,concat(sketch(?{or(<=>,<;>)},1),concat(<=>,sketch(?{or(<=>,<;>)},1))))))";

    run_test(sketch, "7");

  }

  public void b8() {

    String sketch = "concat(?{<num>},?{or(<0>,<5>),<.>})";
//    String sketch = "?{repeat(<num>,1),repeat(or(<0>,<5>),1),<dot>,<num>,<0>,<5>}";
//    String sketch = "sep(?{or(<0>,<5>),<num>},<.>)";

//    String sketch = "?{concat(?{repeat(<num>,1),<.>,<0>,<5>,or(<0>,<5>)},?{repeat(<num>,1),<.>,<0>,<5>,or(<0>,<5>)})}";

    run_test(sketch, "8");
  }

  public void b9() {

//    String sketch = "?{concat(?{concat(repeatrange(<num>,1,18),<.>)},?{<num>,<.>})}";
//    String sketch = "concat(?{repeatrange(<num>,1,18),<.>,<num>},?{<.>,<num>})";
    String sketch = "?{repeatrange(<num>,1,18),repeat(<num>,1),concat(<.>,<num>),<num>,<.>}";

    run_test(sketch, "9");

  }

  public void b11() {

    String sketch = "?{or(<A>,or(<B>,<C>))}";

    run_test(sketch, "11");

  }

  public void b14() {
//    String sketch = "?{concat(?{concat(repeatrange(<num>,1,18),<.>)},?{<num>,<.>})}";
    String sketch = "concat(?{star(<num>),<X>},?{<^>,repeatatleast(<num>,1)})";

    run_test(sketch, "14");
  }

  public void b15() {

//    String sketch = "concat(?{<num>,repeatrange(<num>,1,15),<,>},?{concat(<.>,?{repeatrange(<num>,1,3),<num>,<,>})})";
//    String sketch = "concat(?{<num>,<,>},?{concat(<.>,?{repeatrange(<num>,1,3),<num>,<,>})})";
//    String sketch = "concat(?{repeatrange(<num>,1,15)},?{<,>,repeatrange(<num>,1,3)})";
//    String sketch = "concat(?{<num>,repeatrange(<num>,1,15),<,>},?{concat(<.>,?{repeatrange(<num>,1,3),<num>,<,>})})";
//    String sketch = "sep(?{<num>,repeat(<num>,1,3)},<.>)";
    String sketch = "concat(?{<num>},?{<,>,repeatrange(<num>,1,3)})";

    run_test(sketch, "15");

  }

  public void b18() {

//    String groundT = "or(repeat(<num>,7),repeat(<num>,10))";
//    String sketch = "v0";   // concat(id_num(<num>),concat(id_num(<num>),concat(id_num(<num>),concat(id_num(<num>),concat(id_num(<num>),concat(id_num(<num>),or(id_num(<num>),concat(id_num(<num>),repeatatleast(id_num(<num>),3)))))))))
//    String sketch = "or(v0,v1)";  // or(id_let(<let>),concat(id_any(<any>),concat(id_any(<any>),concat(id_any(<any>),concat(id_any(<any>),concat(id_any(<any>),concat(id_any(<any>),or(id_any(<any>),concat(id_any(<any>),repeatatleast(id_any(<any>),3))))))))))
//    String sketch = "or(?{<num>},?{<num>})";  // or(id_dummy(<num>),concat(id_dummy(<num>),concat(id_dummy(<num>),concat(id_dummy(<num>),concat(id_dummy(<num>),concat(id_dummy(<num>),concat(id_dummy(<num>),or(id_dummy(<num>),concat(id_dummy(<num>),concat(id_dummy(<num>),not(id_dummy(<num>))))))))))))
//    String sketch = "?{repeat(<num>,7),repeat(<num>,10)}"; // or(id_dummy(repeat(<num>,10)),id_dummy(repeat(<num>,7)))
    String sketch = "?{repeat(<num>,10),<num>}";
//    String sketch = "or(?{<num>},?{repeat(<num>,10),<num>})";

    run_test(sketch, "18");
  }

  public void b19() {

    String sketch = "?{or(?{repeat(<num>,5),<num>},?{repeat(<num>,5),<num>}),<num>,repeat(<num>,5)}";

    run_test(sketch, "19");
  }

  public void b20() {

//    String sketch = "concat(?{<num>,<.>},?{<num>,<.>})";
//    String sketch = "sep(?{<num>,<0>},?{<.>})";
    String sketch = "sep(?{<num>,<0>},<.>)";

    List<Example> additions = new ArrayList<>();
    additions.add(new Example("0.1", true));

    run_test(sketch, "20", additions);

  }

  public void b21() {

//    String sketch = "concat(?{<num>,<.>},?{<num>,<.>})";
//    String sketch = "sep(?{star(<num>),repeatatleast(<num>,1)},<.>)";
    String sketch = "sep(?{<num>},<.>)";
    run_test(sketch, "21");


  }

  public void b22() {

    String sketch = "?{repeatrange(<let>,3,20),not(contain(repeat(<cap>,2))),< >,<low>,<cap>,<->,<let>}";
    run_test(sketch, "22");

  }

  public void b24() {

    String sketch = "?{concat(?{repeat(<any>,3),<,>},?{repeat(<any>,3),<,>})}";
    run_test(sketch, "24");

  }

  public void b25() {

//    String sketch = "concat(?{<alphanum>,<,>},?{<alphanum>,<,>})";
//    String sketch = "sep(repeatatleast(or(<let>,<num>),1),<,>)";
    String sketch = "sep(?{<alphanum>},<,>)";
    run_test(sketch, "25");

  }

  public void b26() {

    String sketch = "concat(concat(concat(<0>,<4>),concat(?{or(<1>,<2>)},?{or(<2>,or(<4>,<6>))})),repeat(?,7))";
    run_test(sketch, "26");

  }

  public void b31() {
    String sketch = "concat(?{repeat(<hex>,8),<_>,star(<let>)},?{repeat(<hex>,8),<_>,star(<let>)})";
//    String sketch = "?{repeat(<hex>,8),<_>,star(<let>)}";
    run_test(sketch, "31");

  }

  public void b32() {

//    String sketch = "concat(?{<num>,<.>},?{<num>,<.>})";
    String sketch = "sep(?{repeatatleast(<num>,1),repeatrange(<num>,1,4),<num>},?{<.>})";

    List<Example> additions = new ArrayList<>();
    additions.add(new Example("12345.12345", false));

    run_test(sketch, "32", additions);

  }

  public void b35() {

//    String sketch = "concat(?{<num>,<.>},?{<num>,<.>})";
    String sketch = "sep(?{<*>,repeat(<let>,2)},?{<*>,repeat(<let>,2)})";
    run_test(sketch, "35");

  }

  public void b36() {

//    String sketch = "not(startwith(concat(<0>,<num>)))";
//    String sketch = "v0"; // timeout
//    String sketch = "not(?)";  //  not(id_num(<num>)), no matter how many examples, 15 secs, depth 4
//    String sketch = "not(startwith(?))"; // not(startwith(endwith(id_5(5)))),  0.4 secs, depth 3
//    String sketch = "not(startwith(concat(?,?)))"; //not(startwith(concat(id_0(0),id_num(<num>)))),0.2, depth 2
    String sketch = "?{startwith(concat(?,?))}";
    run_test(sketch, "36");

  }

  public void b37() {

//    String sketch = "?{repeatatleast(or(<let>,<num>),1),contain(optional(or(<@>,<#>))),repeatrange(<any>,0,8)}";
//    String sketch = "and(repeatatleast(or(<let>,<num>),1),and(contain(optional(or(<@>,<#>))),repeatrange(<any>,0,8)))";
//    String sketch = "and(contain(repeatatleast(or(<let>,<num>),1)),repeatrange(or(or(<let>,<num>),or(<@>,<#>)),1,7))";
    String sketch = "and(?{<alphanum>},and(?{or(<@>,<#>)},?{<any>}))";
//    String sketch = "?{contain(repeatatleast(or(<let>,<num>),1)),or(or(<@>,<#>),or(<let>,<num>))}";
//    String sketch = "and(contain(repeatatleast(or(<let>,<num>),1)),?{or(or(<@>,<#>),or(<let>,<num>))})";
    run_test(sketch, "37");


  }

  public void b38() {

//    String sketch = "?{<+>,optional(<+>),not(contain(<let>)),not(contain(?{<+>}))}";
    String sketch = "concat(?{<+>,optional(<+>),not(contain(<let>)),not(contain(?{<+>}))},?{<+>,optional(<+>),not(contain(<let>)),not(contain(?{<+>}))})";

    run_test(sketch, "38");

  }

  public void b39() {

    String sketch = "?{repeatrange(<num>,1,13),<.>,repeat(<num>,2),<num>,not(concat(<.>,<0>)),<0>}";

    run_test(sketch, "39");
  }

  public void b40() {

//    String sketch = "concat(repeatrange(<num>,1,2),?{repeatrange(<num>,1,2),<.>})";
    String sketch = "concat(or(repeat(<num>,1),repeat(<num>,2)),?{<.>,or(repeat(<num>,1),repeat(<num>,2))})";
//    String sketch = "?{repeatrange(<num>,1,2),<.>}";
//    String sketch = "sep(repeatrange(<num>,1,2),<.>)";
    run_test(sketch, "40");

  }

  public void b41() {

    String sketch = "?{repeatrange(<num>,11,16)}";
    run_test(sketch, "41");

  }

  public void b44() {


    String sketch = "concat(?{<num>},?{concat(<.>,?{<num>})})";
    run_test(sketch, "44");

  }

  public void b45() {

    String sketch = "?{repeat(<num>,10,14),concat(<0>,<7>),concat(<4>,concat(<4>,<7>)),concat(<+>,concat(<4>,<4>))}";
    run_test(sketch, "45");

  }

  public void b46() {

    String sketch = "sep(?{repeatrange(<num>,1,4),repeatrange(<num>,1,2),<num>},?{<,>,<.>})";
    List<Example> additions = new ArrayList<>();
    additions.add(new Example("12,12", true));
    additions.add(new Example("12,123", false));
    additions.add(new Example("12,12.12", false));
    additions.add(new Example("12.12,12", false));
    run_test(sketch, "46", additions);

  }

  public void b47() {

    String sketch = "concat(?{<num>,or(<+>,<->)},concat(<.>,?{<num>,or(<+>,<->)}))";
    run_test(sketch, "47");

  }

  public void b48() {

    String sketch = "sep(?{<num>},<_>)";
//    String sketch = "sep(repeatatleast(<num>,1),<_>)";
    run_test(sketch, "48");

  }

  public void b52() {

    String sketch = "?{or(<let>,or(<num>,< >)),or(<_>,or(<+>,or(<->,or(</>,or(<\\>,or(<^>,or(<(>,<)>)))))))}";
//    String sketch = "repeatatleast(or(<let>,or(<num>,or(<_>,or(< >,or(<->,or(<+>,or(<(>,or(<)>,or(</>,<\\>))))))))),1)";
    run_test(sketch, "52");

  }

  public void b55() {

//    String sketch = "?{concat(?{<cap>,<let>},?{<cap>,<let>})}";
//    String sketch = "sep(?{<cap>},?)";
    String sketch = "concat(and(?{repeatatleast(<num>,1)},not(?{<0>})),?{<num>,repeat(<num>,2)})";
//    String sketch = "sep(?{not(startwith(repeatatleast(<0>,2))),repeatatleast(<num>,1),repeat(<num>,2)},<.>)";
//    String sketch = "sep(and(not(startwith(repeatatleast(<0>,2))),repeatatleast(<num>,1)),repeat(<num>,2)},<.>)";
//    String sketch = "concat(concat(optional(<0>),repeatatleast(<num>,1)),concat(<.>,repeat(<num>,2)))";
//    String sketch = "concat(and(not(startwith(repeatatleast(<0>,2))),repeatatleast(<num>,1)),concat(<.>,repeat(<num>,2)))";
//    String sketch = "sep(and(not(startwith(repeatatleast(<0>,2))),repeatatleast(<num>,1)),repeat(<num>,2)},<.>)";
    run_test(sketch, "55");
  }

  public void b56() {

//    String sketch = "?{concat(?{<cap>,<let>},?{<cap>,<let>})}";
//    String sketch = "sep(?{<cap>},?)";
    String sketch = "sep(concat(?{<cap>},?),?)";
    run_test(sketch, "56");
  }

  public void b57() {

    String sketch = "?{concat(?{repeatatleast(<num>,1),repeatrange(<num>,1,2),<.>,optional(<,>),optional(<->)},?{repeatatleast(<num>,1),repeatrange(<num>,1,2),<.>,optional(<,>),optional(<->)})}";
    run_test(sketch, "57");
  }

  public void b58() {

//    String sketch = "?{not(startwith(< >)),not(contain(< >)),not(endwith(< >))}";
    String sketch = "and(?{not(startwith(< >)),not(contain(< >)),not(endwith(< >))},?{not(startwith(< >)),not(contain(< >)),not(endwith(< >))})";
    run_test(sketch, "58");

  }

  public void b60() {

//    String sketch = "?{not(startwith(< >)),not(contain(< >)),not(endwith(< >))}";
    String sketch = "?{not(?),repeatrange(<any>,3,25)}";
    run_test(sketch, "60");
  }

  public void b61() {


    String sketch = "?{startwith(<alphanum>),or(<.>,or(<->,or(<#>,<&>)))}";
    List<Example> newlist = new ArrayList<>();
    newlist.add(new Example("aaaaaaaa!", false));
    newlist.add(new Example("a@a", true));
    newlist.add(new Example("a@ABC", true));
    newlist.add(new Example("a@AB!C", false));
    run_test(sketch, "61", newlist);
  }

  public void b62() {


    String sketch = "concat(?{<num>,<;>,star(<num>)},?{concat(<;>,?{<num>,<;>,star(<num>)})})";
    run_test(sketch, "62");
  }

  public void b64() {
//    String sketch = "concat(<let>,repeat(<num>,5))"; // ground truth
//    String sketch = "and(v0,v1)";
//    String sketch = "v0{startwith(v1),repeat(v2,1,5)}";
//    String sketch = "v0{startwith(<let>),repeat(<num>,1,5)}";
    String sketch = "concat(?{<let>},?{<num>})";
//    String sketch = "?{startwith(<let>),<num>,<let>}";
    List<Example> additions = new ArrayList<>();
    additions.add(new Example("a123456", false));
//    additions.add(new Example("12,123", false));
//    additions.add(new Example("12,12.12", false));
//    additions.add(new Example("12.12,12", false));
    run_test(sketch, "64", additions);

  }

  public void b67() {

//    String sketch = "concat(?{<alphanum>,<num>,or(or(<d>,<D>),or(or(<w>,<W>),or(or(<m>,<M>),or(or(<q>,<Q>),or(<y>,<Y>))))),<->},concat(?{<alphanum>,<num>,or(or(<d>,<D>),or(or(<w>,<W>),or(or(<m>,<M>),or(or(<q>,<Q>),or(<y>,<Y>))))),<->},?{<alphanum>,<num>,or(or(<d>,<D>),or(or(<w>,<W>),or(or(<m>,<M>),or(or(<q>,<Q>),or(<y>,<Y>))))),<->}))";
    String sketch = "concat(?{<alphanum>,<num>,or(or(<d>,<D>),or(or(<w>,<W>),or(or(<m>,<M>),or(or(<q>,<Q>),or(<y>,<Y>))))),<->},?{<alphanum>,<num>,or(or(<d>,<D>),or(or(<w>,<W>),or(or(<m>,<M>),or(or(<q>,<Q>),or(<y>,<Y>))))),<->})";
//    String sketch = "concat(optional(<->),concat(repeatatleast(<num>,1),optional(or(or(<d>,<D>),or(or(<w>,<W>),or(or(<m>,<M>),or(or(<q>,<Q>),or(<y>,<Y>))))))))";
    List<Example> additions = new ArrayList<>();
    additions.add(new Example("1-d", false));
    additions.add(new Example("-1a", false));
    run_test(sketch, "67", additions);
  }

  public void b69() {


    String sketch = "concat(?{repeatrange(<num>,1,5)},?{<.>,repeatrange(<num>,1,2)})";
    List<Example> newlist = new ArrayList<>();
    newlist.add(new Example("1", true));
    newlist.add(new Example("1.2", true));
//    newlist.add(new Example("a@a",true));
//    newlist.add(new Example("a@ABC",true));
//    newlist.add(new Example("a@AB!C",false));
    run_test(sketch, "69", newlist);
  }

  public void b72() {

    String sketch = "and(?{repeatatleast(<any>,6)},and(?{< >},and(?{repeatatleast(<num>,1)},and(?{repeatatleast(<cap>,1)},and(?{repeatatleast(<low>,1)},?{repeatatleast(<spec>,1),or(<!>,or(<@>,or(<#>,or(<$>,or(<%>,or(<^>,or(<&>,or(<*>,or(<(>,or(<)>,<_>))))))))))})))))";
    run_test(sketch, "72");
  }

  public void b73() {

    String sketch = "concat(?{<num>,<,>,not(contain(<$>)),not(contain(<.>))},?{<num>,<,>,not(contain(<$>)),not(contain(<.>))})";
    run_test(sketch, "73");
  }

  public void b74() {

//    String sketch = "and(repeat(<num>,10),startwith(<9>))";
//    String sketch = "v0"; // using the first four with only useful production
//    String sketch = "v0"; // timeout
//    String sketch = "v0{<num>}";  // timeout
//    String sketch = "v0{repeat(<num>,10)}";
    String sketch = "and(?,?)"; // able to get x`gt in 3 seconds, depth set to 3
//    String sketch = "and(repeat(?,10),?)"; // able to get gt in 2.7 seconds, depth set to 3
    run_test(sketch, "74");
  }

  public void b75() {

    String sketch = "sep(?{<num>},<,>)";
    run_test(sketch, "75");

  }

  public void b76() {

//    String sketch = "?{or(or(<alphanum>,<.>),or(concat(<%>,concat(<t>,concat(<7>,<%>))),concat(<%>,concat(<d>,concat(<4>,<%>)))))}";
    String sketch = "?{or(or(<let>,<num>),<.>),or(concat(<%>,concat(<t>,concat(<7>,<%>))),concat(<%>,concat(<d>,concat(<4>,<%>))))}";
    run_test(sketch, "76");

  }

  public void b77() {

//    String sketch = "?{or(or(<alphanum>,<.>),or(concat(<%>,concat(<t>,concat(<7>,<%>))),concat(<%>,concat(<d>,concat(<4>,<%>)))))}";
    String sketch = "?{<alphanum>,or(<~>,or(<!>,or(<@>,or(<#>,or(<$>,or(<->,<_>))))))}";
    List<Example> additions = new ArrayList<>();
    additions.add(new Example("1", true));
//    Main.K_MAX = 10;
    run_test(sketch, "77", additions);

  }


  public void b78() {


    String sketch = "?{<alphanum>,< >,<_>,not(startwith(< >)),not(endwith(< >))}";
    run_test(sketch, "78");

  }

  public void b81() {

    String sketch = "sep(?{repeatatleast(<let>,1)},< >)";
    run_test(sketch, "81");
  }

  public void b83() {


    String sketch = "sep(?{repeatatleast(<num>,1),repeatrange(<num>,1,3)},<,>)";
//    String sketch = "concat(?{repeatatleast(<num>,1),<,>,repeatrange(<num>,1,3)},?{repeatatleast(<num>,1),<,>,repeatrange(<num>,1,3)})";
//    String sketch = "?{repeatatleast(<num>,1),<,>,repeatrange(<num>,1,3)}";
    run_test(sketch, "83");
  }

  public void b88() {
    String sketch = "sep(?{<num>},<,>)";
    List<Example> additions = new ArrayList<>();
    additions.add(new Example("123456,12", true));
    Main.K_MAX = 10;

    run_test(sketch, "88", additions, 10);
  }

  public void b91() {

    String sketch = "and(?{repeatrange(<any>,10,15)},concat(concat(sketch(?{or(<(>,or(<)>,or(< >,or(<.>,<->))))},2),repeat(<num>,3)),concat(concat(sketch(?{or(<(>,or(<)>,or(< >,or(<.>,<->))))},2),repeat(<num>,3)),concat(sketch(?{or(<(>,or(<)>,or(< >,or(<.>,<->))))},2),repeat(<num>,4)))))";
//    String sketch = "and(?{repeatrange(<any>,10,15)},concat(concat(sketch(?{star(or(<(>,or(<)>,or(< >,or(<.>,<->)))))},2),repeat(<num>,3)),concat(concat(sketch(?{star(or(<(>,or(<)>,or(< >,or(<.>,<->)))))},2),repeat(<num>,3)),concat(sketch(?{star(or(<(>,or(<)>,or(< >,or(<.>,<->)))))},2),repeat(<num>,4)))))";
    run_test(sketch, "91");
  }

  public void b93() {


//    String sketch = "sep(?{repeatatleast(<num>,1),repeatrange(<num>,1,3)},<,>)";
//    String sketch = "concat(?{repeatatleast(<num>,1),<,>,repeatrange(<num>,1,3)},?{repeatatleast(<num>,1),<,>,repeatrange(<num>,1,3)})";
    String sketch = "or(sketch(?{<num>,concat(<1>,<0>)},1),concat(sketch(?{<num>,concat(<1>,<0>)},1),concat(<+>,sketch(?{<num>,concat(<1>,<0>)},1))))";
//    String sketch = "sep(or(<num1-9>,concat(<1>,<0>)),<+>)";
//    String sketch = "concat(or(<num1-9>,concat(<1>,<0>)),optional(concat(<+>,or(<num1-9>,concat(<1>,<0>)))))";
    run_test(sketch, "93");
  }

  public void b96() {
    String sketch = "or(?{<A>,<B>},?{<A>,<B>})";
    List<Example> additions = new ArrayList<>();
    additions.add(new Example("c", false));
    additions.add(new Example("d", false));
    Main.K_MAX = 10;

    run_test(sketch, "96", additions);
  }

  public void b98() {

    String sketch = "concat(sketch(?{not(startwith(<0>)),<num>,repeatrange(<num>,1,6)},1),concat(<->,sketch(?{not(startwith(<0>)),<num>,repeatrange(<num>,1,6)},1)))";
    run_test(sketch, "98");

  }

  public void b104() {

    String sketch = "?{or(<let>,<num>),<let>}";
    run_test(sketch, "104");
  }

  public void b105() {

    String sketch = "and(concat(?{repeatrange(<let>,1,6),repeatatleast(<cap>,1)},and(?{repeat(<any>,2)},?{concat(<0>,<0>)})),?{repeatrange(<any>,3,8)})";

    run_test(sketch, "105");
  }


  public void b106() {

    String sketch = "concat(?{<let>,or(<A>,or(<T>,or(<C>,or(<G>,<N>))))},?{<num>})";
//    String sketch = "concat(repeatrange(or(<A>,or(<T>,or(<C>,or(<G>,<N>)))),1,64),<num>)";

    run_test(sketch, "106");
  }

  public void b107() {

    String sketch = "sep(?{<num>},<.>)";

    run_test(sketch, "107");
  }

  public void b110() {

    String sketch = "sep(?{<num>},<.>)";

    run_test(sketch, "107");
  }

  public void b111() {

    String sketch = "and(?{repeatatleast(<any>,2)},concat(?{repeatatleast(<let>,1),endwith(<.>),or(< >,<->)},?{<let>}))";

    run_test(sketch, "111");
  }


  public void b112() {

    String sketch = "concat(?{<let>},?{<let>,<num>})";

    run_test(sketch, "112");
  }

  public void b114() {

    String sketch = "concat(concat(<1>,concat(<5>,concat(<6>,<6>))),?{repeat(<num>,2),<num>,<->})";

    run_test(sketch, "114");
  }

  public void b115() {
    String sketch = "?{<.>,concat(<a>,concat(<b>,<c>))}";

    run_test(sketch, "115");
  }

  public void b124() {
    String sketch = "and(?{repeatatleast(<num>,1)},and(?{repeatatleast(<cap>,1),<low>},and(?{repeatatleast(<spec>,1)},?{repeatrange(<any>,6,20)})))";

    run_test(sketch, "124");
  }

  public void programTest() {

    sComposite2();
    sComposite1();
    sOr();
    sAnd();
    sConcat();
    sRepeatLessThan();
    sRepeatRange();
    sRepeat();
    sNot();
    sNotcc();
    sStar();
    sOptional();
    sContain();
    sStartwith();
    sEndwith();
    sFreeVar();
    sGuardedVar();

    sCustom1();

//    sRepeatSketch();
  }

  public void evaluateTest() {

    pLet();
    pNum();
    pCap();
    pLow();
    pAny();
    pStartwith();
    pEndwith();
    pContain();
    pNot();
    pRepeat();
    pRepeatAtLeast();
    pRepeatRange();
    pStar();
    pOptional();
    pConcat();
    pOr();
    pAnd();
    pComposite1();
    pComposite2();
    pComposite3();
    pComposite4();
    pNotcc();
  }

  // test concrete

  private void pNotcc() {

    System.out.println("======= Test pNotcc ========");

    String sketch = "notcc(<.>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1", true));
    examples.add(new Example("a", true));
    examples.add(new Example("aa", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();

    assert (res.equals(sketch));

  }

  private void pComposite4() {
    System.out.println("======= Test pComposite4 ======= ");

    String sketch = "concat(<let>,repeatrange(<num>,0,4))";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a1", true));
    examples.add(new Example("a", true));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();

    assert (res.equals(sketch));

  }

  // composite concrete3
  private void pComposite3() {
    System.out.println("======= Test pComposite3 ======= ");

    String sketch = "startwith(repeatatleast(<num>,0))";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1aa", true));
    examples.add(new Example("22222", true));
    examples.add(new Example("bbb3", true));
//    examples.add(new Example("a222a", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // composite concrete 2
  private void pComposite2() {

    System.out.println("======= Test pComposite2 ======= ");

    String sketch = "or(startwith(<num>),endwith(<num>))";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1aa", true));
    examples.add(new Example("22222", true));
    examples.add(new Example("bbb3", true));
    examples.add(new Example("a222a", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // composite concrete 1
  private void pComposite1() {

    System.out.println("======= Test pComposite1 ======= ");

    String sketch = "or(repeat(<num>,7),repeat(<num>,10))";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("0123456", true));
    examples.add(new Example("0123456789", true));
    examples.add(new Example("123456", false));
    examples.add(new Example("123", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));
  }

  // repeat range
  private void pRepeatRange() {
    System.out.println("======= Test pRepeatRange ======= ");

//    String sketch = "repeat(<num>,2,4)";
    String sketch = "repeatrange(<num>,2,4)";

    List<Example> examples = new ArrayList<>();
//    examples.add(new Example("11111", true));
    examples.add(new Example("1111", true));
    examples.add(new Example("111", true));
    examples.add(new Example("11", true));
    examples.add(new Example("1", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // repeat at least concrete
  private void pRepeatAtLeast() {
    System.out.println("======= Test pRepeatAtLeast ======= ");

//    String sketch = "repeat(<num>,2,)";
    String sketch = "repeatatleast(concat(<let>,<num>),0)";

    List<Example> examples = new ArrayList<>();
//    examples.add(new Example("11", true));
//    examples.add(new Example("111", true));
//    examples.add(new Example("1", false));
    examples.add(new Example("a1a1a1a1", true));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // repeat exact concrete
  private void pRepeat() {

    System.out.println("======= Test pRepeat ======= ");

    String sketch = "repeat(<num>,2)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("11", true));
    examples.add(new Example("1", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // or concrete
  private void pOr() {

    System.out.println("======= Test pOr ======= ");

    String sketch = "or(<let>,<num>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a", true));
    examples.add(new Example("1", true));
    examples.add(new Example("%", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // and concrete
  private void pAnd() {

    System.out.println("======= Test pAnd ======= ");

    String sketch = "and(<let>,<any>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a", true));
    examples.add(new Example("1", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // not concrete
  private void pNot() {

    System.out.println("======= Test pNot ======= ");

    String sketch = "not(<let>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1", true));
    examples.add(new Example("a", false));
    examples.add(new Example("aa", true));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  private void pStar() {

    System.out.println("===== Test pStar ======");

    String sketch = "star(<a>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("", true));
    examples.add(new Example("aaaa", true));
    examples.add(new Example("aaaaaaaaaaa", true));
    examples.add(new Example("bbb", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));
  }

  private void pOptional() {

    System.out.println("===== Test pOptional ======");

    String sketch = "concat(<a>,optional(repeat(<5>,2)))";
//    String sketch = "optional(<a>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a", true));
//    examples.add(new Example("", true));
    examples.add(new Example("a55", true));
    examples.add(new Example("a5", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // concat concrete
  private void pConcat() {

    System.out.println("======= Test pConcat ======= ");

    String sketch = "concat(<num>,<let>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1a", true));
    examples.add(new Example("a1", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // contain concrete
  private void pContain() {

    System.out.println("======= Test pContain ======= ");

    String sketch = "contain(<num>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("11a", true));
    examples.add(new Example("aa", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  // endwith concrete
  private void pEndwith() {

    System.out.println("======= Test pEndwith ======= ");

    String sketch = "endwith(<num>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("11", true));
    examples.add(new Example("aa", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));
  }

  // startwith concrete
  private void pStartwith() {

    System.out.println("======= Test pStartWith ======= ");

    String sketch = "startwith(<num>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("11", true));
    examples.add(new Example("aa", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  private void pAny() {
    System.out.println("======= Test pAny ======= ");

    String sketch = "<any>";
//    String sketch = "repeat(<any>,0,)";
//    String sketch = "not(<any>)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a", true));
    examples.add(new Example(".", true));
    examples.add(new Example(".", true));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  private void pLow() {
    System.out.println("======= Test pLow ======= ");

    String sketch = "<low>";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a", true));
    examples.add(new Example("A", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  private void pCap() {
    System.out.println("======= Test pCap ======= ");

    String sketch = "<cap>";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("A", true));
    examples.add(new Example("a", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  private void pLet() {
    System.out.println("======= Test pLet ======= ");

    String sketch = "<let>";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a", true));
    examples.add(new Example("1", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  private void pNum() {

    System.out.println("======= Test pNum ======= ");

    String sketch = "<num>";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1", true));
    examples.add(new Example("a", false));

    Learner l = new Learner();
    String res = l.learn(sketch, examples, null).toString();
    assert (res.equals(sketch));

  }

  private void sRepeatSketch() {
    String sketch = "concat(sketch(concat(?{<num>},?{<let>}),1),or(?{<let>},sketch(concat(?{<num>},?{<let>}),1)))";
//    String sketch = "concat(sketch(?{<num>},1),concat(<->,concat(sketch(?{<num>},1),concat(<->,sketch(?{<num>},1)))))";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("11aa2a", true));
    examples.add(new Example("11aaa", true));
    examples.add(new Example("2aaa", true));
    examples.add(new Example("1", false));
    examples.add(new Example("a", false));


//    examples.add(new Example("1-1-1", true));
//    examples.add(new Example("1-2-33", true));
//    examples.add(new Example("1-a-1", false));
//    examples.add(new Example("a", false));
//    examples.add(new Example("a", false));
//    examples.add(new Example("a", false));


    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
  }


  private void sCustom1() {

    System.out.println("======= Test sCustom1 ======= ");
//    String groundT = "repeat(or(<num>,<let>),1,)";
//    String sketch = "repeat(v0,1,)";  // maxDepth = 1
//    String sketch = "v0";      // maxDepth = 2
    String sketch = "?{repeatrange(<any>,4,8),not(startwith(<let>)),not(endwith(repeat(<low>,2)))}";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("_1cnmlK", true));
    examples.add(new Example("_1cn+57", true));
    examples.add(new Example("2Q,l", true));
    examples.add(new Example("_4Jw42Cq", true));
    examples.add(new Example(";lmm1i", true));
    examples.add(new Example("_2fT6", true));
    examples.add(new Example(";lmE0Z", true));
    examples.add(new Example("_2Q2g", true));
    examples.add(new Example("_pj8", true));
    examples.add(new Example(";lmEg,", true));
    examples.add(new Example("ZYyhA", false));
    examples.add(new Example("9sedsw", false));
    examples.add(new Example("MvFxjSb", false));
    examples.add(new Example("0JL", false));
    examples.add(new Example("_iE", false));
    examples.add(new Example("9seGav", false));
    examples.add(new Example("ZYBf,", false));
    examples.add(new Example("_jAupnuh", false));
    examples.add(new Example("IUSa", false));
    examples.add(new Example("-qwif", false));


    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();

    assert (evaluate(res, examples));
  }

  // composite sketch
  private void sComposite2() {

    System.out.println("======= Test sComposite2 ======= ");
//    String groundT = "repeat(or(<num>,<let>),1,)";
//    String sketch = "repeat(v0,1,)";  // maxDepth = 1
//    String sketch = "v0";      // maxDepth = 2
    String sketch = "repeatatleast(or(?,?),1)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("3a", true));
    examples.add(new Example("2s4f", true));
    examples.add(new Example("...", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();

    assert (evaluate(res, examples));
  }

  private void sComposite1() {
    System.out.println("======= Test sComposite1 ======= ");

    String sketch = "?{startwith(?),endwith(?)}";
//    String sketch = "or(?,?)";

    List<Example> examples = new ArrayList<>();

    examples.add(new Example("a2a", true));
    examples.add(new Example("22f", true));
    examples.add(new Example("a22", true));
    examples.add(new Example("&", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  // or sketch
  private void sOr() {

    System.out.println("======= Test sOr ======= ");

    String sketch = "or(?,?)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("2", true));
    examples.add(new Example("a", true));
    examples.add(new Example("@", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  // and sketch
  private void sAnd() {
    System.out.println("======= Test sAnd ======= ");

    String sketch = "and(?,?)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("%", false));
    examples.add(new Example("a", true));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  // concat sketch
  private void sConcat() {

    System.out.println("======= Test sConcat ======= ");

//    String sketch = "concat(v0,v1)";
    String sketch = "concat(?,?{<let>})";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("2a", true));
    examples.add(new Example("1A", true));
    examples.add(new Example("%%", false));
    examples.add(new Example("1%", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  // repeat less than sketch
  private void sRepeatLessThan() {

    System.out.println("======= Test sRepeatLessThan ======= ");
    String sketch = "repeatatleast(?,1)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("22", true));
    examples.add(new Example("AAA", true));
    examples.add(new Example("aa", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  // repeat range sketch
  private void sRepeatRange() {

    System.out.println("======= Test sRepeatRange ======= ");

    String sketch = "repeatrange(?,0,3)";
//    String sketch = "repeat(v0{<num>},1,2)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("22", true));
    examples.add(new Example("1", true));
    examples.add(new Example("a", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  private void sNotcc() {

    System.out.println("======= Test sNotcc ======= ");
    String sketch = "repeat(notcc(?),2)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("11", false));
    examples.add(new Example("22", false));
    examples.add(new Example("00", false));
    examples.add(new Example("aa", true));
    examples.add(new Example("AA", true));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));
  }

  // not sketch
  private void sNot() {

    System.out.println("======= Test sNot ======= ");
    String sketch = "not(?)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1", true));
    examples.add(new Example("a", true));
    examples.add(new Example("A", true));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));
  }

  // repeat sketch
  private void sRepeat() {

    System.out.println("======= Test sRepeat ======= ");

//    String sketch = "repeat(v0,6)";
    String sketch = "repeat(?{<num>,<let>},6)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("111aaa", true));
//    examples.add(new Example("11", true));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  // contain sketch
  private void sContain() {

    System.out.println("======= Test sContain ======= ");

    String sketch = "contain(?)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a1", false));
    examples.add(new Example("11", true));
    examples.add(new Example("%%", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  private void sOptional() {

    System.out.println("======= Test sOptional ======");

//    String sketch = "optional(?{<a>,<5>})";
    String sketch = "concat(?{<a>},?{<5>})";
//    String sketch = "optional(concat(?{<a>},?{<5>}))";
//    String sketch = "optional(concat(<a>,not(?{<5>},2)))";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a", true));
    examples.add(new Example("a55", true));
    examples.add(new Example("a555555", false));
    examples.add(new Example("a5", false));

    Main.K_MAX = 7;

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));
  }

  private void sStar() {

    System.out.println("======= Test sStar ========");

    String sketch = "star(?)";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("a", true));
    examples.add(new Example("abbb", true));
    examples.add(new Example("abab", true));
    examples.add(new Example("ccc", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  // endwith sketch
  private void sEndwith() {
    System.out.println("======= Test sEndwith ======= ");

    String sketch = "endwith(?{<let>,<num>})";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("aa1", true));
    examples.add(new Example("22", true));
    examples.add(new Example("3bbb", true));
    examples.add(new Example("a222.", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  // startwith sketch
  private void sStartwith() {
    System.out.println("======= Test sStartwith ======= ");

    String sketch = "startwith(?{<let>,<num>})";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1aa", true));
    examples.add(new Example("22", true));
    examples.add(new Example("bbb3", true));
    examples.add(new Example(".222a", false));

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  //free var
  private void sFreeVar() {

    System.out.println("======= Test sFreeVar ======= ");

    String sketch = "?";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("2", true));
    examples.add(new Example("111", true));
    examples.add(new Example("1111", false));

    List<String> terminals = new ArrayList<>();
    terminals.add("<num>");
    terminals.add("<1>");
    terminals.add("<2>");

    CFG g = new CFG("", terminals);

    Learner l = new Learner(g);
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  public void sGuardedVar() {

    System.out.println("======= Test sGuardedVar ======= ");

//  String sketch = "startwith(?0{concat(<num>,<let>)})";
    String sketch = "?{<num>,<let>}";

    List<Example> examples = new ArrayList<>();
    examples.add(new Example("1a1", true));
    examples.add(new Example("a1", true));
    examples.add(new Example("1a", true));
    examples.add(new Example("^1", false));
    examples.add(new Example("1^", false));
    examples.add(new Example("a", false));

    Main.K_MAX = 10;

    Learner l = new Learner();
    RegexProgram res = l.learn(sketch, examples, null).program.pp.getRegex();
    assert (evaluate(res, examples));

  }

  private boolean evaluate(RegexProgram regex, List<Example> exs) {
    Automaton a = new RegExp(regex.programs[0].toString()).toAutomaton();

    for (Example e : exs) {
      if (a.run(e.input)) {
        if (e.output == false) return false;
      } else {
        if (e.output == true) return false;
      }
    }
    return true;
  }

}
