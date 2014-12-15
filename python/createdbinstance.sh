# Use for example with OFLCOND-RUN12-SDR-01
gtag=$1
db="OFLP200"
rm *json
python ./jbcooldb.py --schema="ATLAS_COOLOFL" --json listSchemas $db
python ./jbcooldb.py --schema="ATLAS_COOLOFL" --json listNodes $db
python ./jbcooldb.py --schema="ATLAS_COOLOFL" --json tracetags $db $gtag
echo "Json files created for $db and $gtag "
ls -altr *json
python ./jbcooldb.py --schema="ATLAS_COOLOFL" --skipobsoletecheck MIGSCRIPT $db True
echo "Create scripts for migration : "
ls -altr COOLOFL*sh
 
