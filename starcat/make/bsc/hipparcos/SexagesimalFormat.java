package starcat.make.bsc.hipparcos;

import static starcat.util.LogUtil.log;

import java.text.DecimalFormat;

import starcat.util.Maths;

/** 
 Format RA and DEC using the sexagesimal style.
 
 This class uses a '_' character to join together the pieces, such 
 that they appear as a single unit in the catalog. 
 This makes it easier to read.
*/
final class SexagesimalFormat {

  /** Format as 02_16_22.1234567, for example, to 7 decimal places (same as VizieR). */
  static String radsToTimeString(String rads) {
    double val = Math.abs(Double.valueOf(rads));
    
    double hrs = Maths.radsToHours(val); //6.123
    double hours = Math.floor(hrs); //6
    double mins = (hrs - hours) * 60; // 0.123 * 60 = 7.38
    double minutes = Math.floor(mins); //7
    double secs = (mins - minutes) * 60; // 0.38 * 60 =  22.8
    
    String result = pad0((int)hours) + "_" + pad0((int)minutes) + "_" + pad0(sevenDecimals(secs), "00.0000000");
    return result;
  }
  
  /** Format as 09_16_22.123456, for example, to 6 decimals (same as VizieR). */
  static String radsToDegreeString(String rads) {
    Double r = Double.valueOf(rads);
    double val = Math.abs(r);
    
    double degs = Maths.radsToDegs(val); //25.123
    double degrees = Math.floor(degs); //25
    double arcmins = (degs - degrees) * 60; // 0.123 * 60 = 7.38
    double arcminutes = Math.floor(arcmins); //7
    double arcsecs = (arcmins - arcminutes) * 60; // 0.38 * 60 =  22.8
    
    String result = pad0((int)degrees) + "_" + pad0((int)arcminutes) + "_" + pad0(sixDecimals(arcsecs), "00.000000");
    if (r<0) {
      result = "-" + result;
    }
    return result;
  }

  /**
   This seems to only happen when Hipparcos-2 has no solution for proper motion and so on.
   It only happens for a few records.
   Convert a HMS to rads, with 10 decimal places in the final result. 
   Used to convert Hipparcos-1 RA/DEC data to the format used by Hipparcos-2 RA/DEC (rads).  
  */
  static String radsFromHourMinSec(String hms /*hh mm ss: 23 19 13.98 */) {
    String[] parts = hms.split(" ");
    double hour = Double.valueOf(parts[0]);
    double min = Double.valueOf(parts[1]);
    double sec = Double.valueOf(parts[2]);
    double hrs = hour + min/60.0 + sec/3600.0; //avoid int div
    double degs = hrs * 15;
    String result = radsTenDecimalPlaces(degs);
    log("  Transform HMS: " + hms + " to " + result + " rads.");
    return result;
  }
  
  /** Convert a DMS format to rads, with 10 decimal places in the final result. */
  static String radsFromDegMinSec(String dms /* deg min sec: +40 07 16.5*/) {
    String[] parts = dms.split(" ");
    double deg = Double.valueOf(parts[0]);
    double min = Double.valueOf(parts[1]);
    double sec = Double.valueOf(parts[2]);
    double degs = deg + min/60.0 + sec/3600.0; //avoid int div
    String result = radsTenDecimalPlaces(degs);
    log("  Transform DMS: " + dms + " to " + result + " rads.");
    return result;
  }
  
  // PRIVATE 
  
  static private String pad0(Integer val) {
    DecimalFormat format = new DecimalFormat("00");
    return format.format(val);
  }
  
  private static String pad0(Double val, String fmt) {
    DecimalFormat format = new DecimalFormat(fmt);
    return format.format(val);
  }
  
  private static double sevenDecimals(double val) {
    double factor = 10000000.0;
    return Math.round(val * factor) / factor; //avoid int div
  }
  
  private static double sixDecimals(double val) {
    double factor = 1000000.0;
    return Math.round(val * factor) / factor; //avoid int div
  }
  
  private static String radsTenDecimalPlaces(Double degs) {
    Double rads = Maths.degToRads(degs);
    double FACTOR = 10000000000.0;
    Double result = Math.round(rads * FACTOR) / FACTOR;
    return result.toString();
  }
}
