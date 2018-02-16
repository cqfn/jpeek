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

function avg($line) {
  $parts = explode(' ', $line);
  $sum = 0;
  for ($i = 2; $i < count($parts); ++$i) {
    preg_match('/([A-Z0-9]+)=([\\.\\d]+)\\/([\\.\\d]+)/', $parts[$i], $matches);
    $metric = $matches[1];
    $mu = floatval($matches[2]);
    if ($metric == 'LCOM5') {
      $mu = 1 - $mu;
    }
    $sum += $mu;
  }
  return $sum / (count($parts) - 1);
}

function load($path, &$array, $idx) {
  $f = fopen($path, 'r');
  if (!$f) {
    throw new Exception('Cannot open input file');
  }
  while (!feof($f)) {
    $line = fgets($f);
    $parts = explode(' ', $line);
    if (count($parts) < 2) {
      continue;
    }
    $artifact = $parts[0];
    $classes = intval($parts[1]);
    $avg = avg($line);
    if (!array_key_exists($artifact, $array)) {
      $array[$artifact] = array('classes' => $classes);
    }
    $array[$artifact]['avg' . $idx] = $avg;
  }
  fclose($f);
}

function order(&$array, $idx) {
  uasort(
    $array,
    function ($a, $b) {
      return $a['avg' . $idx] < $b['avg' . $idx];
    }
  );
  $pos = 0;
  foreach ($array as &$h) {
    $h['pos' . $idx] = $pos;
    ++$pos;
  }
}

$all = array();
load($argv[1], $all, 1);
load($argv[2], $all, 2);

$all = array_filter(
  $all,
  function ($h) {
    return array_key_exists('avg1', $h) && array_key_exists('avg2', $h);
  }
);

order($all, 1);
order($all, 2);

foreach ($all as &$h) {
  $h['diff'] = $h['pos1'] - $h['pos2'];
}

$output = fopen($argv[3], 'w+');
if (!$output) {
  throw new Exception('Cannot open output file');
}
foreach ($all as $a => $h) {
  fputs($output, "${a} ${h['diff']} ${h['classes']} ${h['avg1']} ${h['pos1']} ${h['avg2']} ${h['pos2']}\n");
}
fclose($output);
