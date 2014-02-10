import java.util.*;

public class AVLTree {
    public AVLNode _root;
    public AVLNode _head;

    public AVLTree() { 
	_root = null;
    }

    public void print() {
	if (_root != null) {
	    _root.print(0);
	}
    }

    public int getDepth(AVLNode node) {
	AVLNode head = node;
	int dep = 0;
	while (head != _root) {
	    ++dep;
	    head = head.parent;
	}
	return dep;
    }




    public void addEntry( Entry csv, ArrayList<Entry> obj  ) {
	if (_root == null) {
	    _root = new AVLNode(csv, obj);
	    return;
	}

	_head = _root;
	int compvalue;

	while (_head != null) {
	    compvalue = csv.compareTo(_head._csv);

	    if (compvalue == 0) {
		_head.addObject( obj );
		return;
	    }

	    if (compvalue > 0) {
		if (_head.rightChild == null) {
		    _head.rightChild = new AVLNode(csv, obj);
		    _head.rightChild.parent = _head;
  		    this.rebalance(_head.parent);
		    return;
		}
		_head = _head.rightChild;
		continue;
	    }

	    if (compvalue < 0)  {
		if (_head.leftChild == null) {
		    _head.leftChild = new AVLNode(csv, obj);
		    _head.leftChild.parent = _head;
  		    this.rebalance(_head.parent);
		    return;
		}
		_head = _head.leftChild;
		continue;
	    }
	}
    }

    public ArrayList<ArrayList<Entry>> getEqual(Entry val) {
	int compvalue;
	_head = _root;

	while ( _head != null ) {
	    compvalue = val.compareTo(_head._csv);

	    if (compvalue < 0)
		_head = _head.leftChild;
	    if (compvalue == 0)
		return _head._objs;
	    if (compvalue > 0)
		_head = _head.rightChild;
	}
	return null;
    }

    public ArrayList<ArrayList<Entry>> getMoreThan(Entry val) {
	ArrayList<ArrayList<Entry>> pool = new ArrayList<ArrayList<Entry>>();
	_root.getMore(pool, val);
	return pool;
    }

    public ArrayList<ArrayList<Entry>> getLessThan(Entry val) {
	ArrayList<ArrayList<Entry>> pool = new ArrayList<ArrayList<Entry>>();
	_root.getLess(pool, val);
	return pool;
    }

    public ArrayList<ArrayList<Entry>> getMoreOrEqual(Entry val) {
	ArrayList<ArrayList<Entry>> pool = new ArrayList<ArrayList<Entry>>();
	_root.getMoreOrEqual(pool, val);
	return pool;
    }

    public ArrayList<ArrayList<Entry>> getLessOrEqual(Entry val) {
	ArrayList<ArrayList<Entry>> pool = new ArrayList<ArrayList<Entry>>();
	_root.getLessOrEqual(pool, val);
	return pool;
    }

    public ArrayList<ArrayList<Entry>> getAll() {
	ArrayList<ArrayList<Entry>> pool = new ArrayList<ArrayList<Entry>>();
	_root.getAll(pool);
	return pool;
    }

    public ArrayList<ArrayList<Entry>> getNotEqual(Entry val) {
	ArrayList<ArrayList<Entry>> pool = new ArrayList<ArrayList<Entry>>();
	_root.getNotEqual(pool, val);
	return pool;
    }
    public ArrayList<ArrayList<Entry>> getAll(AVLNode head) {
	ArrayList<ArrayList<Entry>> pool = new ArrayList<ArrayList<Entry>>();
	head.getAll(pool);
	return pool;
    }

    protected int height(AVLNode head) {
	int l = l_height(head);
	int r = r_height(head);
	return (1 + ((l > r) ? l : r));
    }

    protected int l_height(AVLNode head) {
	return (head.leftChild != null) ? height(head.leftChild) : 0;
    } 

    protected int r_height(AVLNode head) {
	return (head.rightChild != null) ? height(head.rightChild) : 0;
    }

    protected int balance(AVLNode head) {
	return (l_height(head) - r_height(head));
    }

    protected void rebalance(AVLNode node) {
	AVLNode link;

	for (_head = node; _head != null; ) {
	    int dep = getDepth(_head);
	    int bal = balance(_head);
	    if (bal > 1) { // Left Case
		link = _head.leftChild;
		if (balance(link) < 0) {
		    rotate_left(link); // Reduce L-R to L-L
		}
		rotate_right(_head);
	    }
	    if (bal < -1) { // Right Case
		link = _head.rightChild;
		if (balance(link) > 0) {
		    rotate_right(link); // Reduce R-L to R-R
		}
		rotate_left(_head);
	    }
	    if (dep == 0) {
		break;
	    }
	    while (getDepth(_head) > dep - 1) { _head = _head.parent; } 
	    continue;
	}
	return;
    }

    protected void rotate_right(AVLNode node) {
	AVLNode root, pivot, wedge, ground;

	root = node;
	if ((pivot = root.leftChild) != null) { 
	    wedge = pivot.rightChild;
	    root.leftChild = wedge;
	    if ((wedge) != null)
		wedge.parent = root;
	    pivot.rightChild = root;
	    if (root.equals(_root)) {
		_root = pivot;
	    } else {
		ground = root.parent;
		if (root.equals(ground.rightChild))
		    ground.rightChild = pivot;
		if (root.equals(ground.leftChild))
		    ground.leftChild = pivot;
		pivot.parent = ground;
	    }
	    root.parent = pivot;
	}
    }
 
    protected void rotate_left(AVLNode node) {
	AVLNode root, pivot, wedge, ground;

	root = node;
	if ((pivot = root.rightChild) != null) { 
	    wedge = pivot.leftChild;
	    root.rightChild = wedge;
	    if (wedge != null)
		wedge.parent = root;
	    pivot.leftChild = root;
	    if (root.equals(_root)) {
		_root = pivot;
	    } else {
		ground = root.parent;
		if (root.equals(ground.rightChild))
		    ground.rightChild = pivot;
		if (root.equals(ground.leftChild))
		    ground.leftChild = pivot;
		pivot.parent = ground;
	    }
	    root.parent = pivot;
	}
    }

}

class AVLNode {
    public AVLNode parent = null;
    public AVLNode leftChild = null;
    public AVLNode rightChild = null;

    public Entry _csv;
    public ArrayList<ArrayList<Entry>> _objs;

    public AVLNode(Entry csv, ArrayList<Entry> obj ) {
	_csv = csv;
	_objs = new ArrayList<ArrayList<Entry>>();
	_objs.add(obj);
    }

    public void print(int depth) {
	if (rightChild != null) {
	    rightChild.print(depth + 1);
	}
	for (int i = 0; i < depth; ++i) {
	    System.out.print("\t");
	}
	System.out.print("<" + _csv + ">\n");
	if (leftChild != null) {
	    leftChild.print(depth + 1);
	}
    }

    public void getAll(ArrayList<ArrayList<Entry>> pool) {
	if (leftChild != null) {
	    leftChild.getAll(pool);
	}
	if (rightChild != null) {
	    rightChild.getAll(pool);
	}
	pool.addAll(this._objs);
    }


    public void getLess(ArrayList<ArrayList<Entry>> pool, Entry val) {
	int comp = _csv.compareTo(val);
	if (comp <= 0) {
	    if (leftChild != null) {
		leftChild.getAll(pool);
	    }
	}
	if (comp > 0) {
	    if (leftChild != null) {
		leftChild.getLess(pool, val);
	    }
	}
	if (comp < 0) {
	    pool.addAll(this._objs);
	    if (rightChild != null) {
		rightChild.getLess(pool, val);
	    }
	}
    }

    public void getMore(ArrayList<ArrayList<Entry>> pool, Entry val) {
	int comp = _csv.compareTo(val);
	if (comp >= 0) {
	    if (rightChild != null) {
		rightChild.getAll(pool);
	    }
	}
	if (comp < 0) {
	    if (rightChild != null) {
		rightChild.getMore(pool, val);
	    }
	}
	if (comp > 0) {
	    pool.addAll(this._objs);
	    if (leftChild != null) {
		leftChild.getMore(pool, val);
	    }
	}
    }

    public void getLessOrEqual(ArrayList<ArrayList<Entry>> pool, Entry val) {
	int comp = _csv.compareTo(val);
	if (comp <= 0) {
	    if (leftChild != null) {
		leftChild.getAll(pool);
	    }
	    pool.addAll(this._objs);
	}
	if (comp > 0) {
	    if (leftChild != null) {
		leftChild.getLessOrEqual(pool, val);
	    }
	}
	if (comp < 0) {
	    if (rightChild != null) {
		rightChild.getLessOrEqual(pool, val);
	    }
	}
    }

    public void getMoreOrEqual(ArrayList<ArrayList<Entry>> pool, Entry val) {
	int comp = _csv.compareTo(val);
	if (comp >= 0) {
	    if (rightChild != null) {
		rightChild.getAll(pool);
	    }
	    pool.addAll(this._objs);
	}
	if (comp < 0) {
	    if (rightChild != null) {
		rightChild.getMoreOrEqual(pool, val);
	    }
	}
	if (comp > 0) {
	    if (leftChild != null) {
		leftChild.getMoreOrEqual(pool, val);
	    }
	}
    }

    public void getNotEqual(ArrayList<ArrayList<Entry>> pool, Entry val) {
	if (leftChild != null) {
	    leftChild.getNotEqual(pool, val);
	}
	if (!(val.equals(_csv))) {
	    pool.addAll(this._objs);
	}
	if (rightChild != null) {
	    rightChild.getNotEqual(pool, val);
	}
    }




    public int compareTo(AVLNode other) {
	return _csv.compareTo(other._csv);
    }
    public int relation() {
	return (parent != null) ? (this.compareTo(parent)) : 0;
    }

    public void addObject(ArrayList<Entry> obj) {
	_objs.add(obj);
    }
}
