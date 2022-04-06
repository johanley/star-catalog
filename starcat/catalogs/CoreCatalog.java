package starcat.catalogs;

import java.util.ArrayList;
import java.util.List;

import starcat.star.Catalog;
import starcat.star.Star;
import starcat.util.DataFileReader;

/** 
 Core catalog data.
 
 <P>This interface is somewhat brittle, because it depends on how you choose the data that's of interest to you.
 That is, it depends on the data you decide to put in the {@link Star} class. 
*/
public interface CoreCatalog {
  
  /** Parse a line of text in a catalog into a {@link Star} object. */
  Star parse(String line);
  
  /** Return all of the lines in the catalog. */
  List<Star> records();
  
  /** Show simple stats for the catalog. */
  void statsFor(List<Star> stars);
  
  /** 
   Default method for returning the list of records in the given {@link Catalog}.
   The data is located under the 'catalogs/input' directory in this project. 
  */
  default List<Star> records(Catalog catalog) {
    List<Star> result = new ArrayList<>();
    DataFileReader reader = new DataFileReader();
    String dataFile = DataFileReader.inputFile(catalog);
    List<String> lines = reader.readFile(dataFile);
    for(String line : lines) {
      result.add(parse(line));
    }
    return result;
  }
}
