#!/bin/bash
a=$1
if [ ! -z "$a" ]; then
    echo $a
else
    echo arg is empty!
fi
