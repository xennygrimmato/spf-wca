package wcanalysis.heuristic;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Set;

import gov.nasa.jpf.vm.ChoiceGenerator;

/**
 * @author Kasper Luckow
 *
 */
public abstract class Policy implements Serializable {
  
  static enum ResolutionType implements Serializable {
    PERFECT,
    HISTORY, 
    INVARIANT,
    UNRESOLVED,
    NEW_CHOICE;
  }
  
  static class Resolution implements Serializable {
    private static final long serialVersionUID = 2247935610676857227L;
    public final ResolutionType type;
    public final int choice;
    public Resolution(int choice, ResolutionType type) {
      this.choice = choice;
      this.type = type;
    }
  }
  
  private static final long serialVersionUID = -2247935610676857237L;
  
  
  private final Set<String> measuredMethods;
    
  public Policy(WorstCasePath wcPath, Set<String> measuredMethods) {
    this.measuredMethods = measuredMethods;
    computePolicy(wcPath);
  }
  
  protected abstract void computePolicy(WorstCasePath wcPath);
  
  public Set<String> getMeasuredMethods() {
    return this.measuredMethods;
  }
  
  public abstract Resolution resolve(ChoiceGenerator<?> cg, ContextManager ctxManager);
  
  public void save(OutputStream out) {
    try {
      ObjectOutputStream o = new ObjectOutputStream(out);
      o.writeObject(this);
      o.close();
      out.close();
    } catch(IOException i) {
      throw new RuntimeException("Could not serialize policy", i);
    }
  }

  public static <T extends Policy> T load(InputStream in, Class<T> polCls) {
    T graph;
    try {
      ObjectInputStream i = new ObjectInputStream(in);
      graph = (T)i.readObject();
      i.close();
    } catch (ClassNotFoundException | IOException e1) {
      throw new RuntimeException("Could not deserialize policy", e1);
    }
    return graph;
  }
}