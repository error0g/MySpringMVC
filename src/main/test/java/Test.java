import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class Test {

   @org.junit.Test
    public void getPropertyTest()
    {

        File filePath=new File("E:\\MySpringMvc\\src\\main\\java\\cn\\error0\\");

        List<File> files= new ArrayList<>(Arrays.asList(filePath.listFiles()));
        for (File file:files)
        {
            if(file.isDirectory())
            {
                files.add(file);
            }
            else {
                if(file.getName().endsWith(".class"))
                {
                    System.out.println(file.getName());
                }
            }
        }
    }
}
