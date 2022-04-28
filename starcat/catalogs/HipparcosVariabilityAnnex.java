package starcat.catalogs;

import java.util.ArrayList;
import java.util.List;

import starcat.star.Catalog;
import starcat.util.DataFileReader;
import starcat.util.Parser;
import starcat.variablestar.VariableStar;

/** Hipparcos-1 Variability Annex 1 and 2 (1997). */
public final class HipparcosVariabilityAnnex {
  
  /** This parses both annex 1 and annex 2. */
  public VariableStar parse(String line) {
    VariableStar result = new VariableStar();
    
    result.IDENTIFIER = p.slice(line, 1, 6);
    result.TYPE = p.slice(line, 25, 6);
    result.BANDPASS = p.slice(line, 140, 1);
    
    result.MAXIMUM = p.slice(line, 128, 5);
    result.MINIMUM = p.slice(line, 134, 5);
    
    result.NAME = p.slice(line, 93, 12);
    
    return result;
  }

  /** 
   Amalgamates records from both annex 1 and annex 2.
   Assumes there are no duplicate HIPs between the two annexes. 
  */
  public List<VariableStar> records() {
    List<VariableStar> result = new ArrayList<>();
    result.addAll(records(Catalog.HIPPARCOS_VA_1));
    result.addAll(records(Catalog.HIPPARCOS_VA_2));
    return result;
  }

  // PRIVATE

  private Parser p = new Parser();
  
  private List<VariableStar> records(Catalog catalog) {
    List<VariableStar> result = new ArrayList<>();
    DataFileReader reader = new DataFileReader();
    String dataFile = DataFileReader.inputFile(catalog);
    List<String> lines = reader.readFile(dataFile);
    for(String line : lines) {
      result.add(parse(line));
    }
    return result;
  }
}