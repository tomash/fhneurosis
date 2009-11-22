set yrange[-2:2]
set xrange[-4*pi:4*pi]
plot (sin(x) + sin(x-0.5*pi)),\
       sin(x),\
       sin(x-0.5*pi)
