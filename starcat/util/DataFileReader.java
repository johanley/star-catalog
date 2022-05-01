package starcat.util;

import static starcat.util.LogUtil.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import starcat.star.Catalog;

/**
 Catalog file operations.
*/
public final class DataFileReader {

  /** Where to find an input catalog file. You must set a system property named 'project-root'. */
  public static String inputFile(Catalog catalog) {
    return directory(catalog, "input", catalog.dataFile());
  }
  
  /** Where to output a catalog file. You must set a system property named 'project-root'. */
  public static String outputFile(Catalog catalog) {
    return directory(catalog, "output", catalog.dataFile());
  }
  
  /** Where to output a file related to a catalog. You must set a system property named 'project-root'. */
  public static String outputFile(Catalog catalog, String fileName) {
    return directory(catalog, "output", fileName);
  }
  
  /**
  Read a text file and return it as a list of (untrimmed) Strings.
   @param fileName the full name of UTF-8 text file. 
  */
  public List<String> readFile(String fileName) {
    List<String> result = new ArrayList<>();
    try {
      Path path = Paths.get(fileName);
      result = Files.readAllLines(path, Consts.ENCODING);
    }
    catch(IOException ex) {
      log("CANNOT OPEN FILE: " + fileName);
    }
    return result;
  }
  
  private static String directory(Catalog catalog, String inout, String filename) {
    String rootDir = System.getProperty("project-root");
    if (Util.isBlank(rootDir)) {
      throw new RuntimeException("You must set a System property named 'project-root', and point it to the root directory of this project.");
    }
    String sep = File.separator;
    return rootDir + sep + "catalogs" + sep + inout + sep + catalog.directory() + sep + filename;
  }
}