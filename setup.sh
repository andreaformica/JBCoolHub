alldirs=`ls -1`
alldirs=`find . -name "build.xml" -print | awk -F'/' '{print $2}'`
wdir=$PWD

# Change this variables to point to your local installation
#COMMON_LIBS=/Users/formica/git/libraries
#EXTERNAL_LIBS=/Users/formica/MyApp/Library/external
COMMON_LIBS=$1
EXTERNAL_LIBS=$2

# setup lib and other directories: libraries and external are mandatory for project build
if [ ! -e lib ]; then
  mkdir lib
fi
if [ ! -e libraries ]; then
   ln -s $COMMON_LIBS .
fi
if [ ! -e externals ]; then
   ln -s $EXTERNAL_LIBS .
fi


for adir in $alldirs; do
echo "present directory is $wdir: examine $adir"
echo "linking common and externals"
cd $adir
if [ ! -e common ]; then
 echo "directory common does not exists...linking it"
 ln -s $wdir/common .
fi
if [ ! -e external ]; then
 echo "directory external does not exists...linking it"
 ln -s $wdir/external .
fi
if [ ! -e lib ]; then
 echo "directory lib does not exists...linking it"
 ln -s $wdir/lib .
fi

cd $wdir
done
