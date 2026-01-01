# SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
# SPDX-License-Identifier: MIT

metrics = ARGV[0]
dir = ARGV[1]

probs = []
File.open(metrics).read.each_line.map do |l|
  cohesion, flag, size = l.split(',', 3)
  probs << cohesion.to_f.round(2)
end

def draw(probs)
  total = 100
  slices = []
  total.times do |i|
    slices[i] = probs.select { |p| p <= i.to_f / total && p > (i.to_f-1) / total }.count
  end
  txt = '\begin{tikzpicture}' +
  '\begin{axis}[width=12cm,height=6cm,' +
  'axis lines=middle, xlabel={$p_i$}, ylabel={classes},' +
  "xmin=0, xmax=1, ymin=0, ymax=#{slices.max}," +
  'grid=major]\addplot [only marks, mark size=1pt] table {' + "\n"
  slices.each_with_index do |c, i|
    txt += "#{i.to_f / 100} #{c}\n"
  end
  txt + '};\end{axis}\end{tikzpicture}'
end

File.write(File.join(dir,'graph-distribution.tex'), draw(probs))
