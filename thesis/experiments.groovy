package thesis

import java.util.Date;
import java.util.Random;

D0 = 1E-6
D1 = 5E-5

println "GStringin some ${D0} \t ${D1}"
println String.format("%.6f", 34.7326123098)
System.out.printf("\t%f\n", 34.997766)

long seed = new Date().getTime();
println("initializing Random with seed ${seed}");
Random generator = new Random(seed);
n = 5
for(i in 1..(n-1))
{
	println(generator.nextDouble());
}