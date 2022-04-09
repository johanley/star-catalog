package starcat.star;

/** Names of selected star identifiers. */
public enum Identifier {
  
  /** Greek letter plus constellation (usually abbreviated). Not often used in modern catalogs. */
  BAYER, 
  
  /** Bonner Durchmusterung. Sometimes this is broken down into 3 separate identifiers. */
  DM,
  
  /** Fifth fundamental catalog.  */
  FK5,
  
  /** Flamsteed number. Usually not used in modern catalogs. */
  FLAMSTEED,
  
  /** Henry Draper. */
  HD,
  
  /** Hipparcos. */
  HIP,
  
  /** Bright Star Catalog. */
  HR,
  
  /** Example: 'Sirius'. */
  PROPER_NAME,
  
  /** Catalog of Components of Double & Multiple stars (Dommanget+ 2002). */
  CCDM,
  
  /** Smithsonian Astrophysical Observatory. */
  SAO, 
  
  /** The constellation to which a star belongs. Not a unique identifier, but it's convenient for this program to treat it as such. */
  CONSTELLATION;

}
