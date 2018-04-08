
metrics = ARGV[0]
dir = ARGV[1]

immutable = []
mutable = []
File.open(metrics).read.each_line.map do |l|
  cohesion, flag, size = l.split(',', 3)
  hash = { cohesion: cohesion.to_f.round(2), size: size.to_i }
  if flag == 'true'
    immutable << hash
  else
    mutable << hash
  end
end

def draw(array)
  unique = array.uniq { |c| "#{c[:cohesion]}/#{c[:size]}" }
  xavg = unique.map { |c| c[:size] }.inject(:+) / unique.size
  yavg = unique.map { |c| c[:cohesion] }.inject(:+) / unique.size
  txt = '\begin{tikzpicture}' +
  '\begin{axis}[width=12cm,height=6cm,' +
  'axis lines=middle, xlabel={$S_i$}, ylabel={$C_i$},' +
  'xmin=0, xmax=100, ymin=0, ymax=1,' +
  "extra x ticks={#{xavg}},extra y ticks={#{yavg}},extra tick style={major grid style=black}," +
  'grid=major]\addplot [only marks, mark size=1pt] table {' + "\n"
  unique.each do |c|
    txt += "#{c[:size]} #{c[:cohesion]}\n"
  end
  txt + '};\end{axis}\end{tikzpicture}'
end

File.write(File.join(dir,'graph-immutable.tex'), draw(immutable))
File.write(File.join(dir,'graph-mutable.tex'), draw(mutable))

