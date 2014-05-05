import java.util.*;

public class AVLTree
{
    class AVLNode {
		AVLNode parent, left, right;
		Entry _csv;
		Shelf _objs;

		AVLNode () {
			_csv = null;
			_objs = null;
		}

		AVLNode(Entry csv, ArrayList<Entry> obj ) {
		    _csv = csv;
		    _objs = new Shelf();
		    _objs.add(obj);
		}

		public int compareTo(AVLNode other) {
		    return _csv.compareTo(other._csv);
		}

		void addObject(ArrayList<Entry> obj) {
		    _objs.add(obj);
		}
    }

    private AVLNode radix;
    private AVLNode root;

    public AVLTree() { 
		radix = new AVLNode();
		root = null;
		radix.right = root;
    }

    public void print() {
		if (root != null)
		    print(root,"");
    }

    public void print(AVLNode n, String s) {
		if (n == null)
		    return;
		
		print(n.right, s+"\t");
		System.out.print(s + "<" + n._csv + ">\n");
		print(n.left, s+"\t");
    }

    public AVLNode getRoot() { return root; }

    public AVLNode search(Entry csv) { return search(csv,root); }
    public AVLNode search(Entry csv, AVLNode n) {
		if (n == null || n._csv.equals(csv))
		    return n;
		if (n._csv.compareTo(csv) < 0)
		    return search(csv, n.right);
		else
		    return search(csv, n.left);
    }

    public AVLNode tsearch(Entry csv) {
		return tsearch(csv,root);
    }

    public AVLNode tsearch(Entry csv, AVLNode n) {
		if (n == null)
		    return radix;

		if (n._csv.equals(csv))
		    return n.parent;

		if (n._csv.compareTo(csv) < 0) {
		    if (n.right == null)
			return n;
		    return tsearch(csv, n.right);
		}
		else {
		    if (n.left == null)
			return n;
		    return tsearch(csv, n.left);
		}
    }

    public void addEntry( Entry csv, ArrayList<Entry> obj ) {
		if (root == null) {
		    root = new AVLNode(csv, obj);
		    root.parent = radix;
		    radix.right = root;
		    return;
		}

		AVLNode p = tsearch(csv);
		AVLNode n = search(csv);

		if (n == null) {
		    n = new AVLNode(csv, obj);
		    n.parent = p;
		    if (n.compareTo(p) < 0)
				p.left = n;
		    else
				p.right = n;
		    rebalance(p);
		} else
		    n.addObject(obj);
    }

    public Shelf getEqual(Shelf pool, Entry val, AVLNode n) {
		if (n == null)
		    return pool;

		int comp = val.compareTo(n._csv);

		if (comp < 0)
		    return getEqual(pool, val, n.left);
		if (comp > 0)
		    return getEqual(pool, val, n.right);
		else {
		    pool.addAll(n._objs);
			return pool;
		}
    }

    public Shelf getAll(Shelf pool, AVLNode n) {
		if (n == null)
			return pool;

		pool = getAll(pool, n.left);
		pool.addAll(n._objs);
		pool = getAll(pool, n.right);
		return pool;
    }

	public Shelf getAllAsc(Shelf pool, AVLNode n) {
	    if (n == null)
			return pool;

		pool = getAll(pool, n.left);
		pool.addAll(n._objs);
		pool = getAll(pool, n.right);
		return pool;
	}

	public Shelf getAllDesc(Shelf pool, AVLNode n) {
	    if (n == null)
			return pool;

		pool = getAll(pool, n.right);
		pool.addAll(n._objs);
		pool = getAll(pool, n.left);
		return pool;
	}

	public Shelf getLessThan(Shelf pool, Entry val, AVLNode n) {
	    if (n == null)
	    	return pool;

		int comp = n._csv.compareTo(val);
		
		if (comp > 0)
			return getLessThan(pool, val, n.left);
	    if (comp <= 0)
			pool = getAllAsc(pool, n.left);
	    if (comp < 0) {
			pool.addAll(n._objs);
			pool = getLessThan(pool, val, n.right);
	    }
	    return pool;
	}

	public Shelf getMoreThan(Shelf pool, Entry val, AVLNode n) {
	    if (n == null)
	    	return pool;

		int comp = n._csv.compareTo(val);
		
		if (comp < 0)
			return getMoreThan(pool, val, n.right);
	    if (comp >= 0)
			pool = getAllDesc(pool, n.right);
	    if (comp > 0) {
			pool.addAll(n._objs);
			pool = getMoreThan(pool, val, n.left);
	    }
	    return pool;
	}

	public Shelf getLessOrEqual(Shelf pool, Entry val, AVLNode n) {
	    if (n == null)
	    	return pool;

		int comp = n._csv.compareTo(val);
		
		if (comp > 0)
			return getLessOrEqual(pool, val, n.left);
		if (comp < 0)
			pool = getLessOrEqual(pool, val, n.right);
	    if (comp <= 0) {
			pool = getAllAsc(pool, n.left);
			pool.addAll(n._objs);
	    }
	    return pool;
	}

	public Shelf getMoreOrEqual(Shelf pool, Entry val, AVLNode n) {
	    if (n == null)
	    	return pool;

		int comp = n._csv.compareTo(val);
		
		if (comp < 0)
			return getMoreThan(pool, val, n.right);
		if (comp > 0)
			pool = getMoreThan(pool, val, n.left);
	    if (comp >= 0) {
			pool = getAllDesc(pool, n.right);
			pool.addAll(n._objs);
		}
	    
	    return pool;
	}

	public Shelf getNotEqual(Shelf pool, Entry val, AVLNode n) {
	    if (n == null)
	    	return pool;

	    
		pool = getNotEqual(pool, val, n.left);   
	    if (!(val.equals(n._csv))) pool.addAll(n._objs);
		pool = getNotEqual(pool, val, n.right);

		return pool;

	}

    protected int height(AVLNode node) {
		if (node == null) {
		    return -1;
		}
		int l = height(node.left);
		int r = height(node.right);
		return (1 + ((l > r) ? l : r));
    }

    protected int balance(AVLNode node) {
		if (node == null)
	 	   return 0;
		return (height(node.left) - height(node.right));
    }

    protected void rebalance(AVLNode node) {
		AVLNode link;

		for (AVLNode n = node; n != radix; ) {
		    AVLNode head = n;
		    n = n.parent;

		    int bal = balance(head);
		    if (bal > 1) { // Left Case
				link = head.left;
				if (balance(link) < 0) {
			   		rotate_left(link); // Reduce L-R to L-L
				}
				rotate_right(head);
		    }
		    if (bal < -1) { // Right Case
				link = head.right;
				if (balance(link) > 0) {
			    	rotate_right(link); // Reduce R-L to R-R
				}
				rotate_left(head);
		    }
		}
		root = radix.right;
    }

    protected void rotate_right(AVLNode node) {
		AVLNode stem, pivot, wedge, ground;
		stem = node;

		if ((pivot = stem.left) != null) { 
		    wedge = pivot.right;
		    stem.left = wedge;
		    if ((wedge) != null)
				wedge.parent = stem;
		    pivot.right = stem;

			ground = stem.parent;
			if (stem.equals(ground.right))
		    	ground.right = pivot;
			if (stem.equals(ground.left))
		   		ground.left = pivot;
			pivot.parent = ground;
	    }
		stem.parent = pivot;
    }
 
    protected void rotate_left(AVLNode node) {
		AVLNode stem, pivot, wedge, ground;
		stem = node;

		if ((pivot = stem.right) != null) { 
		    wedge = pivot.left;
		    stem.right = wedge;
		    if ((wedge) != null)
				wedge.parent = stem;
		    pivot.left = stem;

			ground = stem.parent;
			if (stem.equals(ground.right))
			    ground.right = pivot;
			if (stem.equals(ground.left))
			    ground.left = pivot;
			pivot.parent = ground;
		}
		stem.parent = pivot;
    }
}
