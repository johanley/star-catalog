package starcat.star;

/** 
 The error for selected fields.
 
 The exact definition varies according to catalog:
 <ul>
  <li>standard error (Hipparcos) 
  <li>formal error (Hipparcos-2)
  <li>mean error (Pulkovo)
  <li>mean standard error (General Catalog of Mean RV)
 </ul> 
*/
public enum Error {
  RA, DEC, PARALLAX, PROPER_MOTION_RA, PROPER_MOTION_DEC, RADIAL_VELOCITY;
}
