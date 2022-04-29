package starcat.variablestar;

import static starcat.util.LogUtil.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import starcat.catalogs.HipparcosCatalog;
import starcat.catalogs.HipparcosVariabilityAnnex;
import starcat.star.Bandpass;
import starcat.star.Flag;
import starcat.star.Identifier;
import starcat.star.Star;
import starcat.util.Consts;
import starcat.util.Util;

/**
 Find variable stars that should be included in the catalog.
 
 The problem addressed here is exemplified by the star Mira (omicron Ceti).
 Its Vmag in hip_main.dat is dimmer than the limiting mag of the catalog, so it is filtered-out by default.
 But Mira should really be included, since much of the time it is indeed brighter than the limiting magnitude of the catalog.
*/
public final class VariableStarFilterIn {
  
  /**
   The variable stars that are candidates for inclusion in the new catalog generated by this project.
   
   Criteria: 
   <ul>
    <li>band V is present
    <li>the brightness range is present (min..max), and brackets (straddles) the given limiting magnitude
    <li>the variable-type is not in the {@link #NEGLECTED_VARIABLE_TYPES} (to avoid novas and similar)
   </ul> 
   These criteria are against the 'literature' fields in the Hipparcos variability annex, not the Hipparcos core data.
   
   <P>The returned {@link Star} object has its magnitude taken from {@link VariableStar#MAXIMUM}.
   That magnitude will be within the given magnitude limit for the catalog.
   The caller will need to explicitly add the returned objects to the generated catalog.
   The caller must also note the provenance of the Vmag field, as coming from the Hipparcos variability annex.
  */
  public List<Star> annexCandidatesForInclusion(Double limitingMagnitude){
    log("Scanning for variables that straddle the limiting mag " + limitingMagnitude);
    log("These variable types will be filtered out (not included): " + NEGLECTED_VARIABLE_TYPES);
    List<Star> result = new ArrayList<>();
    List<Star> mainStarsWithVariability = mainWithVariabilityFlagAndDimmerThan(limitingMagnitude);
    Map<String, VariableStar> annexData = annexVariableStarData();
    for(Star star : mainStarsWithVariability) {
      String hip = star.IDENTIFIERS.get(Identifier.HIP);
      VariableStar annex = annexData.get(hip);
      if (annex != null) {
        if (passesCriteria(annex, limitingMagnitude)){
          // alters the state of the star object, to take its Vmag from the max brightness found in the annex
          star.MAGNITUDES.put(Bandpass.V, annex.MAXIMUM);
          result.add(star);
        }
      }
    }
    log("Number of variables that straddle the magnitude limit: " + result.size());
    return result;
  }
  
  /**
   References a field in the variability annex (P5) which states the variable type. 
   This is used to avoid including cataclysmic and eruptive variables such as the dim variable T CrB, a recurrent nova having rare outbursts. 
   These kinds of items are usually dim. It's reasonable to exclude them from a bright star catalog.
   Value - {@value}.
   Reference: <a href='https://www.cosmos.esa.int/documents/532822/552851/vol1_all.pdf/99adf6e3-6893-4824-8fc2-8d3c9cbba2b5'>link</a>.
  */
  public static final List<String> NEGLECTED_VARIABLE_TYPES = Arrays.asList(
    "N", "NA", "NB", "NL", "NR", "ZAND", "NA/E+X", //cataclysmic variables
    "UV" //eruptive variable
  );
  
  /** Return the stars in the Hipparcos main catalog having the variability flag set, and whose Vmag is dimmer than the limiting mag. */
  List<Star> mainWithVariabilityFlagAndDimmerThan(Double limitingMag){
    return dimVariables(Consts.MAGNITUDE_LIMIT, Bandpass.V, new HipparcosCatalog().records());
  }
  
  /** Return core data for all variable stars in the two Hipparcos variability annexes, regardless of brightness. */
  Map<String, VariableStar> annexVariableStarData(){
    Map<String, VariableStar> result = new LinkedHashMap<>();
    HipparcosVariabilityAnnex annex = new HipparcosVariabilityAnnex();
    List<VariableStar> vars = annex.records();
    for(VariableStar var : vars) {
      result.put(var.IDENTIFIER, var);
    }
    log("Num stars in the variability annexes: " + result.size());
    return result;
  }
  
  // PRIVATE
  
  /** The Vmag straddles the limiting mag, and the variability type is not like a nova. */
  private boolean passesCriteria(VariableStar annex, Double limitingMag) {
    boolean result = false;
    if ("V".equals(annex.BANDPASS)){
      if (Util.isPresent(annex.MAXIMUM) && Util.isPresent(annex.MINIMUM)) {
        if ( !NEGLECTED_VARIABLE_TYPES.contains(annex.TYPE)) {
          Double min = Double.valueOf(annex.MINIMUM);
          Double max = Double.valueOf(annex.MAXIMUM);
          if (max <= limitingMag && limitingMag < min) {
            log("Variable star's Vmag straddles the limiting mag: " + annex);
            result = true;
          }
        }
        else {
          log("Variable star left out because of variable-type: " + annex + " *");
        }
      }
    }
    return result;
  }
  
  /** Filter-in if sufficiently dim, and variability flag is present. */
  private List<Star> dimVariables(Double mag, Bandpass bandpass, List<Star> stars){
    List<Star> result = new ArrayList<>();
    for (Star star : stars) {
      String m = star.MAGNITUDES.get(bandpass);
      String variability = star.FLAGS.get(Flag.VARIABILITY);
      if (Util.isPresent(m) && Util.isPresent(variability)) {
        Double magVal = Double.valueOf(m);
        if (magVal > mag) {
          result.add(star);
        }
      }
    }
    log("Num stars in main catalog with a variability flag and dimmer than the limiting mag: " + result.size());
    return result;
  }
}