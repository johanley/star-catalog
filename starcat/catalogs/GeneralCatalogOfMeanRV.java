package starcat.catalogs;

import static starcat.star.Catalog.GC_MEAN_RV;
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
 General catalog of averaged stellar radial velocities for galactic stars,  Barbier-Brossat M., Figon P. (2000).
*/
public final class GeneralCatalogOfMeanRV implements CoreCatalog {
  
  @Override public List<Star> records() {
    return records(GC_MEAN_RV);
  }
  
  /** Basic stats for the list of stars. Missing-field counts, and so on.*/
  @Override public void statsFor(List<Star> stars) {
    log("General Catalog of Mean Radial Velocities.");
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

  /** This class alters the format of the radial velocity, as it has a strange format in the source catalog. */
  @Override public Star parse(String line) {
    Star result = new Star();
    p.identifier(HIP, 65, 6, line, result); // the HIC id is the same as the HIP id
    p.identifier(HD, 7, 6, line, result);
    result.RADIAL_VELOCITY = normalizedFormat(p.slice(line, 113, 7));
    String mag = p.slice(line, 95, 5);
    if (Util.isPresent(mag)) {
      result.MAGNITUDES.put(Bandpass.V, mag);
    }

    p.uncertainty(Error.RADIAL_VELOCITY, 122, 4, line, result);
    
    return result;
  }
  
  // PRIVATE

  private Parser p = new Parser();
  
  /**
   Radial velocity data from the General Catalog has an odd format. 
   Let's coerce it to be the same as the Pulkovo catalog, which has a more reasonable format:
     if it ends in '.' add a 0
     remove leading +
     remove leading 0s
  */
  private String normalizedFormat(String rv) {
    String result = rv;
    if (result.endsWith(".")) {
      result = result + "0";
    }
    if (result.startsWith("+")) {
      result = result.substring(1);
    }
    Double dbl = Double.valueOf(result);
    String finalAnswer = dbl.toString();
    return finalAnswer;
  }
  
}
