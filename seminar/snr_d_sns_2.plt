reset
#set decimalsign locale
set title "SNR(D,sns)"
set yrange [0:110]
set xrange [0:6E-5]
plot 'snr_d_sns_2.txt' using 1:2 title 'sns = 0.05' with linespoints

