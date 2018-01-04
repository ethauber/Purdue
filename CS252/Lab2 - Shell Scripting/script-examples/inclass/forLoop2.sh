#1/bin/bash
for i in `seq 1 100`; do
    if [ "$i" -gt 10 ] && [ $i -lt 90 ]; then
        echo $i
    fi
done
