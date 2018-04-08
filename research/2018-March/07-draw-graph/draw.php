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

$diffs = fopen($argv[1], 'r');
if (!$diffs) {
  throw new Exception('Cannot open diff file');
}
$x = [];
$y = [];
while (!feof($diffs)) {
  $line = fgets($diffs);
  $parts = explode(' ', $line);
  if (count($parts) < 3) {
    continue;
  }
  $artifact = $parts[0];
  $diff = intval($parts[1]);
  $classes = intval($parts[2]);
  $x[] = $diff;
  $y[] = $classes;
}
fclose($diffs);

$tex = fopen($argv[2], 'w+');
if (!$tex) {
  throw new Exception('Cannot open .tex file');
}
fputs(
  $tex,
  "\\begin{tikzpicture}\n"
  . "\\begin{axis}[axis lines=middle, xlabel=\$d_a\$, ylabel={classes},\n"
  . "x post scale=1.2,"
  . "xmin=" . min($x) . ", xmax=" . max($x) . ", ymin=" . min($y) . ", ymax=" . max($y). "]\n"
  . "\\addplot [only marks] table {\n"
);
for ($i = 0; $i < count($x); ++$i) {
  fputs($tex, "${x[$i]} ${y[$i]}\n");
}
fputs($tex, "};\n\\end{axis}\n");
fputs($tex, "\\end{tikzpicture}\n");
fclose($tex);
