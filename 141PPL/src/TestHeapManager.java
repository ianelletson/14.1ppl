import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class TestHeapManager {

	private HeapManager m;

	@Test
	public void testWebbersCase() {
		int p1 = m.allocate(4); //(0)1-4
		assertEquals(1, p1);
		int p2 = m.allocate(2); //(5)6-7
		assertEquals(6, p2);
		m.deallocate(p1);
		int p3 = m.allocate(1); //(0)1
		assertEquals(1, p3);
	}
	
	@Test
	public void testLowestAddressFirst(){
		int p1 = m.allocate(4); //(0)1-4
		assertEquals(1, p1);
		int p2 = m.allocate(2); //(5)6-7
		assertEquals(6, p2);
		m.deallocate(p1);
		m.deallocate(p2);
		int p3 = m.allocate(2); //(0)1-2
		assertEquals(1, p3);
	}
	
	@Test
	public void testCoalescing(){
		int p1 = m.allocate(4); //(0)1-4
		assertEquals(1, p1);
		int p2 = m.allocate(2); //(5)6-7
		assertEquals(6, p2);
		m.deallocate(p1);
		m.deallocate(p2);
		int p3 = m.allocate(9); //(0)1-9
		assertEquals(1, p3);
	}
	
	@Before
	public void createHeap() {
		m = new HeapManager(new int[10]);
	}

}