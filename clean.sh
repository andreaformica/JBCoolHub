alldirs=`find . -name "build.xml" -print | awk -F'/' '{print $2}'`

wdir=$PWD

# Change this variables to point to your local installation
#COMMON_LIBS=/Users/formica/git/libraries
#EXTERNAL_LIBS=/Users/formica/MyApp/Library/external

echo "Clean all build directories"
ant -f jbcool-ant.xml clean-all

# setup lib and other directories: libraries and external are mandatory for project build
if [ -e lib ]; then
  rm -r lib
fi
if [ -L libraries ]; then
  rm libraries 
fi
if [ -L external ]; then
  rm external 
fi


for adir in $alldirs; do
echo "present directory is $wdir: examine $adir"
echo "Removing common and externals"
cd $adir
if [ -L common ]; then
 echo "directory common exists...remove it"
 rm common 
fi
if [ -L external ]; then
 echo "directory external exists...remove it"
 rm external 
fi
if [ -L lib ]; then
 echo "directory lib exists...remove it"
 rm lib 
fi

cd $wdir
done

echo "Clean up done"
