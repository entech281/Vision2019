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
            tt.startTimer("test1");
            Thread.sleep(10);
            tt.startTimer("test2");
            Thread.sleep(5);
            tt.endTimer("test2");
            tt.endTimer("test1");
        }
        
        System.out.println(tt + "");
    }
}
