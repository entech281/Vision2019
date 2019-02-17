package frc.timers;

import org.junit.Test;

/**
 *
 * @author dcowden
 */
public class TestTimeTracker {
    
    @Test
    public void testTimeTracker() throws Exception{
        TimeTracker tt = new TimeTracker();
        for ( int i=0;i<200;i++){
            tt.start("test1");
            Thread.sleep(10);
            tt.start("test2");
            Thread.sleep(5);
            tt.end("test2");
            tt.end("test1");
        }
        
        System.out.println(tt + "");
    }
}
