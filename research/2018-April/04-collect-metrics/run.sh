#!/bin/bash
#
# SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
# SPDX-License-Identifier: MIT

set -e

home=$(pwd)
jar=~/.m2/repository/org/jpeek/jpeek/1.0-SNAPSHOT/jpeek-1.0-SNAPSHOT-jar-with-dependencies.jar
if [ ! -e "${jar}" ]; then
  echo "${jar} doesn't exist, please run 'mvn clean install' first"
  exit 1
fi

rm -f "${home}/metrics.txt"

while read -r line
do
  IFS=',' read -ra parts <<< "${line}"
  echo "${parts[0]}..."
  "${home}/process-artifact.sh" "${jar}" "${parts[0]}" "${home}/metrics.txt"
done < "${home}/../03-filter-out-artifacts/target-926.csv"
