package starcat.make.bsc.hipparcos;

import static starcat.util.LogUtil.log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import starcat.star.Bandpass;
import starcat.star.ColorIndex;
import starcat.star.Flag;
import starcat.star.Identifier;
import starcat.util.Util;

/** Simple stats on the generated catalog. Completeness of fields. */
public final class StatsOnGeneratedCatalog {
  
  void showStats(List<GeneratedRecord> records) {
    log("Summary stats for constructed catalog." );
    log(" Num records: " + records.size());
    int ra = 0;
    int dec = 0;
    int parallax = 0;
    int pmRa = 0;
    int pmDec = 0;
    int rv = 0;
    int vmag = 0;
    int var = 0;
    int multiple = 0;
    int spec = 0;
    int color = 0;
    List<String> noColor = new ArrayList<>();
    int hr = 0;
    int properName = 0;
    int brighter = 0;
    int bayerMismatchConstellation = 0;
    int flamsteedMismatchConstellation = 0;
    for(GeneratedRecord r : records) {
      if(Util.isBlank(r.STAR.RA)) ++ra;
      if(Util.isBlank(r.STAR.DEC)) ++dec;
      if(Util.isBlank(r.STAR.PARALLAX)) ++parallax;
      if(Util.isBlank(r.STAR.PROPER_MOTION_RA)) ++pmRa;
      if(Util.isBlank(r.STAR.PROPER_MOTION_DEC)) ++pmDec;
      if(Util.isBlank(r.STAR.RADIAL_VELOCITY)) ++rv;
      if(Util.isBlank(r.STAR.MAGNITUDES.get(Bandpass.V))) ++vmag;
      if(Util.isBlank(r.STAR.SPECTRAL_TYPE)) ++spec;
      if(Util.isBlank(r.STAR.COLOR_INDICES.get(ColorIndex.B_MINUS_V))) {
        ++color;
        noColor.add(r.STAR.IDENTIFIERS.get(Identifier.HIP));
      }
      if(Util.isBlank(r.STAR.IDENTIFIERS.get(Identifier.HR))) ++hr;
      if(Util.isPresent(r.STAR.FLAGS.get(Flag.VARIABILITY))) ++var;
      if(Util.isPresent(r.STAR.FLAGS.get(Flag.MULTIPLICITY))) ++multiple;
      if(Util.isPresent(r.STAR.IDENTIFIERS.get(Identifier.PROPER_NAME))) ++properName;
      double v = Double.valueOf(r.STAR.MAGNITUDES.get(Bandpass.V));
      if (v < 3.0) ++brighter;
      
      if(Util.isPresent(r.STAR.IDENTIFIERS.get(Identifier.BAYER))) {
        String bayer = r.STAR.IDENTIFIERS.get(Identifier.BAYER);
        String bayerConstell = bayer.split(" ")[1];
        String constell = r.STAR.IDENTIFIERS.get(Identifier.CONSTELLATION);
        if (!constell.equals(bayerConstell)) {
          ++bayerMismatchConstellation;
        }
      }
      if(Util.isPresent(r.STAR.IDENTIFIERS.get(Identifier.FLAMSTEED))) {
        String flamsteed = r.STAR.IDENTIFIERS.get(Identifier.FLAMSTEED);
        String flamsteedConstell = flamsteed.split(" ")[1];
        String constell = r.STAR.IDENTIFIERS.get(Identifier.CONSTELLATION);
        if (!constell.equals(flamsteedConstell)) {
          ++flamsteedMismatchConstellation;
        }
      }
    }
    log(" No RA: " + ra);
    log(" No DEC: " + dec);
    log(" No Parallax: " + parallax);
    log(" No pmRa: " + pmRa);
    log(" No pmDec: " + pmDec);
    log(" No rv: " + rv);
    log(" No Vmag: " + vmag);
    log(" No spectral type: " + spec);
    log(" No B-V: " + color + " " + noColor);
    log(" No HR: " + hr);
    log(" Mismatch of Bayer Id vs. constellation : " + bayerMismatchConstellation);
    log(" Mismatch of Flamsteed Id vs. constellation : " + flamsteedMismatchConstellation);
    log(" With variable flag: " + var);
    log(" With multiplicity flag: " + multiple);
    log(" With proper name: " + properName);
    log(" With Vmag < 3.0: " + brighter);
    repeatedIdentifiers(records);
  }
  
  private void repeatedIdentifiers(List<GeneratedRecord> records) {
    log(" Search for repeated identifiers.");
    repeatedIdentifier(Identifier.HIP, records);
    repeatedIdentifier(Identifier.HD, records);
    repeatedIdentifier(Identifier.HR, records);
    repeatedIdentifier(Identifier.BAYER, records);
    repeatedIdentifier(Identifier.FLAMSTEED, records);
  }
  
  private void repeatedIdentifier(Identifier ident, List<GeneratedRecord> records) {
    List<String> all = new ArrayList<>();
    Set<String> uniques = new LinkedHashSet<>();
    for(GeneratedRecord r : records) {
      String id = r.STAR.IDENTIFIERS.get(ident);
      if (Util.isPresent(id)) {
        all.add(id);
        uniques.add(id);
      }
    }
    log("  " + ident + ": num present = " + all.size() + ", num unique = " + uniques.size());
    
    if (all.size() > uniques.size()) {
      Set<String> uniq = new LinkedHashSet<>();
      List<String> repeated = new ArrayList<>();
      for(String id : all) {
        boolean isRepeated = !uniq.add(id);
        if (isRepeated) {
          repeated.add(id);
        }
      }
      log("   Repeats: " + repeated);
    }
  }
}
