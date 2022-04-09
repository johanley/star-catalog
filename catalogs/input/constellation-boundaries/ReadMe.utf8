VI/42       Identification of a Constellation From Position  (Roman 1987)
================================================================================
Identification of a Constellation from a Position
    Roman N. G.
    <Pub. Astron. Soc. Pac. 99, 695, (1987)>
    =1987PASP...99..695R
================================================================================
ADC_Keywords: Constellations

Description:
   A table of constellation boundaries was rearranged to permit the
   rapid identification of the constellation to which a position refers.
   A software program is provided to precess positions at another
   equinox to those at 1875.0.

Introduction:
   Barry Rappaport put the list of constellation boundaries that was
   published in 1930 (Delporte 1930) into electronic form.  This form
   made it convenient to manipulate the data into an arrangement that
   is easier to use for finding a constellation from a position. The
   southern boundaries of each constellation are listed in order of
   declination and then by the eastern terminus of each declination arc.

   A program, in FORTRAN77, for determining the constellation using the
   data in data.dat is provided in two forms.  Comments describe
   the format in which the positions must be entered.  The main program is
   followed by a precession subroutine which permits the use of positions at
   any epoch.

   The file data.dat is a list of constellation boundaries in the form
   Lower Right Ascension, Upper Right Ascension, Lower Declination,
   three letter abbreviation for the Constellation.

    The following is an example of the output of the program:
      RA =  9.0000 DEC =  65.0000  IS IN CONSTELLATION UMa
      RA = 23.5000 DEC = -20.0000  IS IN CONSTELLATION Aqr
      RA =  5.1200 DEC =   9.1200  IS IN CONSTELLATION Ori
      RA =  9.4555 DEC = -19.9000  IS IN CONSTELLATION Hya
      RA = 12.8888 DEC =  22.0000  IS IN CONSTELLATION Com
      RA = 15.6687 DEC = -12.1234  IS IN CONSTELLATION Lib
      RA = 19.0000 DEC = -40.0000  IS IN CONSTELLATION CrA
      RA =  6.2222 DEC = -81.1234  IS IN CONSTELLATION Men
 END OF INPUT POSITIONS AFTER: RA =   6.2222   DEC = -81.1234
 THE EQUINOX FOR THESE POSITIONS IS 1950.0

   A C version of the program for those who have not access to a f77
   compiler is also available.

File Summary:
--------------------------------------------------------------------------------
 FileName    Lrecl    Records    Explanations
--------------------------------------------------------------------------------
ReadMe          80          .    This file
program.f       76        127    FORTRAN version of the software
program.c       77        156    C version of the software
data.dat        29        357    The data file with constellation boundaries
--------------------------------------------------------------------------------

Byte-by-byte Description of file: data.dat
--------------------------------------------------------------------------------
  Bytes  Format  Units   Label    Explanations
--------------------------------------------------------------------------------
 2- 8     F7.4    h      RA_low   Lower right ascension range, equinox=1875
10-16     F7.4    h      RA_up    Upper right ascension range, equinox=1875
18-25     F8.4    deg    DE_low   Lower (southern) declination, equinox=1875
27-29     A3      ---    const    Constellation name
--------------------------------------------------------------------------------

History:
  * 30-Dec-1999: one line (#229) was corrected by Nancy G. Roman

References:
   Delporte, E. 1930, Delimitation Scientifique des Constellations,
      Cambridge: Cambridge U. Press
   Roman N.G. 1987, Publ. Astron. Soc. Pacific, 99, 695-699
================================================================================
(End)       Nancy Grace Roman          [ADC/SSDOO]                   22-Feb-1996
