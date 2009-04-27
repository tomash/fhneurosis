reset
#set decimalsign locale
set title "SNR(D,sns)"
set yrange [0:100]
set xrange [0:6E-5]
plot 'c_0.00.txt' using 1:2 title 'sns = 0.05' with linespoints
