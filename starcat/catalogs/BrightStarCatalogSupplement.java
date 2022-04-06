package starcat.catalogs;

import static starcat.star.Catalog.BSCS;
import static starcat.star.Flag.VARIABLE_RADIAL_VELOCITY;
import static starcat.star.Identifier.DM;
import static starcat.star.Identifier.HD;
import static starcat.star.Identifier.SAO;
import static starcat.util.LogUtil.log;

import java.util.List;

import starcat.star.Bandpass;
import starcat.star.Star;
import starcat.util.Parser;
import starcat.util.Util;

/**
 The Supplement to revision 4 of the Yale Bright Star Catalog.
*/
public final class BrightStarCatalogSupplement implements CoreCatalog {
  
  /** Include only the 1983 supplement for r4. */
  @Override public List<Star> records() {
    return records(BSCS);
  }
  
  /** Basic stats for the list of stars. Missing-field counts, and so on.*/
  @Override public void statsFor(List<Star> stars) {
    log("Bright Star Catalog r4 supplement.");
    log("  Num stars: " + stars.size());
    int countNoPosition = 0;
    int countNoRV = 0;
    int countNoParallax = 0;
    int countNoProperMotion = 0;
    int countNoMag = 0;
    int countVariableRV = 0;
    int countNoHD = 0;
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
      if (star.MAGNITUDES.size() == 0 || Util.isBlank(star.MAGNITUDES.get(Bandpass.V))) {
        ++countNoMag;
      }
      if (Util.isBlank(star.RA)) {
        ++countNoPosition;
      }
      if (star.FLAGS.containsKey(VARIABLE_RADIAL_VELOCITY)) {
        ++countVariableRV;
      }
      if (star.IDENTIFIERS.get(HD) == null) {
        ++countNoHD;
      }
    }
    log("  No position: " + countNoPosition);
    log("  No proper motion: " + countNoProperMotion);
    log("  No RV: " + countNoRV);
    log("  No parallax: " + countNoParallax);
    log("  No V mag: " + countNoMag);
    log("  No HD: " + countNoHD);
    log("  Variable RV : " + countVariableRV);
  }
  
  @Override public Star parse(String line) {
    Star result = new Star();
    p.identifier(HD, 1, 6, line, result);
    p.identifier(DM, 9, 10, line, result);
    p.identifier(SAO, 20, 6, line, result);
    
    result.MAGNITUDES.put(Bandpass.V, p.slice(line, 105, 4));
    
    result.RA = p.slice(line, 70, 2+2+4+2);
    result.DEC = p.slice(line, 84, 1+2+2+2+2);
    
    result.PROPER_MOTION_RA = p.slice(line, 149, 6); //arcsecs per year 
    result.PROPER_MOTION_DEC = p.slice(line, 156, 6); //arcsecs per year
    
    result.PARALLAX = p.slice(line, 163, 5);
    
    result.RADIAL_VELOCITY = p.slice(line, 169, 4);
    String variableRV = p.sliceMultiFlag(line, 173, 1, "V"); //this gets both 'V' and 'V?'
    if (Util.isPresent(variableRV)) {
      result.FLAGS.put(VARIABLE_RADIAL_VELOCITY, variableRV);
    }
    
    //no flags for variability, multiplicity?
    
    result.SPECTRAL_TYPE = p.slice(line, 128, 20);
    return result;
  }

  // PRIVATE

  private Parser p = new Parser();
  
}
