reset
#set decimalsign locale
set title "SNR(D,sns)"
set yrange [0:100]
set xrange [0:6E-5]
plot 'snr_d_sns.txt' using 1:2 title 'sns = 0.0' with linespoints,\
  'snr_d_sns.txt' using 1:3 title 'sns = 0.05' with linespoints,\
  'snr_d_sns.txt' using 1:4 title 'sns = 0.10' with linespoints

