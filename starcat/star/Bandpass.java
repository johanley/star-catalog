package starcat.star;

/** 
 Every magnitude measurement is made in a specific bandpass.
 Instruments such as Hipparcos and Gaia have their own bandpasses, instead of the Johnson UBV standards. 
*/
public enum Bandpass {
  
  V, Vt, Bt, Hp;

}
