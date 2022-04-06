package starcat.star;

/** Various indicators of interest, attached to a star's data. */
public enum Flag {
  
  /** The brightness of the star varies. */
  VARIABILITY,
  
  /** Binary or multiple star.*/
  MULTIPLICITY,
  
  /** Sometimes a magnitude field is calculated, not measured; or multiple sources can be used.*/
  MAGNITUDE_SOURCE,
  
  /** Astrometric catalogs often copy the spectral type from some other catalog.*/
  SPECTRAL_TYPE_SOURCE,
  
  /** Observations may indicate that the RV is not steady over time. */
  VARIABLE_RADIAL_VELOCITY, 
  
  /** 
   Boolean flag is true if the object is within a certain number of arcseconds of another.
   For Hipparcos, the limit is 10". 
  */
  PROXIMITY;
}
