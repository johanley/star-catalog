package starcat.catalogs;

import static starcat.star.Catalog.HIPPARCOS2;
import static starcat.star.Identifier.HIP;
import static starcat.util.LogUtil.log;

import java.util.List;

import starcat.star.Star;
import starcat.star.Error;
import starcat.util.Parser;
import starcat.util.Util;

/**
 Hipparcos-2 catalog (2007).
 This catalog is focused soley on recalculating the astrometry from the Hipparcos mission.
 The number of fields in this catalog is much less than that in Hipparcos-1 (1997). 
*/
public final class Hipparcos2Catalog implements CoreCatalog {
  
  @Override public Star parse(String line) {
    Star result = new Star();
    p.identifier(HIP, 1, 6, line, result);
    //p.identifier(HIP, 9, 6, line, result);
    
    result.RA = p.slice(line, 16, 13);
    result.DEC = p.slice(line, 30, 13);
    
    result.PROPER_MOTION_RA = p.slice(line, 52, 8);  
    result.PROPER_MOTION_DEC = p.slice(line, 61, 8); 
    
    result.PARALLAX = p.slice(line, 44, 7);

    p.uncertainty(Error.RA, 70, 6, line, result);
    p.uncertainty(Error.DEC, 77, 6, line, result);
    p.uncertainty(Error.PARALLAX, 84, 6, line, result);
    p.uncertainty(Error.PROPER_MOTION_RA, 91, 6, line, result);
    p.uncertainty(Error.PROPER_MOTION_DEC, 98, 6, line, result);
    
    return result;
  }
  
  @Override public List<Star> records() {
    return records(HIPPARCOS2);
  }
  
  /** Basic stats for the list of stars. Missing-field counts, and so on.*/
  @Override public void statsFor(List<Star> stars) {
    log("Hipparcos-2 Catalog.");
    log("  Num stars: " + stars.size());
    int countNoPosition = 0;
    int countNoRV = 0;
    int countNoParallax = 0;
    int countNoProperMotion = 0;
    for(Star star : stars) {
      if (Util.isBlank(star.RADIAL_VELOCITY)) {
        ++countNoRV;
      }
      if (Util.isBlank(star.PARALLAX)) {
        ++countNoParallax;
      }
      if (Util.isBlank(star.PROPER_MOTION_RA)) {
        ++countNoProperMotion;
      }
      if (Util.isBlank(star.RA)) {
        ++countNoPosition;
      }
    }
    log("  No position: " + countNoPosition);
    log("  No proper motion: " + countNoProperMotion);
    log("  No RV: " + countNoRV);
    log("  No parallax: " + countNoParallax);
  }

  // PRIVATE

  private Parser p = new Parser();
}
