package starcat;

import static starcat.util.LogUtil.log;

import java.io.IOException;
import java.util.List;

import starcat.catalogs.BrightStarCatalog;
import starcat.catalogs.CoreCatalog;
import starcat.catalogs.GeneralCatalogOfMeanRV;
import starcat.catalogs.Hipparcos2Catalog;
import starcat.catalogs.HipparcosCatalog;
import starcat.catalogs.PulkovoRVCatalog;
import starcat.make.bsc.hipparcos.HipparcosBSC;
import starcat.star.Bandpass;
import starcat.star.Star;
import starcat.star.StarTool;
import starcat.util.Consts;

/** 
 Top-level class for running scripts that analyze existing catalogs, and create new ones. 
 
 <P>In order to run these scripts, you need to tell the program where to find catalog files. 
 You do that by setting an argument called 'project-root' on the command line.
 
 <P><pre>-Dproject-root=C:\blah\star-catalog</pre>

 <P>It points to the root of this project, underneath which is found the 'catalogs' directory, 
 containing text files with data files from various catalogs.
 
 <P>You need to DOWNLOAD the catalog files separately, from VizieR. 
 They aren't included in this project.
 See the catalogs/input directory. 
 Each catalog has a SOURCE.utf8 file with a link to the Vizier catalog, and the name of the missing file
 that you need to download.
*/ 
public final class RunMain {
  
  /** Run a chosen script. */
  public static void main(String[] args) throws IOException {
    //showStatsForInputCatalogs();
    //showStatsForFilteredHipparcos();
    makeHipparcosBrightStarCatalog();
    log("Done.");
  }
  
  /** Make a bright star catalog based on Hipparcos-2 astrometry. */
  static void makeHipparcosBrightStarCatalog() throws IOException {
    HipparcosBSC hp = new HipparcosBSC();
    boolean sortByMagnitude = false;
    hp.writeCatalogFile("open-source-bsc", sortByMagnitude);
  }
  
  /** Show simple stats for selected input catalogs. */
  static void showStatsForInputCatalogs() {
    CoreCatalog[] catalogs = {
      new BrightStarCatalog(), 
      /* new BrightStarCatalogSupplement(), */ 
      new HipparcosCatalog(), 
      new PulkovoRVCatalog(),
      new GeneralCatalogOfMeanRV(),
      new Hipparcos2Catalog()
    };
    for(CoreCatalog catalog: catalogs) {
      List<Star> stars = catalog.records();
      catalog.statsFor(stars);
    }
  }
  
  /** Show simple stats for bright stars selected rom Hipparcos-1. */
  static void showStatsForFilteredHipparcos() {
    CoreCatalog catalog = new HipparcosCatalog();
     List<Star> brightHipparcosStars = StarTool.filterByMagLTEQ(Consts.MAGNITUDE_LIMIT, Bandpass.V, catalog.records());
     catalog.statsFor(brightHipparcosStars);
  }
}