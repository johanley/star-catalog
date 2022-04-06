package starcat.catalogs;

import static starcat.star.Catalog.PULKOVO;
import static starcat.star.Identifier.HD;
import static starcat.star.Identifier.HIP;
import static starcat.util.LogUtil.log;

import java.util.List;

import starcat.star.Bandpass;
import starcat.star.Star;
import starcat.star.Error;
import starcat.util.Parser;
import starcat.util.Util;

/**
 Pulkovo Compilation of Radial Velocities for 35493 Hipparcos Stars in a Common System, Gontcharov G.A. (2006).
*/
public final class PulkovoRVCatalog implements CoreCatalog {
  
  @Override public List<Star> records() {
    return records(PULKOVO);
  }
  
  /** Basic stats for the list of stars. Missing-field counts, and so on.*/
  @Override public void statsFor(List<Star> stars) {
    log("Pulkovo Radial Velocity Catalog.");
    log("  Num stars: " + stars.size());
    int countNoRV = 0;
    int countNoMag = 0;
    int countNoHD = 0;
    int countNoHIP = 0;
    for(Star star : stars) {
      if (Util.isBlank(star.RADIAL_VELOCITY)) {
        ++countNoRV;
      }
      if (star.MAGNITUDES.size() == 0 || Util.isBlank(star.MAGNITUDES.get(Bandpass.V))) {
        ++countNoMag;
      }
      if (star.IDENTIFIERS.get(HD) == null) {
        ++countNoHD;
      }
      if (star.IDENTIFIERS.get(HIP) == null) {
        ++countNoHIP;
      }
    }
    log("  No RV: " + countNoRV);
    log("  No HD: " + countNoHD);
    log("  No HIP: " + countNoHIP);
    log("  No V mag: " + countNoMag);
  }
  
  @Override public Star parse(String line) {
    Star result = new Star();
    p.identifier(HIP, 1, 6, line, result);
    p.identifier(HD, 8, 6, line, result);
    result.RADIAL_VELOCITY = p.slice(line, 14, 7);
    String mag = p.slice(line, 46, 5);
    if (Util.isPresent(mag)) {
      result.MAGNITUDES.put(Bandpass.V, mag);
    }
    
    p.uncertainty(Error.RADIAL_VELOCITY, 22, 3, line, result);
    
    return result;
  }
  
  // PRIVATE

  private Parser p = new Parser();
  
}
