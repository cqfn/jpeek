#!/bin/bash
#
# The MIT License (MIT)
#
# SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
# SPDX-License-Identifier: MIT

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
