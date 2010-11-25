/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sort.TreeBuilder;
import static org.junit.Assert.*;

/**
 *
 * @author bendaccache
 */
public class FPTest {

    public FPTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void test() throws IOException {
            File f = new File("/Users/bendaccache/Documents/Sort/test.txt/");
            String expected = "Car Id\nPerson name last Id\nPerson name first Id\nPerson name Id";
            System.out.println(f.getAbsolutePath());
            TreeBuilder builder = new TreeBuilder(f);
            builder.BuildTree(0);
            builder.showOnScreen(builder.getTree());
            builder.BuildConditionnalTree("Id");
            builder.showOnScreen(builder.getTree());
            ArrayList[] res = builder.calculateTrees();
            File results = new File("results.dat");
            FileInputStream in= new FileInputStream(results) ;
            byte[] read = new byte[(int)results.length()];
            in.read(read);
            in.close();
            assertSame(expected, read.toString());


     }

     @Test public void testFrequents() {
         /**
          * load a file of items
          * ask for the frequent item for XXX
          * check that the results are as expected
          * get the frequent items
          * check that item 1 is XX1
          * check that item 2 is XX2
          * check that the total of items Z
          * check that ...
          */
     }

}