package starcat.catalogs;

import static starcat.util.LogUtil.log;

import java.util.ArrayList;
import java.util.List;

import starcat.precession.Position;
import starcat.precession.Precession;
import starcat.star.Catalog;
import starcat.util.Consts;
import starcat.util.DataFileReader;
import starcat.util.Maths;
import starcat.util.Parser;

/**
  Find the constellation corresponding to a given J2000 position.
  
  References: 
  <ul>
   <li><a href='https://iopscience.iop.org/article/10.1086/132034/pdf'>Roman's paper</a>
   <li><a href='https://cdsarc.cds.unistra.fr/ftp/VI/42/program.c'>this implementation</a> in the C language
   <li>Delporte, E. 1930, Delimitation Scientifique des Constellations (Cambridge: Cambridge University Press).
  </ul>  
*/
public final class Constellation {
  
  /** 
   The given position must be a J2000 position.
  
   Equatorial J2000 coords are almost the same as ICRS coords.
   For the purpose of this class, they can be treated as identical.
     
   <P>This implementation will apply precession to the given position to 
   1875.0 (which I assume is Jan 1.5 1875, Julian Date 2405890.0), and compare that position
   with the boundary definitions (which are defined using 1875.0), to determine the corresponding constellation.
   
   <P>Statements that <em>star X is no longer in constellation Y, because of precession</em> misunderstand the algorithm.
   The boundaries don't move over the sky, over time. They are attached to the epoch of 1875.0.
   
   @param raJ2000 in radians
   @param decJ2000 in radians
  */
  public String constellationFor(String raJ2000, String decJ2000) {
    if (table == null) readSourceDataIntoMemory();
    
    String result = tableScanAlgorithm(raJ2000, decJ2000);
    return result;
  }
  
  /** The Julian Date of the epoch used to define constellation boundaries. Value - {@value}, for Jan 1.5, 1875. */
  public static final Double J1875 = 2405890.0;
  
  // PRIVATE 
  
  private static List<List<String>> table = null;
  
  /** Read the data.dat file into a simple data structure. */
  private void readSourceDataIntoMemory() {
    table = new ArrayList<List<String>>();
    DataFileReader reader = new DataFileReader();
    String dataFile = DataFileReader.inputFile(Catalog.CONSTELLATION_BOUNDARIES);
    List<String> lines = reader.readFile(dataFile);
    int lineCount = 0;
    for(String line : lines) {
      if (line.startsWith(Consts.COMMENT) || line.trim().length() == 0){
        continue;
      }
      else {
        ++lineCount;
        processLine(line); //don't trim it!
      }
    }
    log("Processed many entries for constellation boundaries: " + lineCount);
  }
  
  /* First line: '  0.0000 24.0000  88.0000 UMi' */
  private void processLine(String line) {
    Parser parser = new Parser();
    List<String> row = new ArrayList<>();
    row.add(parser.slice(line, 2, 7));
    row.add(parser.slice(line, 10, 7));
    row.add(parser.slice(line, 18, 8));
    row.add(parser.slice(line, 27, 3));
    table.add(row);
  }

  /** 
   This implementation closely follows the C program referenced by VizieR.
   Ref: https://cdsarc.cds.unistra.fr/ftp/VI/42/program.c 
   The given angles are in rads.
  */
  private String tableScanAlgorithm(String raJ2000, String decJ2000) {
    String result = "";
    
    //apply precession from J2000 TO 1875
    Precession precession = new Precession(J1875);
    Position pos2000 = new Position(Double.valueOf(raJ2000), Double.valueOf(decJ2000)); //rads 
    Position pos1875 = precession.apply(pos2000); //rads
    Double ra = Maths.radsToHours(pos1875.α); // decimal hours
    Double dec = Maths.radsToDegs(pos1875.δ); // decimal degs
    
    //test without precession
    //ra = Maths.radsToHours(Double.valueOf(raJ2000));
    //dec = Maths.radsToDegs(Double.valueOf(decJ2000));
    
    for(List<String> row : table) {
      Double raL = Double.valueOf(row.get(COL.RAl.col() - 1));
      Double raU = Double.valueOf(row.get(COL.RAu.col() - 1));
      Double decL = Double.valueOf(row.get(COL.Decl.col() - 1));
      String constellation = row.get(COL.Con.col() - 1);
      if (decL > dec) continue;
      if (raU <= ra) continue;
      if (raL > ra) continue;
      if (ra >= raL && ra < raU && decL <= dec) {
        result = constellation;
        break;
      }
      else if (raU < ra) {
        continue;
      }
      else {
        String msg = "Constellation not found. An error occurred.";
        throw new RuntimeException(msg);
      }
    }
    return result;
  }

  /** The columns in the data.dat file.*/
  private enum COL {
    /** RA lower limit, in decimal hours. */
    RAl(1), 
    
    /** RA upper limit, in decimal hours. */
    RAu(2),
    
    /** Declination in decimal degrees. */
    Decl(3),
    
    /** Constellation, as a three-letter abbreviation. */
    Con(4);
    
    int col() { return column; }
    
    private COL(int column){
      this.column = column;
    }
    private int column;
  }
}