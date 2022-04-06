package starcat.star;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static starcat.util.LogUtil.*;
import starcat.util.Util;

/** Utility methods. */
public final class StarTool {

  /** Filter by magnitude less than or equal to a given value. */
  public static List<Star> filterByMagLTEQ(Double mag, Bandpass bandpass, List<Star> stars){
    List<Star> result = new ArrayList<>();
    for (Star star : stars) {
      String m = star.MAGNITUDES.get(bandpass);
      if (Util.isPresent(m)) {
        Double magVal = Double.valueOf(m);
        if (magVal <= mag) {
          result.add(star);
        }
      }
    }
    return result;
  }
  
  /** Show counts of ID values in A but not B, and vice versa.  */
  public static void logIdsNotInCommon(List<Star> A, List<Star> B, Identifier identifier, String Aname, String Bname) {
    log("A:" + Aname + " (" + A.size() + ") B:" + Bname + " (" + B.size() + "). Using " + identifier + " identifier to cross-match.");
    log("  In A but not B: " + onlyInFirst(A, B, identifier).size());
    log("  In B but not A: " + onlyInFirst(B, A, identifier).size());
  }
  
  /** Return the IDs that are present only in List A. */
  public static Set<String> onlyInFirst(List<Star> A, List<Star> B, Identifier identifier){
    Set<String> aIDs = identifiersIn(A, identifier);
    Set<String> bIDs = identifiersIn(B, identifier);
    Set<String> result = new LinkedHashSet<>(aIDs);
    for (String aID : aIDs) {
      if (bIDs.contains(aID)) {
        result.remove(aID);
      }
    }
    return result;
  }
  
  private static Set<String> identifiersIn(List<Star> stars, Identifier identifier){
    Set<String> result = new LinkedHashSet<>();
    for(Star star : stars) {
      String id = star.IDENTIFIERS.get(identifier);
      if (Util.isPresent(id)) {
        result.add(id);
      }
    }
    return result;
  }
}
