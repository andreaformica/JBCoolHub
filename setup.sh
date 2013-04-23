alldirs=`ls -1`
alldirs=`find . -name "build.xml" -print | awk -F'/' '{print $2}'`
wdir=$PWD
if [ ! -e lib ]; then
  mkdir lib
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
