package starcat.make.bsc.hipparcos;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import starcat.precession.Position;
import starcat.star.Bandpass;
import starcat.star.Identifier;
import starcat.star.Star;
import starcat.util.Maths;
import starcat.util.Util;

import static starcat.util.LogUtil.*;

/**
 Scan the output catalog for close pairs of stars (as of J1991.25, the proper motion epoch of the catalog).
 
 <P>When creating a star chart, one might treat close pairs of stars as being a single entity.
 This is especially true when the charts cover large areas of the sky, and are intended to 
 approximate the view seen by the human eye.
 
 <P>An improved implementation of this class would take the epoch as a parameter, so that proper 
 motion could be calculated before calculating separations, but that isn't implemented here.
 Hence, this class can only be used for years that are not too distant from J1991.25.
*/
final class ClosePairs {

  /** Struct for data on a close pair of records. */
  static class CloseDouble {
    String hipA;
    String hipB;
    String nameA;
    String nameB;
    String VmagA;
    String VmagB;
    Double VmagCombined;
    Double separation;
    /** Format the final result. */
    @Override public String toString() {
      return "[" + hipA + name(nameA) + ", " + hipB + name(nameB) + "]: sep=" + arcseconds() + " arcseconds" + " Vmag=" + VmagCombined + " [" + VmagA + " + " + VmagB +"]";
    }
    private String name(String name) {
      return Util.isPresent(name) ? "("+name+")" : ""; 
    }
    /** Arcseconds, to two decimal places. */
    Double arcseconds(){
      Double sep = Maths.radsToArcseconds(separation);
      sep = roundTwoDecimals(sep);
      return sep;
    }
  }
  
  /** 
   Find close pairs of stars in the output catalog.  
   @param limitingArcseconds distance between the pair of stars (usually in the range 5..60 or so)
   @param records in the output catalog
   @return result ordered by separation, with smallest first
  */
  List<CloseDouble> findClosePairs(Double limitArcseconds, List<GeneratedRecord> records) {
    Double limit = Maths.degToRads(limitArcseconds/3600.0); //avoid int division
    List<CloseDouble> result = new ArrayList<>();
    for(GeneratedRecord aRec : records) {
      for(GeneratedRecord bRec : records) {
        if (aRec == bRec) continue;
        Star a = aRec.STAR;
        Star b = bRec.STAR;
        //rough but fast calc; it's at least this far away, and very likely more:
        Double decDist = Math.abs(Double.valueOf(a.DEC) - Double.valueOf(b.DEC)); //rads
        if (decDist <= limit) { 
          Double sep = separationBetween(a, b); // precise calc, rads
          if (sep <= limit) {
            CloseDouble closePair = new CloseDouble();
            closePair.hipA = a.IDENTIFIERS.get(Identifier.HIP);
            closePair.hipB = b.IDENTIFIERS.get(Identifier.HIP);
            closePair.nameA = nameFor(a);
            closePair.nameB = nameFor(b);
            closePair.VmagA = a.MAGNITUDES.get(Bandpass.V);
            closePair.VmagB = b.MAGNITUDES.get(Bandpass.V);
            closePair.VmagCombined = combinedMag(a, b); // two decimals
            closePair.separation = sep; // rads
            if (!alreadyIn(result, closePair)) {
              result.add(closePair);
              log(" " + closePair);
            }
          }
        }
      }
    }
    Collections.sort(result, bySeparation());
    return result;
  }
  
  // PRIVATE 

  private static final Double TEN_ARCMIN = Maths.degToRads(0.1667); 

  /** Radians, to two decimals. See Astronomical Algorithms, Meeus. */
  private Double separationBetween(Star starA, Star starB) {
    Position a = new Position(Double.valueOf(starA.RA), Double.valueOf(starA.DEC));
    Position b = new Position(Double.valueOf(starB.RA), Double.valueOf(starB.DEC));
    Double result = sepLarge(a, b); //0..pi
    if (result <= TEN_ARCMIN) {
      result = sepSmall(a, b); //rads
    }
    return result;
  }
  
  /** Return 0..pi rads. */
  private Double sepLarge(Position a, Position b) {
    double cosD = sin(a.δ) * sin(b.δ) + cos(a.δ) * cos(b.δ) * cos(a.α - b.α);
    return acos(cosD); //0..pi
  }
  
  /** Bayer or Flamsteed, if present. */
  private String nameFor(Star star) {
    String result = star.IDENTIFIERS.get(Identifier.BAYER);
    if (Util.isBlank(result)) {
      result = star.IDENTIFIERS.get(Identifier.FLAMSTEED);
    }
    if (Util.isBlank(result)) {
      result = "";
    }
    return result;
  }
  
  /** All units are rads. */
  private Double sepSmall(Position a, Position b) {
    double Δα = a.α - b.α;
    double Δδ = a.δ - b.δ;
    double δ = (a.δ + b.δ)/2.0;
    double val = Maths.sqr(Δα * cos(δ)) + Maths.sqr(Δδ);
    return sqrt(val);
  }
  
  private static double roundTwoDecimals(double val) {
    return round(val*100)/100.0;
  }

  /**
   Round to two decimals.  
   Ref: https://en.wikipedia.org/wiki/Apparent_magnitude#Magnitude_addition  
  */
  private Double combinedMag(Star starA, Star starB) {
    Double m1 = Double.valueOf(starA.MAGNITUDES.get(Bandpass.V));
    Double m2 = Double.valueOf(starB.MAGNITUDES.get(Bandpass.V));
    
    double a = pow(10, -0.4*m1) + pow(10, -0.4*m2);
    double b = log10(a);
    double result = -2.5 * b;
    return roundTwoDecimals(result);
  }
  
  private Comparator<CloseDouble> bySeparation() {
    Comparator<CloseDouble> c = new Comparator<CloseDouble>() {
      @Override public int compare(CloseDouble a, CloseDouble b) {
        int result = Double.compare(a.separation, b.separation);
        return result;
      }
    };
    return c;
  }
  
  private boolean alreadyIn(List<CloseDouble> list, CloseDouble candidate) {
    boolean result = false;
    for(CloseDouble pair : list) {
      boolean straightMatch = match(pair.hipA, candidate.hipA) && match(pair.hipB, candidate.hipB);
      boolean reverseMatch = match(pair.hipA, candidate.hipB) && match(pair.hipB, candidate.hipA);
      if(straightMatch || reverseMatch) {
        result = true;
        break;
      }
    }
    return result;
  }
  
  private boolean match(String a, String b) {
    return a.equals(b);
  }
} 