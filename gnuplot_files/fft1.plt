reset
#set decimalsign locale
set title "widmo mocy v"
set autoscale xy
#set yrange [-0.5:1.5]
set xrange [0:5]
plot 'neuron01fft.txt' using 1:2 title 'widmo mocy n2' with lines
