#!/bin/bash
if [ "$1" = "" ]; then
    echo "Usage ./pwcheck.sh <file containing password>"
else
    #prints file
    #echo "$1"
    
    PASSW=`cat $1`
    #prints password
    #echo $PASSW

    if [ ${#PASSW} -lt 6 ] || [ ${#PASSW} -gt 32 ]; then
	echo "Error: Password length invalid."
    else
	points=${#PASSW}
	#special char check
	if [[ $PASSW == *['#'$\+%@]* ]]; then
	    let points=points+5
	    echo '#'$+%@  $points
	fi;
	#numberical check 
	if [[ $PASSW == *[0-9]* ]]; then
	    let points=points+5
	    echo 0-9 $points
	fi;
	#alpha character check
	if [[ $PASSW == *[A-Za-z]* ]]; then
	    let points=points+5
	    echo A-Za-z $points
	fi;
	#two repitition check
	repeat2=`egrep '(.)\1{1}' $1`
	if [ ${#repeat2} -gt 0 ]; then
	    let points=points-10
	    echo two repitition $points
	fi;
	repeat3Upper=`egrep '([A-Z])\1[A-Z]' $1`
	repeat3Lower=`egrep '([a-z])\1[a-z]' $1`
	consecutiveNums=`egrep '[0-9][0-9]' $1`
	if [ ${#repeat3Upper} -gt 0 ]; then
	    let points-=3
	    echo repeat3Upper $points
	fi
	if [ ${#repeat3Lower} -gt 0 ]; then
	    let points-=3
	    echo repeat3Lower $points
	fi
	if [ ${#consecutiveNums} -gt 0 ]; then
	    let points-=3
	    echo consecutiveNums $points
	fi
	echo Password Score: $points
    fi;
fi;
