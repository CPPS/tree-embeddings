#!/bin/bash
#SBATCH -t 1:00:00
#SBATCH -N 10
#cd ${PBS_O_WORKDIR}

# Size of pointset
N=13

# Pool name
export STOPOS_POOL=point-parts

module load stopos

nprocs=`cat /proc/cpuinfo | grep processor | wc -l`
mbmem=`cat /proc/meminfo | awk '/MemTotal:/ { print int($2/1000000) ; exit }' | awk '{print int($1/'$nprocs'*1024) }'`

# Run one instance
run_one() {
    java -Xmx${mbmem}M -jar out.jar --point-offset $1 --threads 1 -n $N
}

outdir=$HOME/tree-workers
mkdir -p $outdir

for ((i=1; i<=ncores; i++)) ; do
(
    while true ; do
        # Fetch next
        stopos next -m

        if [ "$STOPOS" != "OK" ] ; then
            # No more data
            break
        fi

        if [ "$STOPOS_COMMITTED" -gt 3 ] ; then
            # Data was already handed out 3 times before.
            # Pretend it has been processed.
            stopos remove
            continue
        fi

        # Create a temp file to write to
        outfile=`tempfile`

        # Run the program
        run_one $STOPOS_VALUE 2>&1 > $outfile

        # Finalize the temp file
        outname="$STOPOS_KEY.`basename $outfile`"
        mv $outfile $outdir/$outname

        # Mark as done
        stopos remove
      done
) &
done

# Wait for everything to finish
wait
