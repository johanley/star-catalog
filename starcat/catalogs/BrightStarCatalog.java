package starcat.catalogs;

import static starcat.star.Catalog.BSC;

import starcat.util.GreekLetters;
import static starcat.star.Flag.MAGNITUDE_SOURCE;
import static starcat.star.Flag.MULTIPLICITY;
import static starcat.star.Flag.VARIABILITY;
import static starcat.star.Flag.VARIABLE_RADIAL_VELOCITY;
import static starcat.star.Identifier.HD;
import static starcat.star.Identifier.HR;
import static starcat.util.LogUtil.log;

import java.util.List;

import starcat.star.Bandpass;
import starcat.star.Identifier;
import starcat.star.Star;
import starcat.util.Parser;
import starcat.util.Util;

/** Yale Bright Star Catalog, revision 5 (1991). */
public final class BrightStarCatalog implements CoreCatalog {

  @Override public Star parse(String line) {
    String SPACE = " "; 
    Star result = new Star();
    p.identifier(HR, 1, 4, line, result);
    
    String constellation = p.slice(line, 12, 3);
    if (Util.isPresent(constellation)) {
      String bayer = p.slice(line, 8, 4);
      if (Util.isPresent(bayer)) {
        String greekLetter = GreekLetters.greekLetter(bayer);
        result.IDENTIFIERS.put(Identifier.BAYER, greekLetter + SPACE + constellation);
      }
      String flamsteed = p.slice(line, 5, 3);
      if (Util.isPresent(flamsteed)) {
        result.IDENTIFIERS.put(Identifier.FLAMSTEED, flamsteed + SPACE + constellation);
      }
    }
    
    p.identifier(HD, 26, 6, line, result);
    /*
    p.identifier(DM, 15, 11, line, result);
    p.identifier(SAO, 32, 6, line, result);
    p.identifier(FK5, 38, 4, line, result);
    */

    String mag = p.slice(line, 103, 5);
    if (Util.isPresent(mag)) {
      result.MAGNITUDES.put(Bandpass.V, mag);
    }
    
    p.flag(VARIABILITY, 52, 9, line, result); //non-blank means variable
    
    p.flag(MAGNITUDE_SOURCE, 108, 1, line, result); //three sources: V, HR reduced to V, or HR
    
    result.RA = p.slice(line, 76,2+2+4);
    result.DEC = p.slice(line, 84, 1+2+2+2);
    
    result.PROPER_MOTION_RA = p.slice(line, 149, 6); //arcsecs per year 
    result.PROPER_MOTION_DEC = p.slice(line, 155, 6); //arcsecs per year
    
    result.PARALLAX = p.slice(line, 162, 5);
    
    result.RADIAL_VELOCITY = p.slice(line, 167, 4);
    String variableRV = p.sliceMultiFlag(line, 108, 1, "V"); //this gets both 'V' and 'V?'
    if (Util.isPresent(variableRV)) {
      result.FLAGS.put(VARIABLE_RADIAL_VELOCITY, variableRV);
    }
    
    p.flag(MULTIPLICITY, 44, 1, line, result);  //non-blank means binary/multiple
    
    result.SPECTRAL_TYPE = p.slice(line, 128, 20);
    return result;
  }
  
  /** Ignores the 1983 supplement for revision 4. */
  @Override public List<Star> records() {
    return records(BSC);
  }

  /** Basic stats for the list of stars. Missing-field counts, and so on.*/
  @Override public void statsFor(List<Star> stars) {
    log("Bright Star Catalog r5.");
    log("  Num stars: " + stars.size());
    int countNoPosition = 0;
    int countNoRV = 0;
    int countNoParallax = 0;
    int countNoProperMotion = 0;
    int countNoMag = 0;
    int countVariableMag = 0;
    int countMultiple = 0;
    int countVariableRV = 0;
    int countNoHD = 0;
    int countFaint = 0;
    int countNoSpectrum = 0;
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
      else {
        Double mag = Double.valueOf(star.MAGNITUDES.get(Bandpass.V));
        if (mag > 6.5) {
          ++countFaint;
        }
      }
      if (Util.isBlank(star.RA)) {
        ++countNoPosition;
      }
      if (star.FLAGS.containsKey(VARIABILITY)) {
        ++countVariableMag;
      }
      if (star.FLAGS.containsKey(MULTIPLICITY)) {
        ++countMultiple;
      }
      if (star.FLAGS.containsKey(VARIABLE_RADIAL_VELOCITY)) {
        ++countVariableRV;
      }
      if (star.IDENTIFIERS.get(HD) == null) {
        ++countNoHD;
      }
      if (Util.isBlank(star.SPECTRAL_TYPE)) {
        ++countNoSpectrum;
      }
    }
    log("  No position: " + countNoPosition);
    log("  No proper motion: " + countNoProperMotion);
    log("  No RV: " + countNoRV);
    log("  No parallax: " + countNoParallax);
    log("  No V mag: " + countNoMag);
    log("  No HD: " + countNoHD);
    log("  No spectral type: " + countNoSpectrum);
    log("  Variable RV : " + countVariableRV);
    log("  Variable V (or suspected): " + countVariableMag);
    log("  Binary or multiple: " + countMultiple);
    log("  V > 6.5: " + countFaint);
  }
  
  // PRIVATE

  private Parser p = new Parser();

}
