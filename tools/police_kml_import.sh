#!/bin/sh
function concatenate_args
{
    string="run-main com.codeandmagic.ukgist.tools.PoliceKmlImport"
    for a in "$@" # Loop over arguments
    do
        string+=" $a"
    done
    echo "$string"
}

# Usage:
args="$(concatenate_args "$@")"
echo $args
cd ..
sbt "$args"