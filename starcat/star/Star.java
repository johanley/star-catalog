package starcat.star;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** 
 Core data for stars, as a struct-like data structure.
 
 <P>The units used are left up to the caller.
 
 <P>Only text is used here, to ensure that no changes to the underlying data are applied (at least by this class).
 This class is intended mostly for these kinds of operations: parse, join, sort, filter, and finding blank fields.
 
 <P>If a calculation needs to be done with the data, then it will need to be converted to a numeric form.  
*/
public final class Star {

  /** Right ascension. */
  public String RA = "";
  
  /** Declination. */
  public String DEC = "";
  
  /** Parallax of the star. */
  public String PARALLAX = "";

  /** Proper motion in right ascension. */
  public String PROPER_MOTION_RA = "";
  
  /** Proper motion in declination. */
  public String PROPER_MOTION_DEC = "";

  /** Heliocentric radial velocity. */
  public String RADIAL_VELOCITY  = "";
  
  /** Spectral type. */
  public String SPECTRAL_TYPE = "";

  /** Magnitude in one or more bandpasses. */
  public Map<Bandpass, String> MAGNITUDES = new LinkedHashMap<>();
  
  /** Color indices, as in B-V and so on. A difference in magnitude between two bandpasses. */
  public Map<ColorIndex, String> COLOR_INDICES = new LinkedHashMap<>();
  
  /** Various identifiers for this star. */
  public Map<Identifier, String> IDENTIFIERS = new LinkedHashMap<>();

  /** Flags for various items (variablility, multiplicity, and so on). */
  public Map<Flag, String> FLAGS = new LinkedHashMap<>();
  
  /** The uncertainty for selected fields. The precise meaning of the uncertainty depends on the case. */
  public Map<Error, String> ERRORS = new LinkedHashMap<>();
  
  /** WARNING: this method uses only the HD identifier! This usually serves to identify the star in most catalogs. */
  @Override public boolean equals(Object aThat) {
    if (this == aThat) return true;
    if (!(aThat instanceof Star)) return false;
    Star that = (Star)aThat;
    for(int i = 0; i < this.getSigFields().length; ++i){
      if (!Objects.equals(this.getSigFields()[i], that.getSigFields()[i])){
        return false;
      }
    }
    return true;
  }
  
  @Override public int hashCode() {
    return Objects.hash(getSigFields());
  }  
  
  /** Identifiers only. */
  @Override public String toString() {
    return IDENTIFIERS + ""; //convert to a string
  }
  
  private Object[] getSigFields(){
    Object[] result = {
      IDENTIFIERS.get(Identifier.HD)
    };
    return result;
  } 
}
