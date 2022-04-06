package starcat.util;

import java.util.regex.Pattern;

import starcat.star.ColorIndex;
import starcat.star.Flag;
import starcat.star.Identifier;
import starcat.star.Star;
import starcat.star.Error;

/** Help parse a line from a catalog into a {@link Star} object. */
public final class Parser {

  /** The start index is 1-based.  */
  public String slice(String line, int start /*1-based*/, int numchars){
    String result = "";
    try {
      result = line.substring(start-1, start-1+numchars).trim();
    }
    catch(StringIndexOutOfBoundsException ex) {
      //some tables will not pad on the right - just fail silently
    }
    return result;
  }
  
  /** 1 if missing, otherwise 0. Used to produce counts of missing items. */
  public int missing(String thing) { 
    return Util.isBlank(thing) ? 1 : 0;
  }

  /** Some fields have N flags in a field. This method will treat a field as if it has only the given flag (or not). */
  public String sliceMultiFlag(String line, int start, int numchars, String flag) {
    String result = slice(line, start, numchars);
    if (result.matches(Pattern.quote(flag))) {
      result = flag;
    }
    return result;
  }

  /** Add/update an identifier for a Star. */
  public void identifier(Identifier id, int start, int numchars, String line, Star result) {
    String text = slice(line, start, numchars);
    if (Util.isPresent(text)) {
      result.IDENTIFIERS.put(id, text);
    }
  }

  /**
   Add/update a flag for a Star.
   Avoid if the field is a 'multiflag', having multiple meanings. 
  */
  public void flag(Flag flag, int start, int numchars, String line, Star result) {
    String text = slice(line, start, numchars);
    if (Util.isPresent(text)) {
      result.FLAGS.put(flag, text);
    }
  }
  
  /** Add/update an color index for a Star. */
  public void colorIndex(ColorIndex colorIdx, int start, int numchars, String line, Star result) {
    String text = slice(line, start, numchars);
    if (Util.isPresent(text)) {
      result.COLOR_INDICES.put(colorIdx, text);
    }
  }
  
  /** Add/update an error (in a measurement) for a Star. */
  public void uncertainty(Error uncertainty, int start, int numchars, String line, Star result) {
    String text = slice(line, start, numchars);
    if (Util.isPresent(text)) {
      result.ERRORS.put(uncertainty, text);
    }
  }
}
