package starcat.util;

/** Utility methods. */
public final class Util {
  
  /** True only if the text is not null, and its trimmed length is non-zero. */
  public static boolean isPresent(String text) {
    return text != null && text.trim().length() > 0;
  }
  
  /** The opposite of {@link #isPresent(String)}. */
  public static boolean isBlank(String text) {
    return !isPresent(text);
  }
  
}
