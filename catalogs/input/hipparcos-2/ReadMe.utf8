I/311               Hipparcos, the New Reduction       (van Leeuwen, 2007)
================================================================================
Hipparcos, the new Reduction of the Raw data
     van Leeuwen F.
    <Astron. Astrophys. 474, 653 (2007)>
    =2007A&A...474..653V
================================================================================
ADC_Keywords: Positional data ; Proper motions ; Parallaxes, trigonometric ;
              Photometry ; Fundamental catalog
Mission_Name: Hipparcos
Keywords: space vehicles: instruments - methods: data analysis  - catalogs -
         astrometry  - instrumentation: miscellaneous

Abstract:
    A new reduction of the astrometric data as produced by the Hipparcos
    mission has been published, claiming accuracies for nearly all stars
    brighter than magnitude Hp=8 to be better, by up to a factor 4, than
    in the original catalogue.

    The new Hipparcos astrometric catalogue is checked for the quality of
    the data and the consistency of the formal errors as well as the
    possible presence of error correlations. The differences with the
    earlier publication are explained.

    Methods. The internal errors are followed through the reduction
    process, and the external errors are investigated on the basis of a
    comparison with radio observations of a small selection of stars, and
    the distribution of negative parallaxes. Error correlation levels are
    investigated and the reduction by more than a factor 10 as obtained in
    the new catalogue is explained.

    Results. The formal errors on the parallaxes for the new catalogue are
    confirmed. The presence of a small amount of additional noise, though
    unlikely, cannot be ruled out.

    Conclusions. The new reduction of the Hipparcos astrometric data
    provides an improvement by a factor 2.2 in the total weight compared
    to the catalogue published in 1997, and provides much improved data
    for a wide range of studies on stellar luminosities and local galactic
    kinematics.

Notice:
    The files included are slightly different from the ones published in
    the book, as an error that sometimes affected the goodness of fit
    value for the solution was corrected. The first version of these files
    (between June and 15 September 2008) also contained errors corrected
    after this date.

File Summary:
--------------------------------------------------------------------------------
 FileName   Lrecl  Records    Explanations
--------------------------------------------------------------------------------
ReadMe         80        .    This file
hip2.dat      276   117955    The Astrometric Catalogue
hip7p.dat     129     1338    Seven-parameter solutions
hip9p.dat     274      104    Nine-parameter solutions
hipvim.dat    129       25   *Variability-induced (VIM) solutions
--------------------------------------------------------------------------------
Note on hipvim.dat: solution for double stars having one variable component.
--------------------------------------------------------------------------------

See also:
    I/239 : The Hipparcos and Tycho Catalogues (ESA 1997)

Byte-by-byte Description of file: hip2.dat
--------------------------------------------------------------------------------
   Bytes Format Units    Label   Explanations
--------------------------------------------------------------------------------
   1-  6  I6    ---      HIP     Hipparcos identifier
   8- 10  I3    ---      Sn      [0,159] Solution type new reduction (1)
      12  I1    ---      So      [0,5] Solution type old reduction (2)
      14  I1    ---      Nc      Number of components
  16- 28 F13.10 rad      RArad   Right Ascension in ICRS, Ep=1991.25
  30- 42 F13.10 rad      DErad   Declination in ICRS, Ep=1991.25
  44- 50  F7.2  mas      Plx     Parallax
  52- 59  F8.2  mas/yr   pmRA    Proper motion in Right Ascension
  61- 68  F8.2  mas/yr   pmDE    Proper motion in Declination
  70- 75  F6.2  mas    e_RArad   Formal error on RArad
  77- 82  F6.2  mas    e_DErad   Formal error on DErad
  84- 89  F6.2  mas    e_Plx     Formal error on Plx
  91- 96  F6.2  mas/yr e_pmRA    Formal error on pmRA
  98-103  F6.2  mas/yr e_pmDE    Formal error on pmDE
 105-107  I3    ---      Ntr     Number of field transits used
 109-113  F5.2  ---      F2      Goodness of fit
 115-116  I2    %        F1      Percentage rejected data
 118-123  F6.1  ---      var     Cosmic dispersion added (stochastic solution)
 125-128  I4    ---      ic      Entry in one of the suppl.catalogues
 130-136  F7.4  mag      Hpmag   Hipparcos magnitude
 138-143  F6.4  mag    e_Hpmag   Error on mean Hpmag
 145-149  F5.3  mag      sHp     Scatter of Hpmag
     151  I1    ---      VA      [0,2] Reference to variability annex
 153-158  F6.3  mag      B-V     Colour index
 160-164  F5.3  mag    e_B-V     Formal error on colour index
 166-171  F6.3  mag      V-I     V-I colour index
 172-276 15F7.2 ---      UW      Upper-triangular weight matrix (G1)
--------------------------------------------------------------------------------
Note (1): Solution type.
    The solution type is a number 10xd+s consisting of two parts d and s:
    - s describes the type of solution adopted:
      1 = stochastic solution (dispersion is given in the 'var' column)
      3 = VIM solution (additional parameters in file hipvim.dat)
      5 = 5-parameter solution (this file)
      7 = 7-parameter solution (additional parameters in hip7p.dat)
      9 = 9-parameter solution (additional parameters in hip9p.dat)
    - d describes peculiarities, as a combination of values:
      0 = single star
      1 = double star
      2 = variable in the system with amplitude > 0.2mag
      4 = astrometry refers to the photocenter
      8 = measurements concern the secondary (fainter) in the double system

Note (2): as follows:
      0 = standard 5-parameter solution
      1 = 7- or 9-parameter solution
      2 = stochastic solution
      3 = double and multiple stars
      4 = orbital binary as resolved in the published catalog
      5 = VIM (variability-induced mover) solution
--------------------------------------------------------------------------------

Byte-by-byte Description of file: hip7p.dat
--------------------------------------------------------------------------------
   Bytes Format Units     Label  Explanations
--------------------------------------------------------------------------------
   1-  6  I6    ---       HIP    Hipparcos identifier
   8- 12  F5.2  ---       Fg     Detection statistic
  14- 19  F6.2  mas/yr2   dpmRA  Acceleration in Right Ascension
  21- 26  F6.2  mas/yr2   dpmDE  Acceleration in Declination
  28- 32  F5.2  mas/yr2 e_dpmRA  Formal error on dpmRA
  34- 38  F5.2  mas/yr2 e_dpmDE  Formal error on dpmDE
  39-129 13F7.2 ---       UW     Upper-triangular weight matrix U16..U28 (G1)
--------------------------------------------------------------------------------

Byte-by-byte Description of file: hip9p.dat
--------------------------------------------------------------------------------
   Bytes Format Units     Label   Explanations
--------------------------------------------------------------------------------
   1-  6  I6    ---       HIP     Hipparcos identifier
   8- 12  F5.2  ---       Fg      Detection statistic
  14- 19  F6.2  mas/yr2   dpmRA   Acceleration in Right Ascension
  21- 26  F6.2  mas/yr2   dpmDE   Acceleration in Declination
  28- 33  F6.2  mas/yr3   ddpmRA  Acceleration change in Right Ascension
  35- 40  F6.2  mas/yr3   ddpmDE  Acceleration change in Declination
  42- 46  F5.2  mas/yr2 e_dpmRA   Formal error on dpmRA
  48- 52  F5.2  mas/yr2 e_dpmDE   Formal error on dpmDE
  54- 58  F5.2  mas/yr3 e_ddpmRA  Formal error on ddpmRA
  60- 64  F5.2  mas/yr3 e_ddpmDE  Formal error on ddpmDE
  65-274 30F7.2 ---       UW      Upper-triangular weight matrix U16..U45 (G1)
--------------------------------------------------------------------------------

Byte-by-byte Description of file: hipvim.dat
--------------------------------------------------------------------------------
   Bytes Format Units   Label     Explanations
--------------------------------------------------------------------------------
   1-  6  I6    ---     HIP       Hipparcos identifier
   8- 12  F5.2  ---     Fg        Detection statistic
  14- 19  F6.2  mas     upsRA     VIM in Right Ascension (1)
  21- 26  F6.2  mas     upsDE     VIM in Declination (1)
  28- 32  F5.2  mas   e_upsRA     Formal error on upsRA
  34- 38  F5.2  mas   e_upsDE     Formal error on upsDE
  39-129 13F7.2 ---     UW        Upper-triangular weight matrix U16..U28 (G1)
--------------------------------------------------------------------------------
Note (1): the variability-induced movement is due to the variability
     of one component of the binary which changes the position of the
     photocenter along the RA (or Dec) axis with the quantity
         ups*(1 - dexp(-0.4(m_r_-m)))
     where m_r_ is the reference magnitude of the binary and m
     the observed magnitude of the binary, and dexp the decimal
     exponentiation (dexp(x) = 10^x^)
--------------------------------------------------------------------------------

Global Notes:
Note (G1): The upper-triangular weight matrix U is related to the
     covariance matrix C by
         C^-1^ = ~U U    (~U represents transposed U)
     The elements U_i_ forming the upper triangular matrix are stored as
         +-                         -+
         |  (1)  (2)  (4)  (7)  (11) |
         |   0   (3)  (5)  (8)  (12) |
         |   0    0   (6)  (9)  (13) |
         |   0    0    0  (10)  (14) |
         |   0    0    0    0   (15) |
         +-                         -+
     on the astrometric parameters RA, Dec, plx, pmRA, pmDE,
     and derivatives of proper motions for 7- and 9-parameter
     solutions.

Acknowledgements:
    Floor van Leeuwen, Institute of Astronomy, Cambridge University
    Cambrisge, UK

References:
    Floor van Leeuwen, 2007 "Hipparcos, the New Reduction of the Raw Data"
    Astrophysics & Space Science Library #350.

History:
  * 09-Jun-2008: Original version (with errors)
  * 16-Sep-2008: new files from author
================================================================================
(End)                                   Francois Ochsenbein [CDS]    16-Sep-2008
