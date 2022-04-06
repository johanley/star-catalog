package starcat.catalogs;

import static starcat.star.Catalog.HIPPARCOS;
import static starcat.star.Flag.MAGNITUDE_SOURCE;
import static starcat.star.Flag.MULTIPLICITY;
import static starcat.star.Flag.VARIABILITY;
import static starcat.star.Identifier.*;
import static starcat.util.LogUtil.log;

import java.util.List;

import starcat.star.Bandpass;
import starcat.star.ColorIndex;
import starcat.star.Flag;
import starcat.star.Star;
import starcat.star.Error;
import starcat.util.Consts;
import starcat.util.Parser;
import starcat.util.Util;

/** Hipparcos-1 catalog (1997). */
public final class HipparcosCatalog implements CoreCatalog {
  
  @Override public Star parse(String line) {
    Star result = new Star();
    p.identifier(HIP, 9, 6, line, result);
    p.identifier(HD, 391, 6, line, result); //HD/HDE/HDEC

    String mag = p.slice(line, 42, 5);
    if (Util.isPresent(mag)) {
      result.MAGNITUDES.put(Bandpass.V, mag); //mixed bag! catalogs, calculated approximate value
    }
    p.flag(VARIABILITY, 48, 1, line, result); //non-blank means variable; three classes small, med, large range
    p.flag(MAGNITUDE_SOURCE, 50, 1, line, result); //three sources: ground, Hp, Tycho
    
    //ICRS, epoch J1991.25
    //"ICRS is consistent with the conventional coordinate system at J2000.0."
    //can't use the degree fields, since they aren't always present
    result.RA = p.slice(line, 18, 11); //hh mm ss: 23 19 13.98
    result.DEC = p.slice(line, 30, 11); //deg min sec: +40 07 16.5
    
    result.PROPER_MOTION_RA = p.slice(line, 88, 8);  
    result.PROPER_MOTION_DEC = p.slice(line, 97, 8); 
    
    result.PARALLAX = p.slice(line, 80, 7);
    
    p.uncertainty(Error.RA, 106, 6, line, result);
    p.uncertainty(Error.DEC, 113, 6, line, result);
    p.uncertainty(Error.PARALLAX, 120, 6, line, result);
    p.uncertainty(Error.PROPER_MOTION_RA, 127, 6, line, result);
    p.uncertainty(Error.PROPER_MOTION_DEC, 134, 6, line, result);

    /** 
     In H.59, 'C' denotes a 'regular' double/multiple system.
     The CCDM value in H.55 identifies all members of the system present in Hipparcos.
     
     In H.59 'GOVX' denote astrometric binaries, suspected binaries, and a few center-of-mass orbits.
     In this context, I'm only interested in the objects that an observer would have a chance of resolving.
     https://www.cosmos.esa.int/web/hipparcos/catalogue-summary
     https://www.cosmos.esa.int/documents/532822/553204/sect2_01.pdf/88f60038-085c-4cf0-9de0-42af104e8ae1
     Merely looking at the CCDM field is not appropriate, since that data is present for CGVOX, not just for C.
    */
    String multFlag = p.slice(line, 347, 1);
    if ("C".equals(multFlag)) {
      result.FLAGS.put(MULTIPLICITY, multFlag);
      p.identifier(CCDM, 328, 10, line, result);
    }
    
    result.SPECTRAL_TYPE = p.slice(line, 436, 12); //ground-based catalogs; not mission data
    
    p.colorIndex(ColorIndex.B_MINUS_V, 246, 6, line, result);
    
    //if the star is within 10" of another
    p.flag(Flag.PROXIMITY, 16, 1, line, result);
    
    return result;
  }
  
  @Override public List<Star> records() {
    return records(HIPPARCOS);
  }
  
  /** Basic stats for the list of stars. Missing-field counts, and so on.*/
  @Override public void statsFor(List<Star> stars) {
    log("Hipparcos Catalog.");
    log("  Num stars: " + stars.size());
    int countNoPosition = 0;
    int countNoRV = 0;
    int countNoParallax = 0;
    int countNoProperMotion = 0;
    int countNoMag = 0;
    int countVariableMag = 0;
    int countMultiple = 0;
    int countNoHD = 0;
    int countNoSpectralType = 0;
    int countBright = 0;
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
        if (mag <= Consts.MAGNITUDE_LIMIT) {
          ++countBright;
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
      if (star.IDENTIFIERS.get(HD) == null) {
        ++countNoHD;
      }
      if (Util.isBlank(star.SPECTRAL_TYPE)) {
        ++countNoSpectralType;
      }
    }
    log("  No position: " + countNoPosition);
    log("  No proper motion: " + countNoProperMotion);
    log("  No RV: " + countNoRV);
    log("  No parallax: " + countNoParallax);
    log("  No V mag: " + countNoMag);
    log("  No HD: " + countNoHD);
    log("  No spectral type: " + countNoSpectralType);
    log("  Variable V (or suspected): " + countVariableMag);
    log("  Binary or multiple: " + countMultiple);
    log("  V <= " + Consts.MAGNITUDE_LIMIT + ": " + countBright);
  }

  // PRIVATE

  private Parser p = new Parser();
}
