package starcat.variablestar;

/** Struct for data from the Hipparcos variable star annex. */
public final class VariableStar {

  /** The HIP id. */
  public String IDENTIFIER;
  
  /** Magnitude when dimmest. */
  public String MINIMUM;
  
  /** Magnitude when brightest. */
  public String MAXIMUM;
  
  /** The bandpass used by the max and min. Usually Johnson-V. */
  public String BANDPASS;
  
  /** Code describing the type of variable. This usually characterizes the light curve as well. */
  public String TYPE;
  
  /** Name of the variable star. Example: 'R_Crb' for <em>R Corona Borealis</em>. */
  public String NAME;
  
  /** Debugging only. */
  @Override public String toString() {
    return NAME + " " + IDENTIFIER + " [" +  TYPE + "] " + BANDPASS + " Vmin:" + MINIMUM + " Vmax:" + MAXIMUM;
  }
}