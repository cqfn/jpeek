#!/bin/bash
#
# The MIT License (MIT)
#
# Copyright (c) 2017-2018 Yegor Bugayenko
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included
# in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

home=$(pwd)
jar="${home}/../../target/jpeek-jar-with-dependencies.jar"
if [ ! -e "${jar}" ]; then
  echo "${jar} doesn't exist, please run 'mvn clean install' first"
  exit -1
fi

rm -f "${home}/metrics-1.txt"
rm -f "${home}/metrics-2.txt"

while read line
do
  IFS=',' read -ra parts <<< "${line}"
  echo "${parts[0]}..."
  "${home}/get-sigma-and-mu.sh" "${jar}" "${parts[0]}" "${home}/metrics-1.txt"
  echo "${parts[0]} (with ctors)..."
  "${home}/get-sigma-and-mu.sh" "${jar}" "${parts[0]}" "${home}/metrics-2.txt" "--include-ctors"
done < "${home}/../03-filter-out-artifacts/target-926.csv"
