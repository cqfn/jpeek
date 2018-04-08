#!/usr/bin/env ruby

STDOUT.sync = true

require 'nokogiri'

dir=ARGV[0]

metrics = Dir["#{dir}/*"]
  .map { |f| File.basename(f) }
  .select { |f| f =~ /[A-Z0-9]+\.xml/ }
  .map { |f| f.gsub(/\.xml$/, '') }

classes = {}
metrics.each do |m|
  xml = Nokogiri::XML(File.open(File.join(dir, "#{m}.xml")))
  title = xml.xpath('/metric/title/text()')[0].to_s
  xml.xpath('//class[@element="true"]').each do |c|
    id = c.xpath('@id')[0].to_s
    classes[id] = {} unless classes[id]
    value = c.xpath('@value')[0].to_s.to_f
    next if value > 1 or value < 0
    value = 1 - value if title == 'LCOM5'
    classes[id][title] = value
  end
end

skeleton = Nokogiri::XML(File.open(File.join(dir, "skeleton.xml")))
skeleton.xpath('//class').each do |c|
  id = c.xpath('@id')[0].to_s
  classes[id] = {} unless classes[id]
  classes[id][:immutable] = c.xpath('attributes/attribute[@final="false"]').empty?
  classes[id][:size] = c.xpath('attributes/attribute').size +
    c.xpath('methods/method').size
end

classes.select! do |c,h|
  metrics.all? { |m| h.key?(m) } and h.key?(:immutable) and h.key?(:size)
end

classes.each do |c,h|
  print metrics.map { |m| h[m] }.inject(:+) / metrics.size
  print ",#{h[:immutable]}"
  print ",#{h[:size]}\n"
end
