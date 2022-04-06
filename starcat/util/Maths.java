package starcat.util;

/** Various utility methods of general use. */
public class Maths {
  
  public static double degToRads(double deg) {
    return deg * DEG_TO_RADS;   
  }
  
  public static double radsToDegs(double rad) {
    return rad * RADS_TO_DEG;   
  }
  
  public static double hoursToRads(double hours) {
    return degToRads(hours * HOURS_TO_DEGS);
  }
  
  public static double radsToHours(double rads) {
    return radsToDegs(rads) / HOURS_TO_DEGS;
  }
  
  public static double sqr(double val) {
    return val * val;
  }
  
  public static final double TWO_PI = 2 * Math.PI;
  
  //PRIVATE 
  
  private static final Double DEG_TO_RADS = TWO_PI/360.0;
  private static final Double RADS_TO_DEG = 360.0/(2*Math.PI);
  private static final Double HOURS_TO_DEGS = 15.0;
}