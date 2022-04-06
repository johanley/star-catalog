package starcat.util;

import java.util.LinkedHashMap;
import java.util.Map;
import static starcat.util.LogUtil.log;

/** Translate Latin text abbreviations as found in the Yale BSC into the actual Greek letters. */
public final class GreekLetters {

  /** Example input: 'Alp' for 'α', and 'Alp2' for 'α2'. */
  public static String greekLetter(String text /*Alp2, for example*/){
    String input = text;
    int len = text.length();
    char lastChar = text.charAt(len-1);
    if (Character.isDigit(lastChar)){
      input = text.substring(0, len-1); //without the number
    }
    String output = GREEK.get(input.trim());
    if (output == null){
      //log("Greek letter not found for: '"  + input + "'");
    }
    if (Character.isDigit(lastChar)){
      output = output + lastChar;
    }
    return output;
  }
  
  private static final Map<String, String> GREEK = new LinkedHashMap<String, String>();
  static {
    add("Alp", "α");
    add("Bet", "β");
    add("Gam", "γ");
    add("Del", "δ");
    add("Eps", "ε");
    add("Zet", "ζ");
    add("Eta", "η");
    add("The", "θ");
    add("Iot", "ι");
    add("Kap", "κ");
    add("Lam", "λ");
    add("Mu", "μ");
    add("Nu", "ν");
    add("Xi", "ξ");
    add("Omi", "ο");
    add("Pi", "π");
    add("Rho", "ρ");
    add("Sig", "σ");
    add("Tau", "τ");
    add("Ups", "υ");
    add("Phi", "φ");
    add("Chi", "χ");
    add("Psi", "ψ");
    add("Ome", "ω");
  }
  
  private static void add(String in, String out){
    GREEK.put(in, out);
  }
}
