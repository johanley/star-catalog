package starcat.precession;

import starcat.util.Maths;

/** 
 Apply precession to a J2000 position using an algorithm valid only for a few centuries.
 
 Uses <a href='https://www.aanda.org/articles/aa/pdf/2003/48/aa4068.pdf'>Capitaine 2003</a> (40).  
 <a href='https://ascl.net/1202.003'>NOVAS</a> uses (37) and (39) from Capitaine 2003, which use a different set of variables.
 The <a href='https://archive.org/details/astronomicalalma0000unse_s2r1/page/n163/mode/2up?q=capitaine&view=theater'>Astronmical Almanac</a>
 also uses Capitaine 2003.
*/
public final class Precession {
  
  public Precession(Double jd) {
    this.jd = jd;
    this.angles = capitaine2003();
  }
  
  public Double jd() {
    return jd;
  }
  
  public Angles angles() {
    return angles;
  }
  
  public Position apply(Position pos) {
    Position result = new Position();
    double A = Math.cos(pos.δ) * Math.sin(pos.α + angles.zeta);
    double B = Math.cos(angles.theta) * Math.cos(pos.δ) * Math.cos(pos.α + angles.zeta) - Math.sin(angles.theta) * Math.sin(pos.δ);
    double C = Math.sin(angles.theta) * Math.cos(pos.δ) * Math.cos(pos.α + angles.zeta) + Math.cos(angles.theta) * Math.sin(pos.δ);

    result.α = Maths.in2pi(Math.atan2(A, B) + angles.z); // 0..2pi
    
    if (Math.abs(pos.δ) < Maths.degToRads(85)){
      result.δ = Math.asin(C); //-pi/2..+pi/2
    }
    else {
      result.δ = Math.acos(Math.sqrt(A*A + B*B)); //0..pi, but always near +90 deg
      if (pos.δ < 0) {
        result.δ = -result.δ;
      }
    }
    return result;
  }
  
  // PRIVATE
  
  private static class Angles {
    double zeta;
    double z;
    double theta;
  }
  
  /** The Julian Date of the target date. */
  private double jd;
  private Angles angles;
  
  /** Julian centuries. */
  private double t() {
    return Maths.julianCenturiesSinceJ2000(jd);
  }
  
  /** 
   Capitaine 2003. 
   Note the presence of the two constant terms, with the same value but opposite sign.
   It's a bit strange that they are present, but the result seems fine.  
  */
  private Angles capitaine2003() {
    Angles result = new Angles();
    double t = t();
    double t2 = t*t;
    double t3 = t*t*t;
    double t4 = t*t*t*t;
    double t5 = t*t*t*t*t;
    result.zeta =  secondsToRads(2.650545 + 2306.083227*t + 0.2988499*t2 + 0.01801828*t3 - 0.000005971*t4 - 0.0000003173*t5);
    result.z =    secondsToRads(-2.650545 + 2306.077181*t + 1.0927348*t2 + 0.01826837*t3 - 0.000028596*t4 - 0.0000002904*t5);
    result.theta =  secondsToRads(          2004.191903*t - 0.4294934*t2 - 0.04182264*t3 - 0.000007089*t4 - 0.0000001274*t5);
    return result;
  }
 
  private double secondsToRads(Double arcsec) {
    return Maths.degToRads(arcsec / 3600.0);
  }
}