reset
#set decimalsign locale
set title "SNR(D,c), sens=0.05"
set yrange [0:100]
set xrange [0:6E-5]
plot 'c_reference.txt' using 1:2 title 'reference' with lines,\
  'c_0.00.txt' using 1:2 title 'c = 0.00 T' with lines,\
  'c_0.10.txt' using 1:2 title 'c = 0.10 T' with lines,\
  'c_0.20.txt' using 1:2 title 'c = 0.20 T' with lines,\
  'c_0.50.txt' using 1:2 title 'c = 0.50 T' with lines,\
  'c_0.75.txt' using 1:2 title 'c = 0.75 T' with lines,\
  'c_0.95.txt' using 1:2 title 'c = 0.95 T' with lines
