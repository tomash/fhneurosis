reset
#set decimalsign locale
set title "Przebiegi"
set yrange [-0.5:1.5]
plot 'neurons.txt' using 1:2 title 'v0' with lines,\
  'neurons.txt' using 1:5 title 'v1' with lines

