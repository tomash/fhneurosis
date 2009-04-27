reset
#set decimalsign locale
set title "SNR(D,sns), delay=ln(2)T"
set yrange [0:70]
set xrange [0:6E-5]
plot 'snr_d_sns_delayln2.txt' using 1:2 title 'sns = 0.0' with linespoints,\
  'snr_d_sns_delayln2.txt' using 1:3 title 'sns = 0.05' with linespoints

