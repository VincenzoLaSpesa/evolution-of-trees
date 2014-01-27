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
  
  def mediamobile
    alpha=1.0/8
    m=self[0]
    self.each{|n|
      m=(1-alpha)*m+alpha*n
    }
    m
  end
  
end 

def esegui(cmd)
  puts cmd
  str=`#{cmd}`
  puts str
  return str
end

#main
  jarurl="../../dist/tesi.jar"  
  medie=[]
  varianze=[]
  tempi=[]
  N=25
  N.times{|n|
          
          risultati=[] 
          generazioni=(n*4)+1;
          settings="--TorneoMultiBenchmark --generazioni #{generazioni}"
          command="java -jar '#{jarurl}' #{settings} | grep '§§' | awk '{print $2}'"  
	 tic=Time.now 
         str=esegui(command)
         toc=Time.now
	  str.each_line{|l|
		p=l.chomp.gsub(',', '.').to_f
		risultati << p if p>0	  
	  }
	  print risultati.mean.round(6)
	  puts "	(media)"
	  print risultati.standard_deviation.round(6)
	  puts "	(deviazione standard)"
          medie << risultati.mean.round(6)
          varianze << risultati.standard_deviation.round(6)
	  tempo=(toc - tic).to_f
          tempi << tempo
	  puts "mancano circa #{(2*tempo-tempi.mediamobile) * (N-n-1) / 60} minuti alla conclusione del benchmark, #{(2*tempo-tempi.mediamobile)/60} minuti previsti per la prossima iterazione. #{tempo/60} trascorsi nell'ultima."
         }

puts medie.inspect
puts varianze.inspect
puts tempi.inspect