reset
#set decimalsign locale
set title "SNR(D,c), sens=0.05"
set yrange [0:100]
set xrange [0:6E-5]
plot 'c_0.00.txt' using 1:2 title 'c = 0.00 T' with linespoints
