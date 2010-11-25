/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author bendaccache
 */
public class FPTree implements Serializable {

    private Node rootTerm = null;
    private int uniqueId = 0;
    private ArrayList<Node> tailTerm = new ArrayList<Node>();
    private HashMap<String, ArrayList<Node>> map = new HashMap<String, ArrayList<Node>>();
    private HashMap<String, ArrayList<Node>> map2 = new HashMap<String, ArrayList<Node>>();
    private int support = 0;

    public FPTree() {
        rootTerm = null;
        uniqueId = 0;
        tailTerm.clear();
        map.clear();
        map2.clear();
        support = 0;
        rootTerm = new Node("root", uniqueId);
        uniqueId++;
    }

    public void addChildOfRoot(String id) {
        rootTerm.addChild(id, uniqueId);
        uniqueId++;
    }

    public Node getChild(String id) {
        return recursiveCheck(rootTerm.getChildren(), id);
    }

    public Node getNodeById(int nid) {
        return recursiveCheck(rootTerm.getChildren(), nid);
    }

    public void updateBranchesContaining(String str) {
        recursiveDeletion(rootTerm.getChildren(), str);
        deleteUnneededBranches(rootTerm.getChildren());
        //getTailNodes(rootTerm.getChildren());
        //cleanArray();
        //updateSupport(tailTerm);
        getAllItems(rootTerm.getChildren());
        removeUnfrequentItems();
    }

    public void setMinSupport(int l) {
        System.out.println("minsupport = " + l);
        this.support = l;
    }

    public HashMap<String, ArrayList<Node>> getHashMapOfItems() {
        return map;
    }

    public void buildFPConditionalTree(ArrayList<Node> arr) throws Exception {
        Iterator<Node> it = arr.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            System.out.println("Processing item " + n.getId());
            if (n.isDirectParent()) {//direct connection with searched term
                n.markParent2();
                n.getParent().deleteChild(n);
               

            } else{
                removeBranch(n);
                throw new Exception();
             }
            n=null;
            System.gc();
        }
        deleteUnneededBranches2(rootTerm.getChildren());
    }


    public ArrayList<String[]> getFrequentItemset(String item1, String item2) {
        tailTerm.clear();
        getTailNodes(rootTerm.getChildren());
        cleanArray(tailTerm);
        ArrayList<Node>  term;
        ArrayList<String[]> res = new ArrayList<String[]>();
        Iterator<Node> it = tailTerm.iterator();
        while(it.hasNext()){
            term = new ArrayList<Node>() ;
            Node n = it.next();
            term.add(n);
            while(n.getParent().getPosition()!=0){
                term.add(n.getParent());
                n=n.getParent();
            }
            String[] tab = new String[term.size()];
            Iterator<Node> it2 = term.iterator();
            int i = term.size()-1;
            while(it2.hasNext()){
                tab[i] = it2.next().getId();
                i--;
            }
            res.add(tab);
        }
        
        return res;
    }


    private void cleanArray(ArrayList<Node> tailTerm) {
        for(int i = 0; i<tailTerm.size();i++)
            if(tailTerm.get(i)==null)
                tailTerm.remove(i);
    }

    private void deleteUnneededBranches2(ArrayList arr) {
        Iterator<Node> it = arr.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            if (!n.isMarked2()){
                System.out.println("Removing item "+n.getId() +" at position "+n.getPosition());
                it.remove();
            }
                
        }
        it = arr.iterator();
        while (it.hasNext()) {
            Node next = it.next();
            if (!next.getChildren().isEmpty())
                deleteUnneededBranches2(next.getChildren());
        }
    }

    private void getAllItems(ArrayList arr) {
        Iterator<Node> it = arr.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            if (map.containsKey(n.getId())) {
                ArrayList<Node> arrN = map.get(n.getId());
                arrN.add(n);
                map.put(n.getId(), arrN);
            } else {
                ArrayList<Node> arrN = new ArrayList<Node>();
                arrN.add(n);
                map.put(n.getId(), arrN);
            }
        }
        it = arr.iterator();
        while (it.hasNext()) {
            Node next = it.next();
            if (!next.getChildren().isEmpty())
                getAllItems(next.getChildren());
        }
    }

    private Node getTailNodes(ArrayList<Node> arr) {
        Iterator<Node> it = arr.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            if (!n.getChildren().isEmpty())
                tailTerm.add(getTailNodes(n.getChildren()));
            else{
                tailTerm.add(n);
             }
        }
        return null;
    }

    private void deleteAllChilds(Node next) {
        next.deleteChildrens();
        System.out.println("Deleting childs of " + next.getId() + " at position " + next.getPosition());
    }

    private void deleteUnneededBranches(ArrayList<Node> arr) {
        Iterator<Node> it = arr.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            if (!n.isMarked())
                it.remove();
        }
        it = arr.iterator();
        while (it.hasNext()) {
            Node next = it.next();
            if (!next.getChildren().isEmpty())
                deleteUnneededBranches(next.getChildren());
        }

    }


   
    /**
     * delete the children of the term that we search for
     * @param children Array of children
     * @param term term we are looking for
     */
    private void recursiveDeletion(ArrayList children, String term) {
        Iterator<Node> it = children.iterator();
        while (it.hasNext()) {
            Node next = it.next();
            if (next.getId().equals(term)) {
                deleteAllChilds(next);
                next.markParents();
                next.markDirectParent();
                updateSupport(next);
                it.remove();

            }
        }
        it = children.iterator();
        while (it.hasNext()) {
            Node next = it.next();
            if (!next.getChildren().isEmpty())
                recursiveDeletion(next.getChildren(), term);
        }

    }

    private Node recursiveCheck(ArrayList<Node> children, int nid) {
        Iterator<Node> it = children.iterator();
        Node res = null;
        while (it.hasNext()) {
            Node next = it.next();
            if (next.getPosition() == nid)
                return next;
        }
        it = children.iterator();
        while (it.hasNext()) {
            res = recursiveCheck(it.next().getChildren(), nid);
            if (res != null)
                return res;
        }
        return res;
    }

    private Node recursiveCheck(ArrayList<Node> children, String id) {
        Iterator<Node> it = children.iterator();
        Node res = null;
        while (it.hasNext()) {
            Node next = it.next();
            if (next.getId().equals(id))
                return next;
        }
        it = children.iterator();
        while (it.hasNext()) {
            res = recursiveCheck(it.next().getChildren(), id);
            if (res != null)
                return res;
        }
        return res;
    }

    public Node getRootNode() {
        return rootTerm;
    }

    public int getUniqueId() {
        return uniqueId++;
    }

    private void removeBranch(Node n) {
        while (n != null && n.getChildren().size()>1 ) {
            n.getParent().deleteChild(n);
            n = n.getParent();
        }
    }

    private void removeUnfrequentItems() {
        Iterator<ArrayList<Node>> it = map.values().iterator();
        while (it.hasNext()) {
            long sup = 0;
            ArrayList<Node> list = it.next();
            for (int i = 0; i < list.size(); i++) {
                sup += list.get(i).getSupport();
            }
            if (sup < support) {
                for (int i = 0; i < list.size(); i++) {
                    Node n = list.get(i);
                    rootTerm.addChild(n.getChildren());
                    n.getParent().removeChild(n.getId());
                }
                it.remove();
            }
        }

    }

    private void updateSupport(ArrayList arr) {
        Iterator<Node> it = arr.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            n.incrementSupport();
            if (n.getParent() != null)
                updateSupport(n.getParent());
        }
    }

    private void updateSupport(Node parent) {
        parent.incrementSupport();
        if (parent.getParent() != null)
            updateSupport(parent.getParent());
    }

}
