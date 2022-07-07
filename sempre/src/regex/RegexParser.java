package regex;

import edu.stanford.nlp.sempre.Builder;
import edu.stanford.nlp.sempre.Example;
import edu.stanford.nlp.sempre.Master;
import edu.stanford.nlp.sempre.Session;

public class RegexParser extends Master {

  public RegexParser(Builder builder) {
    super(builder);
    this.session = new Session("");
  }

  public Session session;

  public Response parse(String utterance) {

    utterance = utterance.trim();

    Response ret = new Response();

    this.session.updateContext();

    // Create example
    Example.Builder b = new Example.Builder();
    b.setId("session:" + session.id);
    b.setUtterance(utterance);
    b.setContext(session.context);
    Example ex = b.createExample();

    ex.preprocess();

    // Parse!
    builder.parser.parse(builder.params, ex, false);

    ret.ex = ex;

    session.updateContext(ex, opts.contextMaxExchanges);

    return ret;

  }

}
