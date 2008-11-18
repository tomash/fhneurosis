package thesis;

import java.util.*;

public class QueueTest
{
    public static void main(String[] args)
            throws InterruptedException
    {
        int time = Integer.parseInt(args[0]);
        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(111);
        for (int i = time; i >= 0; i--)
            queue.add(i);

        queue.add(222);
        while (!queue.isEmpty())
        {
            System.out.println(queue.removeLast());
            Thread.sleep(1000);
        }
    }
}
