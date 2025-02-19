#!/bin/bash
#
# The MIT License (MIT)
#
# Copyright (c) 2017-2025 Yegor Bugayenko
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

# This script calculates sigma and mu of a Maven artifact.
# Just give it artifact location as the second command line argument:
# ./get-sigma-and-mu.sh jpeek-jar-with-dependencies.jar org.cactoos/cactoos

set -e

jar=$1
output=$3
path=${2//.//}
meta=$(curl --fail --silent "https://repo1.maven.org/maven2/${path}/maven-metadata.xml")
version=$(echo "${meta}" | xmllint --xpath '/metadata/versioning/latest/text()' -)
artifact=$(echo "${meta}" | xmllint --xpath '/metadata/artifactId/text()' -)

home=$(pwd)
dir=$(mktemp -d /tmp/jpeek-XXXX)
# shellcheck disable=SC2064
trap "rm -rf ${dir}" EXIT
curl --fail --silent "https://repo1.maven.org/maven2/${path}/${version}/${artifact}-${version}.jar" > "${dir}/${artifact}.jar"
cd "${dir}"
mkdir "${artifact}"
unzip -o -q -d "${artifact}" "${artifact}.jar"
java -jar "${jar}" --sources "${artifact}" --target ./target --quiet
ruby "${home}/collect.rb" target >> "${output}"
cd
rm -rf "${dir}"
