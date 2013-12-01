#!/usr/bin/ruby

module Enumerable

    def sum
      self.inject(0){|accum, i| accum + i }
    end

    def mean
      self.sum/self.length.to_f
    end

    def sample_variance
      m = self.mean
      sum = self.inject(0){|accum, i| accum +(i-m)**2 }
      sum/(self.length - 1).to_f
    end

    def standard_deviation
      return Math.sqrt(self.sample_variance)
    end

end 


jarurl="../../dist/tesi.jar"
settingsurl="./jsonsettings.json"

puts "rigenero il dataset"
#`cd /home/darshan/Uni/Tesi/tesi/Tesi/dataset/ ; ruby taglia.rb`

puts "avvio il benchmark"
command="java -jar '#{jarurl}' --gait --settings='#{settingsurl}' | grep 'p=' | awk '{print $2}'"

risultati=[]
100.times{
  p=`#{command}`.chomp.gsub(',', '.').to_f #cazzo di virgole al posto del punto...
  risultati << p
  puts p
}

puts "-"
print risultati.mean.round(4)
puts "	(media)"
print risultati.standard_deviation.round(4)
puts "	(deviazione standard)"