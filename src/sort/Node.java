/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author bendaccache
 */
public class Node implements Cloneable, Serializable {

    private Node parent = null;
    private ArrayList<Node> childrens;
    private int support = 0;
    private int support2 = 0;
    private String id = null;
    private int position = 0;
    private boolean containsTerm = false;
    private boolean containsTerm2 = false;
    private boolean isDirectParent = false;

    public Node(String id, Node parent, int uniqueId) {
        childrens = new ArrayList<Node>();
        this.id = id;
        this.parent = parent;
        this.position = uniqueId;
        support = 0;
        support2 = 0;
        containsTerm =false;
        containsTerm2 = false;
    }

    public Node(String id, int posid) {
        childrens = new ArrayList<Node>();
        this.id = id;
        this.position = posid;
    }

    public void addChild(String ident, int uniqueId) {
        childrens.add(new Node(ident, this, uniqueId));
    }

    public void addChild(Node n){
        childrens.add(n);
    }

    public void addChild(ArrayList<Node> arr) {
        for (int i = 0; i < arr.size(); i++) {
            childrens.add(arr.get(i));
        }
    }

    public ArrayList getChildren() {
        return childrens;
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public Node getChild(String id) {
        Iterator<Node> it = childrens.iterator();
        while (it.hasNext()) {
            Node next = it.next();
            if (next.getId().equals(id))
                return next;
        }
        return null;
    }

    public void incrementSupport() {
        System.out.println("incrementing support of "+getId());
        support++;
    }

    public void incrementSupport2(){
        support2++;
    }

    public int getSupport() {
        return support;
    }

    public int getSupport2() {
        return support2;
    }
    public void removeChild(String id) {
        Iterator<Node> it = childrens.iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(id)) {
                it.remove();
                break;
            }
        }
    }

    public int deleteChildrens() {
        int size =  childrens.size();
        childrens.clear();
        return size;
    }

    public Node getParent() {
        return parent;
    }

    public int deleteChild(Node node) {
        System.out.println("removing child " +node.getId() +"  of parent "+node.getParent().getId());
        int size = childrens.size();
        childrens.remove(node);
        return size;
    }

    public void markParents() {
        containsTerm = true;
        if (getParent() != null)
            getParent().markParents();
    }

    public void markParent2() {
        this.containsTerm2 = true;
        System.out.println("MarkingParent "+this.getId()+" id "+getPosition());
        if (getParent() != null){
            this. getParent().markParent2();
            
        }
    }

    public boolean isMarked() {
        return containsTerm;
    }

    public boolean isMarked2() {
        return containsTerm2;
    }

    public void markDirectParent() {
        getParent().isDirectParent=true;
    }

    public boolean isDirectParent() {
        return isDirectParent;
    }


   public void setChildrens(ArrayList<Node> cloneArr) {
        childrens.clear();
        childrens = cloneArr;
   }


   public Node getClone(){
        try {
            return (Node) this.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
}
