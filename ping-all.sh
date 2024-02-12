#!/bin/bash
set -e

while IFS= read -r a; do
	curl -s "https://i.jpeek.org/${a}/index.html" > /dev/null
	echo "${a} pinged"
done < artifacts.csv
