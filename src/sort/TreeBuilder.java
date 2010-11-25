package sort;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bendaccache
 */
public class TreeBuilder {

    private File f;
    private FPTree tree = new FPTree();
    private int numberOfItems = 0;
    private String term;
    private int support = 0;

    public TreeBuilder(File f) {
        this.f = f;
        new File("results.dat").delete();
    }

    public void BuildTree(int support) {
        this.support=support;
        try {
            BufferedReader br = createInputStream();
            String str = br.readLine();
            while (str!=null) {
                StringTokenizer stk = new StringTokenizer(str);
                String[] strArray = convertToArray(stk);
                numberOfItems += strArray.length;
                addToTree(strArray);
                str = br.readLine();
            }
            tree.setMinSupport(support);
        } catch (IOException ex) {
            Logger.getLogger(TreeBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showOnScreen(FPTree tree) {
        System.out.println(tree.getRootNode().getId());
        ArrayList ar = tree.getRootNode().getChildren();
        Iterator<Node> it = ar.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            System.out.print(n.getId() + " ");
            System.out.print(n.getSupport() + "   ");
        }
        System.out.println();
        it = ar.iterator();
        while (it.hasNext()) {
            showChildren(it.next().getChildren());
        }
        System.out.println();
        it = ar.iterator();
        while (it.hasNext()) {
            Iterator<Node> it2 = it.next().getChildren().iterator();
            while (it2.hasNext()) {
                showChildren(it2.next().getChildren());
            }

        }
        System.out.println();
    }

    /**
     * Find all the branches containing the String
     * Cut the branches
     * 
     * @param str
     */
    public void BuildConditionnalTree(String str) {
        term = str;
        locateAndDeleteTreeItems(str);
    }

    private void addToTree(String[] s) {
        Node trn = tree.getRootNode().getChild(s[0]);
        if (trn == null) {
            tree.addChildOfRoot(s[0]);
            trn = tree.getRootNode().getChild(s[0]);
        }
        if (s.length > 1)
            recursiveAdd(s, 1, tree.getRootNode().getChild(s[0]).getPosition());

    }

    public ArrayList[] calculateTrees() {
        HashMap<String, ArrayList<Node>> map = tree.getHashMapOfItems();//Contains all the items in the tree
        Iterator<ArrayList<Node>> it2 = map.values().iterator();
        Iterator<String> it = map.keySet().iterator();
        ArrayList[] allResults = new ArrayList[map.size()];
        FPTree localTree = new FPTree();
        
        serializeObject(tree);
        localTree = duplicateTree();
        int i = 0;
        //FPTree localTree = readObject();
        while (it2.hasNext()) {
            String item = it.next();
            System.out.println("Building conditionnal FP-Tree for item " + item);
            ArrayList<Node> a = it2.next();
            ArrayList<String[]> res= null;
            try {
                localTree.buildFPConditionalTree(a);
                res= localTree.getFrequentItemset(item, term);
                System.out.println("Conditionnal FP-Tree for item " + item+", "+term);
                showOnScreen(localTree);
                System.out.println("Frequent itemset for item " + item+", "+term);
                showRes(res, item, term);
                writeResultsToFile(res, item, term);
                
            } catch (Exception ex) {System.out.println("Not a tail term ");}
            localTree= null;
            System.gc();
            localTree = duplicateTree();
            System.gc();
            showOnScreen(localTree);
            System.out.println("allRes.size() = " + allResults.length);
            if(res!=null){
                allResults[i] = res;
                i++;
            }
            

        }
        //allResults = cleanArray(allResults);
        return allResults;
}

    private ArrayList[] cleanArray(ArrayList[] arr ) {
        ArrayList[] cleaned = new ArrayList[arr.length];
        int j =0;
        for(int i = arr.length-1;i>0;i-- )
            if(arr[i]!=null){
                cleaned[j] = arr[i];
                j++;
            }
        ArrayList[] c= new ArrayList[j+1];
        System.arraycopy(cleaned, 0, c, 0, j+1);
        return c;


    }



    private String[] convertToArray(StringTokenizer stk) {
        String[] strArray = new String[stk.countTokens()];
        int i = 0;
        while (stk.hasMoreTokens()) {
            strArray[i] = stk.nextToken();
            i++;
        }
        return strArray;
    }

    private BufferedReader createInputStream() {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TreeBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return br;
    }

    private FPTree duplicateTree() {
       BuildTree(support);
       BuildConditionnalTree(term);
        return tree;

    }

    private void locateAndDeleteTreeItems(String str) {
        tree.updateBranchesContaining(str);
    }

    private FPTree readObject() {
        FPTree readTree= null;
        try {
            FileInputStream fis = null;
            ObjectInputStream in = null;
            fis = new FileInputStream("temp");
            in = new ObjectInputStream(fis);
            readTree =(FPTree) in.readObject();
            in.close();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TreeBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TreeBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return readTree;
    }

    private void recursiveAdd(String[] s, int i, int id) {
        try {
            Node parent = tree.getNodeById(id);
            Node n = parent.getChild(s[i]);
            int pos = 0;
            if (n != null)
                pos = n.getPosition();
            else {
                pos = tree.getUniqueId();
                parent.addChild(s[i], pos);
            }
            if (s.length - 1 > i++)
                recursiveAdd(s, i++, pos);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
    }

    public FPTree getTree() {
        return tree;
    }

    private void serializeObject(FPTree tree) {
        try {
            File file = new File("temp");
            FileOutputStream fos = null;
            ObjectOutputStream out = null;
            file.deleteOnExit();
            fos = new FileOutputStream(file);

            out = new ObjectOutputStream(fos);
            out.writeObject(tree);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(TreeBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showChildren(ArrayList arr) {
        Iterator<Node> it = arr.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            System.out.print(n.getId() + " ");
            System.out.print(n.getSupport() + "  ");
        }
        System.out.print("|");
    }

    private void showRes(ArrayList<String[]> res, String item , String term) {
        for(int i = 0; i<res.size();i++){
            String[] tab = res.get(i);
            for(int j = 0 ; j<tab.length;j++){
                    System.out.print(tab[j]+", ");
            }
            System.out.println(item+", "+term);
        }if(res.size()==0)
             System.out.println(item+", "+term);
    }

    public void writeResultsToFile(ArrayList<String[]> res, String item , String term){
        try {
            FileWriter out = null;
            out = new FileWriter("results.dat",true);
            for (int i = 0; i < res.size(); i++) {
                    String[] tab = res.get(i);
                    for (int j = 0; j < tab.length; j++) {
                        out.write(tab[j] + " ");
                    }
                    out.write(item + " " + term + "\n");
                
            }
            if (res.size() == 0)
                out.write(item + " " + term + "\n");
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(TreeBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }






        
    

    private void duplicateTree(ArrayList<Node> arr, Node newRoot, int position ) {
        Iterator<Node> it = arr.iterator();
        Node current = null;
        int i = 0;
        while (it.hasNext()) {
            Node clone = it.next();
            newRoot.addChild(clone.getClone());
            if(i == position)
                current =clone.getClone() ;
            i++;
        }
        it= null;
        it = arr.iterator();
        while (it.hasNext()) {
            Node tempNode = it.next();
            if (!tempNode.getChildren().isEmpty()) {
                duplicateTree(tempNode.getChildren(), current, ++position);
                i++;
            }
        }
    }
}
