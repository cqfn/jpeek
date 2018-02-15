<?php
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

$f = fopen($argv[1], 'r');
if (!$f) {
  throw new Exception('Cannot open first file');
}
$diffs = [];
while (!feof($f)) {
  $line = fgets($f);
  $parts = explode(' ', $line);
  if (count($parts) != 3) {
    continue;
  }
  $artifact = $parts[0];
  $classes = intval($parts[1]);
  $rank = floatval($parts[2]);
  $pos = intval($parts[3]);
  $diffs[$artifact] = $pos;
}
fclose($f);
$f = fopen($argv[2], 'r');
if (!$f) {
  throw new Exception('Cannot open second file');
}
while (!feof($f)) {
  $line = fgets($f);
  $parts = explode(' ', $line);
  if (count($parts) != 3) {
    continue;
  }
  $artifact = $parts[0];
  $classes = intval($parts[1]);
  $rank = floatval($parts[2]);
  $pos = intval($parts[3]);
  $diffs[$artifact] = $diffs[$artifact] - $pos;
}
fclose($f);
$f = fopen($argv[3], 'w+');
foreach ($diffs as $a => $d) {
  fputs($f, "${a} ${d}\n");
  ++$pos;
}
fclose($f);
