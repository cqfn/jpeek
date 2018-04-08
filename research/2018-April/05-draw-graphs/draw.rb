
metrics = ARGV[0]
dir = ARGV[1]

immutable = []
mutable = []
File.open(metrics).read.each_line.map do |l|
  cohesion, flag, size = l.split(',', 3)
  hash = { cohesion: cohesion.to_f, size: size.to_i }
  if flag == 'true'
    immutable << hash
  else
    mutable << hash
  end
end

def draw(array)
  txt = '\begin{tikzpicture}' +
  '\begin{axis}[axis lines=middle, xlabel={size}, ylabel={cohesion}, x post scale=1.2,' +
  "xmin=#{array.map{ |c| c[:size] }.min}," +
  "xmax=#{array.map{ |c| c[:size] }.max}," +
  'ymin=0, ymax=1]\addplot [only marks] table {' + "\n"
  array.each do |c|
    txt += "#{c[:size]} #{c[:cohesion]}\n"
  end
  txt + '};\end{axis}\end{tikzpicture}'
end

File.write(File.join(dir,'graph-immutable.tex'), draw(immutable))
File.write(File.join(dir,'graph-mutable.tex'), draw(mutable))

