package starcat.precession;

import starcat.util.Maths;

/*** Struct for Right Ascension and Declination. */
public final class Position {
  
  /** Radians. */
  public double α;
  /** Radians. */
  public double δ;

  /** The given arguments are in radians. */
  public Position(Double α, Double δ) {
    this.α = α;
    this.δ = δ;
  }
  
  public Position() { }
  
  /** Debugging only. */
  @Override public String toString() {
    return "α :" + Maths.radsToDegs(α) + " δ:" + Maths.radsToDegs(δ);
  }
}