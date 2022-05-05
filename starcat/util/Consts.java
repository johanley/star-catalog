package starcat.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/** Simple constants class.*/
public final class Consts {

  /** 
   The faintest star magnitude (usually in the V bandpass) for the generated catalog - {@value}.
   I found that using a value of 6.0 gives nearly 100% coverage for radial velocity, with the catalogs I'm now using. 
  */
  public static final Double MAGNITUDE_LIMIT = 6.0;
 
  /** The (UTF-8) encoding used to read and write text files. */
  public static final Charset ENCODING = StandardCharsets.UTF_8;

  /** The character that begins a comment, in a data file, {@value}. */
  public static final String COMMENT = "#";
  
  /** Separation limit for detecting close pairs of stars, in arcseconds. Value - {@value}. */
  public static final Double CLOSE_PAIR_SEPARATION_LIMIT = 120.0;

}
