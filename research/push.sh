#!/bin/bash

for i in $(shuf ./2017)
do
  curl --silent http://i.jpeek.org/${i}/index.html > /dev/null
  echo $i
done
