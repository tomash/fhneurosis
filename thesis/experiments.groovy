package thesis

D0 = 1E-6
D1 = 5E-5

props = new Properties()
f = new FileInputStream("props.txt")
props.load(f)
f.close()
props.setProperty("D", ((1E-2).toString()))
f2 = new FileOutputStream("props.txt")
props.store(f2, "wyjasnienia zmiennych w props_with_properties.txt")

for(d=D0; d<=D1; d+=1E-6)
{
	props = new Properties()
	f = new FileInputStream("props.txt")
	props.load(f)
	println props.getProperty("D")
}