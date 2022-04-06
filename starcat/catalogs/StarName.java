package starcat.catalogs;

import static starcat.util.LogUtil.log;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import starcat.star.Catalog;
import starcat.util.Consts;
import starcat.util.DataFileReader;

/** 
 Proper name for a star, as in 'Sirius' or 'Vega'.
 
 This data is not a catalog, just my own data, not published anywhere.
*/
public final class StarName {

  /**
   For the given constellation abbreviaton and Bayer designation as a Greek letter,
   return the proper name of the star.
   For example, input of 'α Ari' returns 'Hamal'.
   Returns an empty string if not found. 
  */
  public String nameFor(String greekLetter, String constellationAbbr) {
    if (starNames.size() == 0) {
      readSourceDataIntoMemory();
    }
    
    String result = "";
    Map<String, String> forConstellation = starNames.get(constellationAbbr);
    if (forConstellation != null) {
      String starName = forConstellation.get(greekLetter);
      result = starName != null ? starName : "";
    }
    return result;
  }

  // PRIVATE
  
  private static Map<String /*UMa*/, Map<String/*α*/, String /*Dubhe*/>> starNames = new LinkedHashMap<>();
  private static final String BLANK = " ";
  
  private void readSourceDataIntoMemory() {
    DataFileReader reader = new DataFileReader();
    String dataFile = DataFileReader.inputFile(Catalog.STAR_PROPER_NAMES);
    List<String> lines = reader.readFile(dataFile);
    int lineCount = 0;
    for(String line : lines) {
      if (line.startsWith(Consts.COMMENT) || line.trim().length() == 0){
        continue;
      }
      else {
        ++lineCount;
        processLine(line.trim());
      }
    }
    log("Processed star names for this many constellations: " + lineCount);
  }
  
  /** line: 'Ari α=Hamal, β=Sheratan' */
  private void processLine(String line) {
    if (line.length() == 3) {
      starNames.put(line, new LinkedHashMap<>()); //no names for this constellation
    }
    else {
      int firstBlank = line.indexOf(BLANK);
      String constellationAbbr = line.substring(0, firstBlank);
      
      String[] names = line.substring(firstBlank).split(Pattern.quote(","));
      Map<String, String> namesMap = new LinkedHashMap<>();
      for(String name : names) {
          String[] parts = name.split(Pattern.quote("="));
          namesMap.put(parts[0].trim(), parts[1].trim());
      }
      starNames.put(constellationAbbr, namesMap);
    }
  }
}