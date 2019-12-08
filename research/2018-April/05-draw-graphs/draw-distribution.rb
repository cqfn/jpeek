
metrics = ARGV[0]
dir = ARGV[1]

probs = []
File.open(metrics).read.each_line.map do |l|
  cohesion, flag, size = l.split(',', 3)
  probs << cohesion.to_f.round(2)
end

width = 0.1

def draw(slices)
  txt = '\begin{tikzpicture}' +
  '\begin{axis}[width=12cm,height=6cm,' +
  'axis lines=middle, xlabel={$S_i$}, ylabel={$C_i$},' +
  'xmin=0, xmax=100, ymin=0, ymax=1}' +
  'grid=major]\addplot [only marks, mark size=1pt] table {' + "\n"
  slices.each_with_index do |c, i|
    txt += "#{c[:size]} #{c[:cohesion]}\n"
  end
  txt + '};\end{axis}\end{tikzpicture}'
end

File.write(File.join(dir,'graph-distribution.tex'), draw(slices))

