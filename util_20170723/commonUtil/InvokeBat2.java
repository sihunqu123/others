package util.commonUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * shut zhe window finally
 * @author ACE
 *
 */
public class InvokeBat2 {
    public static void runbat(String batName) {
        try {
            Process ps = Runtime.getRuntime().exec(batName);
            InputStream in = ps.getInputStream();
            int c;
            while ((c = in.read()) != -1) {
                System.out.print(c);// 如果你不需要看输出，这行可以注销掉
            }
            in.close();
            ps.waitFor();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("child thread done");
    }

    public static void main(String[] args) {
        InvokeBat2 test1 = new InvokeBat2();
        //String batName = "F:\\database_backup\\ngx_backup\\backup_ngx.bat";
       // String batName = "D:\\Program\" \"Files\\tomcat7\\bin\\startup.bat";
        String batName = "D:\\tomcat7\\bin\\restart.bat";
        test1.runbat(batName);
        System.out.println("main thread");
    }
}