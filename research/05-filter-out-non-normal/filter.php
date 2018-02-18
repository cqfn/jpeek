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

$input = fopen($argv[1], 'r');
if (!$input) {
  throw new Exception('Cannot open input file');
}
$output = fopen($argv[2], 'w+');
if (!$output) {
  throw new Exception('Cannot open output file');
}
while (!feof($input)) {
  $line = fgets($input);
  $parts = explode(' ', $line);
  $artifact = $parts[0];
  $classes = intval($parts[1]);
  $off = false;
  for ($i = 2; $i < count($parts); ++$i) {
    preg_match('/([A-Z0-9]+)=([\\.\\d]+)\\/([\\.\\d]+)/', $parts[$i], $matches);
    $metric = $matches[1];
    $mu = floatval($matches[2]);
    if ($metric == 'LCOM5') {
      $mu = 1 - $mu;
    }
    $sigma = floatval($matches[3]);
    if ($sigma < $mu * 0.31) {
      $off = true;
      break;
    }
  }
  if (!$off) {
    fputs($output, $line);
  }
}
fclose($input);
fclose($output);
