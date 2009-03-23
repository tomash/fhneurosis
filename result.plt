reset
#set decimalsign locale
set title "Przebiegi"
set yrange [-0.5:1.5]
plot 'neurons.txt' using 1:2 title 'v0' with lines,\
  'neurons.txt' using 1:3 title 'v0_flat' with lines,\
  'neurons.txt' using 1:5 title 'v1' with lines,\
  'neurons.txt' using 1:6 title 'v1_flat' with lines

