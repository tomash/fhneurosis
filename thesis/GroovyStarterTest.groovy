package thesis;
import groovy.util.GroovyTestCase

public class GroovyStarterTest extends GroovyTestCase {

	void testReMap() {
		def starter = new GroovyStarter();
		starter.rawResults = [
		              [[0:10.0, 1:7.5], [0:9.8,  1:7.6]],
		              [[0:12.0, 1:9.5], [0:12.3, 1:9.3]],
		              [[0:16.0, 1:11.0],[0:16.8, 1:11.3]]
		               ]
        starter.d_array = [0.05, 0.1, 0.15]

        def remapped = starter.reMap()
        assertEquals("dupa", [
                              0:[ [10.0, 9.8], [12.0, 12.3], [16.0, 16.8] ],
                              1:[ [7.5, 7.6],  [9.5, 9.3],   [11.0, 11.3] ]
                              ], remapped)

	}

	void testArraysVsLists() {
		def list = [0: 5, 1: 8]
		def   arr = [5, 8]
        assertFalse(arr.equals(list))
	}
	
}

