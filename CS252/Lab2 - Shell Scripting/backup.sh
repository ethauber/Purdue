#!/bin/bash
file=$1
BACKUPDIR=$2
INTERVAL=$3
MAXBACKUP=$4
currBackups=0
#array for backups
declare -a array

#debug statments(5)
#echo file is $1
#echo BACKUPDIR is $BACKUPDIR
#echo INTERVAL is $INTERVAL
#echo MAXBACKUP is $MAXBACKUP
#echo currBackups is $currBackups

#error checks
#check if the length of the arguments is 4
if [ "$#" -ne 4 ]; then
    echo usage: ./backup.sh file BACKUPDIR INTERVAL MAXBACKUP
    exit 1
fi
#check if the third argument is a number
if [[ ! $INTERVAL =~ ^[0-9]+$ ]]; then
    echo INTERVAL IS INVALID!
    echo Interval must be a positive real number.
    exit 1
fi
#check if the fourth argument is a number
if [[ ! $MAXBACKUP =~ ^[0-9]+$ ]]; then
    echo MAXBACKUP IS INVALID!
    echo Max backkups must be a positive real number.
    exit 1
fi
#check if file exists
if [[ ! -a $file ]]; then
    echo ERROR: FILE DOES NOT EXIST
    exit 1
fi
#check if the interval is zero
if [ $INTERVAL -eq 0 ]; then
    echo ERROR: ZERO TIME NOT POSSIBLE
    exit 1
fi
#check if max backups are zero
if [ $MAXBACKUP -eq 0 ]; then
    echo ERROR: ZERO BACKUPS IMPLIES PROGRAM TERMINATION
    exit 1
fi

#create directory if it does not exist
if [ ! -d $BACKUPDIR  ]; then
    mkdir $BACKUPDIR
fi

#take fileName
ogBase=`basename $file`
newFile=`basename $file`

#circular array indexing
#circular array index
cai=0
#oldest circular array index
ocai=0

#intial backup check
initialized=0

while [ 1 ]; do
    
    #remove oldest backup when the at maximum backups
    if [ $currBackups -gt $MAXBACKUP ]; then
	#`find . -name "*$ogBase" -type f -printf '%T+ %p\n' | sort | head -n 1 | awk '{print $2}' | xargs rm -v`
	echo "cai:$cai"
	echo "deleted: ${array[$ocai]} in array[ocai:$ocai]"
	rm ${array[$ocai]}
	let ocai++
	if [ $ocai -gt $MAXBACKUP ]; then
	    echo ocai hit max
	    let ocai=0
	fi
	if [ $cai -gt $MAXBACKUP ]; then
            echo cai hit max
	    let cai=0
	fi
	#decrement counter
	let currBackups--
    else
        #check if not at maximum number of backups
        if [ $currBackups -le $MAXBACKUP ]; then
	    newWithBackDir=$BACKUPDIR'/'$newFile
	    #initialize check
	    if [ $initialized -eq 0 ]; then
                cp "./"$file "./"$newWithBackDir
	        array[$cai]="./$newWithBackDir"
	        echo "backed up initial: ${array[$cai]} in array[cai:$cai]"
	        let cai++
	        let currBackups++
	        let initialized=1
	        continue
            fi	
	    #debug (1)
	    #$echo $newWithBackDir
	    #check if diff outputs any difference
	    diffOut=`diff $newWithBackDir $file`	
	    if [ ${#diffOut} -gt 0 ]; then
	        tmpDate=`date "+%F(%I:%M:%S)_"`
	        #debug (1)
	        #echo $tmpDate$newFile
	        #copy file to backupdir
	        `cp "./"$file "./"$BACKUPDIR'/'$tmpDate$ogBase`
	        #send email with output from diff
	        echo $diffOut > diffOut.txt
	        /usr/bin/mailx -s "mail-diff" $USER < diffOut.txt
	        newFile=$tmpDate$ogBase
	        array[$cai]="./$BACKUPDIR/$tmpDate$ogBase"
	        echo "backed up: ${array[$cai]} in array[cai:$cai]"
	        let currBackups++
	        let cai++
	    fi
	fi
    fi

    #sleep for interval
    sleep $INTERVAL
done
