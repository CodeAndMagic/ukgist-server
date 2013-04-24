#!/bin/sh
cd ..
sbt 'run-main com.codeandmagic.ukgist.tools.PoliceKmlImport "$@"'