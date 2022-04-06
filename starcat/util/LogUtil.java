package starcat.util;

/** 
 Logging utility methods.
 
 Centralizing these policies makes it easier to change them, if the logging requirements change. 
*/
public final class LogUtil {

  /** Change this to false, to disable logging via this class. */
  public static final boolean LOGGING_ON = true;

  /** Output to the console. */
  public static void log(Object thing) {
    if (LOGGING_ON) {
      System.out.println(thing.toString());
    }
  }
  
  /** Output to the console, with an extra warning flag. */
  public static void warn(Object thing) {
    if (LOGGING_ON) {
      System.out.println("WARNING!!: " + thing.toString());
    }
  }
}