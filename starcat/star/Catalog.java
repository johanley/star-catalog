package starcat.star;

/** 
 Star catalogs, their name file location.
 Mostly input catalogs, but also catalogs output by this project. 
*/
public enum Catalog {
  
  /** Hipparcos (1997). Hipparcos-2 has improved astrometry in comparios with Hipparcos-1, but lacks Spectral Type and HD. */
  HIPPARCOS("hipparcos", "hip_main.dat"), 
  
  /** Hipparcos-2 (2007). Lacks HD and Spectral Type, but has better astrometry than Hipparcos-1. */
  HIPPARCOS2("hipparcos-2", "hip2.dat"), 
  
  /** Yale Bright Star Catalog, revision 5 (1991). */
  BSC("bright-star-catalog", "catalog.utf8"),
  
  /** Supplement to revision 4 of the Yale Bright Star Catalog (1983). */
  BSCS("bright-star-catalog-supplement", "bcs4s.dat"),
  
  /** Pulkovo Radial Velocities (2006). */
  PULKOVO("pulkovo-radial-velocities", "table8.dat"), 
  
  /** General Catalog of Mean Radial Velocities, Barbier-Brossat and Figon (2000). */
  GC_MEAN_RV("general-catalog-of-mean-rv", "catalog.dat"),

  /** An output catalog for this project. */
  OS_BSC_HIPPARCOS("open-source-bsc", "os-bright-star-catalog-hip.utf8"), 
  
  /** 
   A listing of star names, internal to this project. 
   Not a real catalog, but it's useful to treat it as such. 
  */
  STAR_PROPER_NAMES("star-names", "proper-names.utf8");
  
  /** Location of the catalog's files. */
  public String directory() {
    return directory;
  }
  
  /**  The main data file in the catalog. Simple name, without the directory. */
  public String dataFile() {
    return fileName;
  }
  
  private String directory = "";
  private String fileName = "";
  private Catalog(String directory, String fileName){
    this.directory = directory;
    this.fileName = fileName;
  }

}
