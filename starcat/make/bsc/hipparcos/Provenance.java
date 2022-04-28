package starcat.make.bsc.hipparcos;

import java.util.LinkedHashMap;
import java.util.Map;

/** 
 Simple codes to identify the source for a given field.
 
 <P>WARNING: this class is very brittle.
 It must be reviewed every time there is a non-trivial change in the implementation.
*/
final class Provenance {
  
  /**
   The sources of data in the generated catalog.
   They are assigned the following one-letter codes:
  */
  enum Source {
    
    /** Primary source for astrometry (A).  */
    Hipparcos2("A"),
    
    /** Secondary source for astrometry (B). */
    Hipparcos1("B"),
    
    /** Primary source for radial velocities (C). */
    Pulkovo("C"),
    
    /**  Secondary source for radial velocities (D).  */
    BF("D"),
    
    /** Identifiers: Bayer, Flamsteed, and HR (E). */
    BrightStarCatalog("E"),
    
    /** Backfill for a small number of items (F). */
    Simbad("F"),
    
    /** My own custom data for star names (G). */
    CustomData("G"), 
    
    /** Sexagesimal versions of RA, DEC (H). */
    Calculated("H"),

    /** The Vmag for some variable stars can be overwritten by the maximum brightness found in the variability annex. */
    HipparcosVariabilityAnnex("I"),
    
    /** Used for blank fields (-). */
    None("-");
    
    String code() { return code; }
    private Source(String code) {
      this.code = code;
    }
    private String code = "";
  }

  /** Default blank provenance string with the given number of fields, consisting only of blank spaces. */
  Provenance(int numFields){
    for(int count=1; count <= numFields; ++count) {
      provenance.put(count, Source.None);
    }
  }
  
  /** 
   Override the default provenance for the given field.
   The field must match the position of the field, in order from left to right in the catalog record.
   The first column is column 1. 
  */
  void put(Integer field, Source source) {
    provenance.put(field, source);
  }
  
  /**
   Override the default provenance for the given fields.
   The field must match the position of the field, in order from left to right in the catalog record.
   The first column is column 1. 
  */
  void put(Source source, Integer... fields) {
    for(Integer field : fields) {
      put(field, source);
    }
  }
  
  /** Return the source for the given field. */
  Source get(Integer field) {
    return provenance.get(field);
  }
  
  /** 
   Formatted provenance string used in the catalog.
   One letter per field, in order of appearance in the catalog. 
  */
  @Override public String toString() {
    String result = "";
    for(Integer field : provenance.keySet()) {
      result = result + provenance.get(field).code();
    }
    return result;
  }

  /** The number of fields in the catalog, excluding the provenance field. */
  static final int NUM_FIELDS_EXCLUDING_PROVENANCE = 26;
  
  /** The fields that contain core astrometric data. */
  static final Integer[] ASTROMETRY_NO_RV = {4,5,6,7,8,10,11,12,13,14};

  private final Map<Integer /*1-based field index*/, Source> provenance = new LinkedHashMap<>();
}
