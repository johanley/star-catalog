package starcat.make.bsc.hipparcos;

import static starcat.util.LogUtil.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import starcat.catalogs.BrightStarCatalog;
import starcat.catalogs.Constellation;
import starcat.catalogs.GeneralCatalogOfMeanRV;
import starcat.catalogs.Hipparcos2Catalog;
import starcat.catalogs.HipparcosCatalog;
import starcat.catalogs.PulkovoRVCatalog;
import starcat.catalogs.StarName;
import starcat.make.bsc.hipparcos.ClosePairs.CloseDouble;
import starcat.make.bsc.hipparcos.Provenance.Source;
import starcat.star.Bandpass;
import starcat.star.Catalog;
import starcat.star.ColorIndex;
import starcat.star.Error;
import starcat.star.Flag;
import starcat.star.Identifier;
import starcat.star.Star;
import starcat.star.StarTool;
import starcat.util.Consts;
import starcat.util.DataFileReader;
import starcat.util.Util;
import starcat.variablestar.VariableStarFilterIn;

/**
 Make a bright star catalog based on Hipparcos-2 and Hipparcos-1, supplemented by data from other sources.
 
 The coordinate system used by Hipparcos (and retained by the catalog generated by this class) is the 
 <a href='https://arxiv.org/abs/astro-ph/0602086'>International Celestial Reference System</a> (ICRS), 
 the IAU replacement for FK5.
 The ICRS uses the barycenter of the solar system as its origin, and its axes have fixed directions. 
 The ICRS is implemented by adopting coordinates for extra-galactic radio sources. 
 At optical wavelengths (less precise than radio), the ICRS is implemented by adopting positions for stars. 
 Since the ICRS is a non-rotating system of reference, it has no associated epoch.
  
 <P>The ICRS coordinates are implemented such that they are very close to, but not exactly the same as, J2000 equatorial coordinates. 
 The difference is <a href='https://arxiv.org/abs/astro-ph/0602086'>on the order of 0.02 arcseconds</a>.
 Unless you need extreme precision, ICRS coordinates can be treated as J2000 equatorial coordinates.
 (See Section 3.5 of <a href='https://arxiv.org/abs/astro-ph/0602086'>this reference</a> for the conversion algorithm
 between ICRS(or ICRF if you prefer) and the FK5 system at J2000.)
 
  <P>From the USNO document: <em>"Because of its consistency with previous reference systems, implementation of the ICRS will be
 transparent to any applications with accuracy requirements of no better than 0.1 arcseconds near
 epoch J2000.0. That is, for applications of this accuracy, the distinctions between the ICRS, FK5,
 and dynamical equator and equinox of J2000.0 are not significant." </em>   
 
 <P>For proper motions, the epoch of the positions is J1991.25 = JD2448349.0625 (TT), not J2000.
 This is near the mean date of Hipparcos observations.
 
 <P>Close optical pairs are not amalgamated here, but a supplementary file of close doubles is generated.
 The brightest case of this is Alpha Centauri, whose A and B components are separate entries here (HIP = 71683 and 71681, respectively).
*/
public final class HipparcosBSC {

  /** Create a text file with the catalog data. */
  public void writeCatalogFile(String fileName, boolean sortByMag) throws IOException {
    String outputFile = DataFileReader.outputFile(Catalog.OS_BSC_HIPPARCOS);
    log("Creating/updating file: " + outputFile);
    List<GeneratedRecord> records = make();
    if (sortByMag) {
      log("Sorting by magnitude.");
      sortByMagnitude(records);
    }
    else {
      log("Sorting by HIP.");
      sortByHip(records);
    }
    
    writeClosePairsFile(records);
    
    List<String> lines = new ArrayList<>();
    for (GeneratedRecord r : records) {
      lines.add(formatted(r.STAR, r.PROVENANCE));
    }
    writeTextFile(outputFile, lines);
    log("Catalog file is now written: " + outputFile);
  }

  /** Return the list of entries in the catalog, but don't format it or save to a file. */
  public List<GeneratedRecord> make() {
    log("Making a bright star catalog based on Hipparcos-2. Magnitude limit " + Consts.MAGNITUDE_LIMIT);
    log("  Num Hipparcos records with Vmag <= " + Consts.MAGNITUDE_LIMIT +": " + brightStars.size());
    //commonality();
    initUsingHipparcosBrightStars();
    addVariableStarsStraddlingTheLimitingMag();
    astrometry();
    radialVelocity();
    identifiers();
    properNames();
    backfillMissingDataFromSimbad();
    provenanceForIdentifiers();
    provenanceForSpectraEtc();
    constellation();
    showStats();
    return records;
  }
  
  /**
   Format a single record in the catalog.
   Use a fixed-width for each field, to match the style seen with most astronomical catalogs.
   Put an empty space between fields.
   Some calculated fields are included as well. 
  */
  String formatted(Star star, Provenance provenance) {
    StringBuilder builder = new StringBuilder();
    
    //1: hipparcos identifier
    builder.append(field(star.IDENTIFIERS.get(Identifier.HIP), 6));
    
    //2-3: calculated fields
    builder.append(field(SexagesimalFormat.radsToTimeString(star.RA), 17)); 
    builder.append(field(SexagesimalFormat.radsToDegreeString(star.DEC), 17)); 
    
    //4-9: core astrometry
    builder.append(field(star.RA, 13)); 
    builder.append(field(star.DEC, 14)); 
    builder.append(field(star.PARALLAX, 7)); 
    builder.append(field(star.PROPER_MOTION_RA, 8)); 
    builder.append(field(star.PROPER_MOTION_DEC, 8)); 
    builder.append(field(star.RADIAL_VELOCITY, 7));
    
    //10-15: errors on core astrometry
    builder.append(field(star.ERRORS.get(Error.RA), 6)); 
    builder.append(field(star.ERRORS.get(Error.DEC), 6)); 
    builder.append(field(star.ERRORS.get(Error.PARALLAX), 6)); 
    builder.append(field(star.ERRORS.get(Error.PROPER_MOTION_RA), 6)); 
    builder.append(field(star.ERRORS.get(Error.PROPER_MOTION_DEC), 6)); 
    builder.append(field(star.ERRORS.get(Error.RADIAL_VELOCITY), 5)); 
    
    //16-20: flags
    builder.append(field(star.MAGNITUDES.get(Bandpass.V), 5)); 
    builder.append(field(star.FLAGS.get(Flag.VARIABILITY), 1)); 
    builder.append(field(star.SPECTRAL_TYPE, 12)); 
    builder.append(field(star.COLOR_INDICES.get(ColorIndex.B_MINUS_V), 6)); 
    builder.append(field(star.FLAGS.get(Flag.MULTIPLICITY), 1));
    
    //21-27: identifiers
    builder.append(field(star.IDENTIFIERS.get(Identifier.CCDM), 10)); 
    builder.append(field(star.IDENTIFIERS.get(Identifier.HD), 6)); 
    builder.append(field(star.IDENTIFIERS.get(Identifier.HR), 4)); 
    builder.append(field(star.IDENTIFIERS.get(Identifier.BAYER), 7));
    builder.append(field(star.IDENTIFIERS.get(Identifier.FLAMSTEED), 7)); 
    builder.append(field(star.IDENTIFIERS.get(Identifier.PROPER_NAME), maxLengthProperName));
    builder.append(field(star.IDENTIFIERS.get(Identifier.CONSTELLATION), 3));

    //28: provenance string for all fields (except for the provenance itself, of course)
    builder.append(field(provenance.toString(), 27));
    
    return builder.toString(); 
  }

  /**
   Use improved 2007 astrometry from Hipparcos-2, by default.
   In 2007 the core astrometry for Hipparcos was re-computed with an improved algorithm.
   I don't use Hipparcos-2 as the starting point because the original Hipparcos has more fields, and 
   makes a better base. 
  */
  void astrometry() {
    log("Use the improved Hipparcos-2 astrometry whenever possible.");
    Map<String, Star> table = makeLookupTableWith(Identifier.HIP, hip2);
    for(GeneratedRecord r : records) {
      String hip = r.STAR.IDENTIFIERS.get(Identifier.HIP);
      Optional<Star> hip2Star = lookUp(hip, table, Identifier.HIP);
      if (hip2Star.isPresent()) {
        r.STAR.RA = hip2Star.get().RA;
        r.STAR.DEC = hip2Star.get().DEC;
        r.STAR.PROPER_MOTION_RA = hip2Star.get().PROPER_MOTION_RA;
        r.STAR.PROPER_MOTION_DEC = hip2Star.get().PROPER_MOTION_DEC;
        r.STAR.PARALLAX = hip2Star.get().PARALLAX;
        updateUncertainty(Error.RA, r, hip2Star);
        updateUncertainty(Error.DEC, r, hip2Star);
        updateUncertainty(Error.PARALLAX, r, hip2Star);
        updateUncertainty(Error.PROPER_MOTION_RA, r, hip2Star);
        updateUncertainty(Error.PROPER_MOTION_DEC, r, hip2Star);
        r.PROVENANCE.put(Provenance.Source.Hipparcos2, Provenance.ASTROMETRY_NO_RV);
      }
      else {
        log("Hipparcos-2 astrometry not present for "  + hip);
        log(" Converting sexagesimal ra-dec from Hipparcos-1 to rads for " + hip + ", to match the units of Hipparcos-2.");
        r.STAR.RA = SexagesimalFormat.radsFromHourMinSec(r.STAR.RA);
        r.STAR.DEC = SexagesimalFormat.radsFromDegMinSec(r.STAR.DEC);
        r.PROVENANCE.put(Provenance.Source.Hipparcos1, Provenance.ASTROMETRY_NO_RV);
      }
    }
  }
  
  /**
   Add radial velocity, which is missing entirely from Hipparcos fields.
   Use Pulkovo as primary, and BF as secondary.
   When BF is needed, note that its format is a bit odd, and it's changed here to match the format used by the Pulkovo catalog.
   Note also that Pulkovo uses HIP as its index, while BF does not; moreover, BF can have N entries for one HIP!
  */
  void radialVelocity() {
    log("Supplement Hipparcos with radial velocities. Primary source: Pulkovo  Secondary source: BF");
    Identifier ID = Identifier.HIP;
    
    Map<String/*hip*/, String/*rv*/> primary = rvTable(pulkovo, ID);
    Map<String/*hip*/, String/*rvError*/> primaryErr = rvErrorTable(pulkovo, ID);
    
    Map<String/*hip*/, String/*rv*/> secondary = rvTableWithoutIndexedHIP(bf);
    Map<String/*hip*/, String/*rvError*/> secondaryErr = rvErrorTable(bf, ID);
    
    List<String> notFound = new ArrayList<>();

    int countNoRVFoundPulk = 0;
    int countNoRVFoundBF = 0;
    for(GeneratedRecord record : records) {
      //first choice 
      String hip = record.STAR.IDENTIFIERS.get(ID);
      String rv = primary.get(hip);
      if (Util.isPresent(rv)) {
        record.STAR.RADIAL_VELOCITY = rv;
        record.PROVENANCE.put(9, Provenance.Source.Pulkovo);
        String rvError = primaryErr.get(hip);
        if (Util.isPresent(rvError)) {
          record.STAR.ERRORS.put(Error.RADIAL_VELOCITY, rvError);
          record.PROVENANCE.put(15, Provenance.Source.Pulkovo);
        }
      }
      else {
        ++countNoRVFoundPulk;
        //second choice 
        String hd = record.STAR.IDENTIFIERS.get(Identifier.HD);
        String key = hip + "_" + hd;
        rv = secondary.get(key);
        if (Util.isPresent(rv)) {
          record.STAR.RADIAL_VELOCITY = rv;
          record.PROVENANCE.put(9, Provenance.Source.BF);
          String rvError = secondaryErr.get(hip);
          if (Util.isPresent(rvError)) {
            record.STAR.ERRORS.put(Error.RADIAL_VELOCITY, rvError);
            record.PROVENANCE.put(15, Provenance.Source.BF);
          }
        }
        else {
          ++countNoRVFoundBF;
          notFound.add(hip);
        }
      }
    }
    log("  No RV found in primary RV catalog: " + countNoRVFoundPulk);
    log("  No RV found in secondary RV catalog: " + countNoRVFoundBF);
    log("  No RV found (" + notFound.size() + "):" + notFound);
  }
  
  /** 
   Supplement with the Bayer, Flamsteed, and HR designations, when known.
   Use the HD identifier as an intermediate. 
   This implementation uses data from the Yale Bright Star Catalog.
   Note that HD is missing from 14 records in the Yale Bright Star Catalog, and no HD identifiers are repeated.
   (If the HD was repeated, then it wouldn't be a robust cross-matching tool.)
  */
  void identifiers() {
    log("Adding designations, using the Yale BSC: " + Identifier.BAYER + ", " + Identifier.FLAMSTEED + ", "+ Identifier.HR);
    log(" The HD identifier (Henry Draper) is used as an intermediate between Hipparcos and the Yale BSC.");
    addDesignationFromBSC(Identifier.BAYER);
    addDesignationFromBSC(Identifier.FLAMSTEED);
    addDesignationFromBSC(Identifier.HR);
  }
  
  /** Add names such as 'Sirius', 'Aldebaran', and so on, for a few selected bright stars.  */
  void properNames() {
    // this implementation uses the Bayer designation from the Yale Bright Star catalog as the lookup key.
    int count = 0;
    String longest = "";
    StarName starName = new StarName();
    for(GeneratedRecord record : records) {
      String bayer = record.STAR.IDENTIFIERS.get(Identifier.BAYER);
      if (Util.isPresent(bayer)){
        String[] parts = bayer.split(" ");
        String properName = starName.nameFor(parts[0], parts[1]);
        if (Util.isPresent(properName)) {
          record.STAR.IDENTIFIERS.put(Identifier.PROPER_NAME, properName);
          ++count;
          int length = properName.length();
          if (length > maxLengthProperName) {
            maxLengthProperName = length;
            longest = properName;
          }
        }
      }
    }
    log("Added this many proper names to stars: " + count + " Max length: " + maxLengthProperName + " " + longest);
  }
  
  /** Sort the lines by the Vmag value. */
  void sortByMagnitude(List<GeneratedRecord> records) {
    Comparator<? super GeneratedRecord> c = new Comparator<GeneratedRecord>() {
      @Override public int compare(GeneratedRecord a, GeneratedRecord b) {
        int result = 0;
        String aMag = a.STAR.MAGNITUDES.get(Bandpass.V);
        String bMag = b.STAR.MAGNITUDES.get(Bandpass.V);
        if (Util.isPresent(aMag) && Util.isPresent(bMag)) {
          Double aVal = Double.valueOf(aMag);
          Double bVal = Double.valueOf(bMag);
          result = Double.compare(aVal, bVal);
        }
        return result;
      }
    };
    Collections.sort(records, c);
  }
  
  void sortByHip(List<GeneratedRecord> records) {
    Comparator<? super GeneratedRecord> c = new Comparator<GeneratedRecord>() {
      @Override public int compare(GeneratedRecord a, GeneratedRecord b) {
        int result = 0;
        String aHip = a.STAR.IDENTIFIERS.get(Identifier.HIP);
        String bHip = b.STAR.IDENTIFIERS.get(Identifier.HIP);
        if (Util.isPresent(aHip) && Util.isPresent(bHip)) {
          Double aVal = Double.valueOf(aHip);
          Double bVal = Double.valueOf(bHip);
          result = Double.compare(aVal, bVal);
        }
        return result;
      }
    };
    Collections.sort(records, c);
  }
  
  /** Search and report on stars that are close to each other. */
  void writeClosePairsFile(List<GeneratedRecord> records) throws IOException {
    log("Searching for close pairs of stars within " + Consts.CLOSE_PAIR_SEPARATION_LIMIT + " arcseconds of each other.");
    ClosePairs pairs = new ClosePairs();
    List<CloseDouble> closePairs = pairs.findClosePairs(Consts.CLOSE_PAIR_SEPARATION_LIMIT, records);
    log("Number of close pairs within " + Consts.CLOSE_PAIR_SEPARATION_LIMIT + " arcseconds of each other: " + closePairs.size());
    
    String outputFilePairs = DataFileReader.outputFile(Catalog.OS_BSC_HIPPARCOS, "close-pairs.utf8");
    List<String> lines = new ArrayList<>();
    for (CloseDouble closePair : closePairs) {
      lines.add(formatted(closePair));
    }
    writeTextFile(outputFilePairs, lines);
    log("File of close pairs is now written: " + outputFilePairs);
  }
  
  // PRIVATE
  
  private List<GeneratedRecord> records = new ArrayList<>();
  private List<Star> brightStars = StarTool.filterByMagLTEQ(Consts.MAGNITUDE_LIMIT, Bandpass.V, new HipparcosCatalog().records());
  private List<Star> pulkovo = new PulkovoRVCatalog().records();
  private List<Star> bf = new GeneralCatalogOfMeanRV().records();
  private List<Star> bsc = new BrightStarCatalog().records();
  private List<Star> hip2 = new Hipparcos2Catalog().records();
  
  /** Used to determine the width of a field. */
  private int maxLengthProperName;

  /** 
   Detect the number of items that are not in common between pairs of catalogs.
   Run this as a developer tool, not when the catalog is actually generated. 
  */
  private void commonality() {
    List<Star> bscFiltered = StarTool.filterByMagLTEQ(Consts.MAGNITUDE_LIMIT, Bandpass.V, bsc);
    StarTool.logIdsNotInCommon(bscFiltered, brightStars, Identifier.HD, "BSC mag <=" + Consts.MAGNITUDE_LIMIT, "Hipparcos mag <= " + Consts.MAGNITUDE_LIMIT);
    /*
    StarTool.logIdsNotInCommon(bsc, new HipparcosCatalog().records(), Identifier.HD, "BSC", "Hipparcos (full)");
    StarTool.logIdsNotInCommon(bsc, brightStars, Identifier.HD, "BSC", "Hipparcos mag <= " + Consts.MAGNITUDE_LIMIT);
    StarTool.logIdsNotInCommon(pulkovo, brightStars, Identifier.HIP, "Pulkovo", "Hipparcos mag <= " + Consts.MAGNITUDE_LIMIT);
    StarTool.logIdsNotInCommon(bf, brightStars, Identifier.HIP, "BF", "Hipparcos mag <= " + Consts.MAGNITUDE_LIMIT);
    StarTool.logIdsNotInCommon(hip2, brightStars, Identifier.HIP, "HIP-2", "Hipparcos mag <= " + Consts.MAGNITUDE_LIMIT);
    */
    /*
    Set<String> ids = StarTool.onlyInFirst(bsc, brightStars, Identifier.HD);
    for(String id : ids) {
      log(id);
    }
    */
  }
  
  private void initUsingHipparcosBrightStars() {
    for(Star star : brightStars) {
      GeneratedRecord record = recordFor(star);
      records.add(record);
    }
  }
  
  private void addVariableStarsStraddlingTheLimitingMag() {
    VariableStarFilterIn variableStars = new VariableStarFilterIn();
    List<Star> dimVariableStars = variableStars.annexCandidatesForInclusion(Consts.MAGNITUDE_LIMIT);
    for(Star dimVariableStar : dimVariableStars) {
      GeneratedRecord record = recordFor(dimVariableStar);
      record.PROVENANCE.put(Source.HipparcosVariabilityAnnex, 16);
      if (!containsHipAlready(dimVariableStar.IDENTIFIERS.get(Identifier.HIP))) {
        records.add(record);
      }
    }
    log("  Num Hipparcos records with Vmag <= " + Consts.MAGNITUDE_LIMIT +", after adding variable stars which straddle the mag limit: " + records.size());
  }
  
  private boolean containsHipAlready(String hip) {
    boolean result = false;
    for(GeneratedRecord record : records) {
      if (record.STAR.IDENTIFIERS.get(Identifier.HIP).equals(hip)) {
        log("UNEXPECTED: duplicate hip " + hip);
        result = true;
        break;
      }
    }
    return result;
  }
  
  private GeneratedRecord recordFor(Star s) {
    GeneratedRecord record = new GeneratedRecord();
    record.STAR = s;
    record.PROVENANCE = new Provenance(Provenance.NUM_FIELDS_EXCLUDING_PROVENANCE);
    record.PROVENANCE.put(Source.Hipparcos1, 1);
    record.PROVENANCE.put(Source.Calculated, 2);
    record.PROVENANCE.put(Source.Calculated, 3);
    return record;
  }
  
  /** 
   Left-pads if needed, to the given fixed width. 
   Padding will often be present in the data to begin with. 
   Appends a trailing space as a field separator. 
  */
  private String field(String data, int fixedWidth) {
    if (data == null) {
      data = "";
    }
    String sep = " ";
    String paddedText = String.format("%1$" + fixedWidth + "s", data);
    return paddedText + sep;
  }
 
  private Optional<Star> lookUp(String hip, Map<String, Star> table, Identifier id) {
    Optional<Star> result = Optional.empty();
    Star star = table.get(hip);
    if (star != null) {
      result = Optional.of(star);
    }
    return result;
  }
  
  private Map<String, Star> makeLookupTableWith(Identifier id, List<Star> stars){
    Map<String, Star> result = new LinkedHashMap<>();
    for(Star star : stars) {
      String ident = star.IDENTIFIERS.get(id);
      result.put(ident, star);
    }
    return result;
  }
  
  private Map<String/*id*/, String/*rv*/> rvTable(List<Star> stars, Identifier id) {
    Map<String/*id*/, String/*rv*/> result = new LinkedHashMap<>();
    for(Star s : stars) {
      result.put(s.IDENTIFIERS.get(id), s.RADIAL_VELOCITY);
    }
    return result;
  }
  
  /**
   The Pulkovo table uses HIP as its index. 
   The BF table doesn't, AND it can have N entries for a single HIP.
   In addition, BF has many missing HIPs.
   For BF, I will simply reject for the case of N>1 entries with the same HIP+HD, because I don't know what else to do.
  */
  private Map<String/*hip_hd*/, String/*rv*/> rvTableWithoutIndexedHIP(List<Star> stars) {
    Map<String, String> result = new LinkedHashMap<>();
    List<String> multiples = new ArrayList<>();
    for(Star s : stars) {
      String hip = s.IDENTIFIERS.get(Identifier.HIP);
      String hd = s.IDENTIFIERS.get(Identifier.HD);
      String key = hip + "_" + hd;
      if (Util.isPresent(hip) && Util.isPresent(hd)) {
        if (result.containsKey(key)) {
          //if there's a second entry for the same hip, reject it later
          multiples.add(key);
        }
        else {
          //the first always gets in 
          result.put(key, s.RADIAL_VELOCITY);
        }
      }
    }
    log("Found this many HIP_HDs that occur more than once in BF (which won't be used here): " + multiples.size());
    for(String reject : multiples) {
      //log(reject);
      result.remove(reject);
    }
    return result;
  }
  
  private Map<String/*id*/, String/*rvError*/> rvErrorTable(List<Star> stars, Identifier id) {
    Map<String/*id*/, String/*rv*/> result = new LinkedHashMap<>();
    for(Star s : stars) {
      String rvError = s.ERRORS.get(Error.RADIAL_VELOCITY);
      if (Util.isPresent(rvError)) {
        result.put(s.IDENTIFIERS.get(id), rvError);
      }
    }
    return result;
  }
 
  /** Uses the HD designation as intermediate. */
  private void addDesignationFromBSC(Identifier ident) {
    Map<String/*hd*/, String/*ident*/> table = new LinkedHashMap<>();
    for(Star s : bsc) {
      String hd = s.IDENTIFIERS.get(Identifier.HD);
      if (Util.isPresent(hd)) {
        String id = s.IDENTIFIERS.get(ident);
        if (Util.isPresent(id)) {
          table.put(hd, id);
        }
      }
    }
    //log(" Found " + table.keySet().size() + " HD+" + ident + " designations in BSC.");

    int success = 0;
    for(GeneratedRecord record : records) {
      String hd = record.STAR.IDENTIFIERS.get(Identifier.HD); //may be null
      if (hd != null) {
        String id = table.get(hd);
        if (id != null) {
          record.STAR.IDENTIFIERS.put(ident, id);
          ++success; 
        }
      }
    }
    log(" Number of " + ident + " designations found in the Yale BSC: " + success);
  }
  
  /** 
   A few pieces of key data are missing. Fill in the gaps by looking up data in SIMBAD.
   SIMBAD isn't meant to be used as a catalog, but for so few entries I think it's acceptable. 
  */
  private void backfillMissingDataFromSimbad() {
    ///be careful with items with larger than normal precision: they can exceed the normal field length
    backFillparallaxProperMotion(55203, 113.2, -453.7, -591.4,  4.6, 2.0, 2.0);
    backFillparallaxProperMotion(78727, 43.0, -71.5, -34.5, 4.2, 2.5, 2.5);
    backFillparallaxProperMotion(115125, 44.5698, 306.661, -104.798, 0.0293, 0.030, 0.024);
    
    //No RV found (16):[20070, 26063, 26727, 28614, 28691, 32810, 40834, 45585, 55203, 71681, 77990, 84709, 90441, 104371, 104887, 115125]
    
    ///be careful with items with larger than normal precision! they can exceed the normal field length
    backFillRadialVelocity(20070, 19.8, 0.9);
    backFillRadialVelocity(26063, 22.2, 2.0);
    backFillRadialVelocity(26727, 18.50,  1.3);
    backFillRadialVelocity(28614, 0.00, 0.03); // valid?
    backFillRadialVelocity(28691, 12.61, 0.05);
    backFillRadialVelocity(32810, 13.00, 8.7);
    backFillRadialVelocity(40834, -6.50, null);
    backFillRadialVelocity(45585,  11.20, null);
    backFillRadialVelocity(55203, -18.2, 2.7);
    backFillRadialVelocity(71681, -22.586, 0.0); //0.0001 gives a E-4 in the result; coerce to 0.0
    backFillRadialVelocity(77990,  -13.23, 42.64); //the error is very high here!
    backFillRadialVelocity(84709, 0.00, 3.7); //valid?
    backFillRadialVelocity(90441, -23.3, 0.9);
    backFillRadialVelocity(104371, -25.8, 2.0);
    backFillRadialVelocity(104887, -20.90, 0.8);
    backFillRadialVelocity(115125, 10.89, 0.18);
    //backFillRadialVelocity(110478, ?, ?); //I can't find RV data for pi-1 Gru! 
  }
  
  private void backFillparallaxProperMotion(Integer hip, Double parallax, Double pmRA, Double pmDec, Double e_parallax, Double e_pmRA, Double e_pmDEC) {
    for(GeneratedRecord r : records) {
      if (r.STAR.IDENTIFIERS.get(Identifier.HIP).equals(hip.toString())) {
        r.STAR.PARALLAX = parallax.toString();
        r.STAR.PROPER_MOTION_RA = pmRA.toString();
        r.STAR.PROPER_MOTION_DEC = pmDec.toString();
        r.STAR.ERRORS.put(Error.PARALLAX, e_parallax.toString());
        r.STAR.ERRORS.put(Error.PROPER_MOTION_RA, e_pmRA.toString());
        r.STAR.ERRORS.put(Error.PROPER_MOTION_DEC, e_pmDEC.toString());
        r.PROVENANCE.put(Source.Simbad, 6,7,8, 12,13,14);
        log("SIMBAD backfill of parallax, pm, and errors for: " + hip);
        break;
      }
    }
  }
  
  private void backFillRadialVelocity(Integer hip, Double rv, Double e_rv) {
    for(GeneratedRecord r : records) {
      if (r.STAR.IDENTIFIERS.get(Identifier.HIP).equals(hip.toString())) {
        r.STAR.RADIAL_VELOCITY = rv.toString();
        r.PROVENANCE.put(9, Source.Simbad);
        if (e_rv != null) {
          r.PROVENANCE.put(15, Source.Simbad);
          r.STAR.ERRORS.put(Error.RADIAL_VELOCITY, e_rv.toString());
        }
        log("SIMBAD backfill of radial velocity for: " + hip);
        break;
      }
    }
  }
  
  private void constellation() {
    log("Calculating constellation, corresponding to position as of 1875.0 (Delporte). ");
    Constellation constellation = new Constellation();
    for(GeneratedRecord r : records) {
      String ra = r.STAR.RA;
      String dec = r.STAR.DEC;
      String constell = constellation.constellationFor(ra, dec);
      if (Util.isBlank(constell)) {
        log("No constellation found for HIP " + r.STAR.IDENTIFIERS.get(Identifier.HIP));
      }
      r.STAR.IDENTIFIERS.put(Identifier.CONSTELLATION, constell);
      r.PROVENANCE.put(27, Source.Calculated); 
    }
  }
  
  private void provenanceForIdentifiers() {
    for(GeneratedRecord r : records) {
      idProvenance(Identifier.CCDM, Source.Hipparcos1, 21, r);
      idProvenance(Identifier.HD, Source.Hipparcos1, 22, r);
      idProvenance(Identifier.HR, Source.BrightStarCatalog, 23, r);
      idProvenance(Identifier.BAYER, Source.BrightStarCatalog, 24, r);
      idProvenance(Identifier.FLAMSTEED, Source.BrightStarCatalog, 25, r);
      idProvenance(Identifier.PROPER_NAME, Source.CustomData, 26, r);
    }
  }
  private void idProvenance(Identifier ident, Source source, int field, GeneratedRecord r) {
    if (Util.isPresent(r.STAR.IDENTIFIERS.get(ident))) {
      r.PROVENANCE.put(field, source);     
    }
  }
  
  private void provenanceForSpectraEtc() {
    for(GeneratedRecord r : records) {
      if (!Source.HipparcosVariabilityAnnex.equals(r.PROVENANCE.get(16))) {
        //don't overwrite if the mag comes from the variability annex
        idSpectraEtc(r.STAR.MAGNITUDES.get(Bandpass.V), Source.Hipparcos1, 16, r);
      }
      idSpectraEtc(r.STAR.FLAGS.get(Flag.VARIABILITY), Source.Hipparcos1, 17, r);
      idSpectraEtc(r.STAR.SPECTRAL_TYPE, Source.Hipparcos1, 18, r);
      idSpectraEtc(r.STAR.COLOR_INDICES.get(ColorIndex.B_MINUS_V), Source.Hipparcos1, 19, r);
      idSpectraEtc(r.STAR.FLAGS.get(Flag.MULTIPLICITY), Source.Hipparcos1, 20, r);
    }
  }
  private void idSpectraEtc(String data, Source source, int field, GeneratedRecord r) {
    if (Util.isPresent(data)) {
      r.PROVENANCE.put(field, source);     
    }
  }
  
  private void writeTextFile(String fileName, List<String> lines) throws IOException {
    Path path = Paths.get(fileName);
    Set<Integer> lineLengths = new LinkedHashSet<>();
    try (BufferedWriter writer = Files.newBufferedWriter(path, Consts.ENCODING)){
      for(String line : lines){
        lineLengths.add(line.length());
        if (line.length() > 263) {
          log("Too long: " + line);
        }
        writer.write(line);
        writer.newLine();
      }
    }
    log("Line lengths: " + lineLengths);
  }
  
  private void updateUncertainty(Error uncertainty, GeneratedRecord r, Optional<Star> hip2Star) {
    r.STAR.ERRORS.put(uncertainty, hip2Star.get().ERRORS.get(uncertainty));
  }
  
  private void showStats() {
    StatsOnGeneratedCatalog stats = new StatsOnGeneratedCatalog();
    stats.showStats(records);
  }
  
  private String dblTwoDecimals(Double val) {
    DecimalFormat fmt = new DecimalFormat("#0.00");
    return fmt.format(val);
  }
  
  private String formatted(CloseDouble closePair) {
    StringBuilder builder = new StringBuilder();
    
    builder.append(field(dblTwoDecimals(closePair.arcseconds()), 6));
    builder.append(field(dblTwoDecimals(closePair.VmagCombined), 5));
    
    builder.append(field(closePair.hipA, 6));
    builder.append(field(closePair.nameA, 7));
    builder.append(field(closePair.VmagA, 5));
    
    builder.append(field(closePair.hipB, 6));
    builder.append(field(closePair.nameB, 7));
    builder.append(field(closePair.VmagB, 5));
    
    return builder.toString(); 
  }
}