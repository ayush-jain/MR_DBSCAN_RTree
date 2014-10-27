

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/*
 * @param <T>
 *          the type of entry to store in this RTree.
 */

public class RTree<T>
{
  public enum SeedPicker { LINEAR, QUADRATIC }

  public final int maxEntries;
  public final int minEntries;
  public final int numDims;

  public final float[] pointDims;

  public final SeedPicker seedPicker;

  public Node root;

  public volatile int size;

  /**
   * Creates a new RTree.
   * 
   * @param maxEntries
   *          maximum number of entries per node
   * @param minEntries
   *          minimum number of entries per node (except for the root node)
   * @param numDims
   *          the number of dimensions of the RTree.
   */
  public RTree(int maxEntries, int minEntries, int numDims, SeedPicker seedPicker)
  {
    assert (minEntries <= (maxEntries / 2));
    this.numDims = numDims;
    this.maxEntries = maxEntries;
    this.minEntries = minEntries;
    this.seedPicker = seedPicker;
    pointDims = new float[numDims];
    root = buildRoot(true);
  }

  public RTree(int maxEntries, int minEntries, int numDims)
  {
    this(maxEntries, minEntries, numDims, SeedPicker.LINEAR);
  }

  public Node buildRoot(boolean asLeaf)
  {
    float[] initCoords = new float[numDims];
    float[] initDimensions = new float[numDims];
    for (int i = 0; i < this.numDims; i++)
    {
      initCoords[i] = (float) Math.sqrt(Float.MAX_VALUE);
      initDimensions[i] = -2.0f * (float) Math.sqrt(Float.MAX_VALUE);
    }
    return new Node(initCoords, initDimensions, asLeaf);
  }

  /**
   * Builds a new RTree using default parameters: maximum 50 entries per node
   * minimum 2 entries per node 2 dimensions
   */
  public RTree()
  {
    this(50, 2, 2, SeedPicker.LINEAR);
  }

  /**
   * @return the maximum number of entries per node
   */
  public int getMaxEntries()
  {
    return maxEntries;
  }

  /**
   * @return the minimum number of entries per node for all nodes except the
   *         root.
   */
  public int getMinEntries()
  {
    return minEntries;
  }

  /**
   * @return the number of dimensions of the tree
   */
  public int getNumDims()
  {
    return numDims;
  }

  /**
   * @return the number of items in this tree.
   */
  public int size()
  {
    return size;
  }

  /**
   * Searches the RTree for objects overlapping with the given rectangle.
   * 
   * @param coords
   *          the corner of the rectangle that is the lower bound of every
   *          dimension (eg. the top-left corner)
   * @param dimensions
   *          the dimensions of the rectangle.
   * @return a list of objects whose rectangles overlap with the given
   *         rectangle.
   */
  public List<Entry> search(float[] coords, float[] dimensions)
  {
    assert (coords.length == numDims);
    assert (dimensions.length == numDims);
    List<Entry> results = new ArrayList<Entry>();
    search(coords, dimensions, root, results);
    return results;
  }

  public void search(float[] coords, float[] dimensions, Node n,
      List<Entry> results)
  {
    if (n.leaf)
    {
      for (Node e : n.children)
      {
        if (isOverlap(coords, dimensions, e.coords, e.dimensions))
        {
          //results.add(((Entry) e).entry);
        	results.add((Entry) e);
        }
      }
    }
    else
    {
      for (Node c : n.children)
      {
        if (isOverlap(coords, dimensions, c.coords, c.dimensions))
        {
          search(coords, dimensions, c, results);
        }
      }
    }
  }

  /**
   * Deletes the entry associated with the given rectangle from the RTree
   * 
   * @param coords
   *          the corner of the rectangle that is the lower bound in every
   *          dimension
   * @param dimensions
   *          the dimensions of the rectangle
   * @param entry
   *          the entry to delete
   * @return true iff the entry was deleted from the RTree.
   */
  public boolean delete(float[] coords, float[] dimensions, T entry)
  {
    assert (coords.length == numDims);
    assert (dimensions.length == numDims);
    Node l = findLeaf(root, coords, dimensions, entry);
    if ( l == null ) {
      System.out.println("WTF?");
      findLeaf(root, coords, dimensions, entry);
    }
    assert (l != null) : "Could not find leaf for entry to delete";
    assert (l.leaf) : "Entry is not found at leaf?!?";
    ListIterator<Node> li = l.children.listIterator();
    T removed = null;
    while (li.hasNext())
    {
      @SuppressWarnings("unchecked")
      Entry e = (Entry) li.next();
      if (e.entry.equals(entry))
      {
        removed = e.entry;
        li.remove();
        break;
      }
    }
    if (removed != null)
    {
      condenseTree(l);
      size--;
    }
    if ( size == 0 )
    {
      root = buildRoot(true);
    }
    return (removed != null);
  }

  public boolean delete(float[] coords, T entry)
  {
    return delete(coords, pointDims, entry);
  }

  public Node findLeaf(Node n, float[] coords, float[] dimensions, T entry)
  {
    if (n.leaf)
    {
      for (Node c : n.children)
      {
        if (((Entry) c).entry.equals(entry))
        {
          return n;
        }
      }
      return null;
    }
    else
    {
      for (Node c : n.children)
      {
        if (isOverlap(c.coords, c.dimensions, coords, dimensions))
        {
          Node result = findLeaf(c, coords, dimensions, entry);
          if (result != null)
          {
            return result;
          }
        }
      }
      return null;
    }
  }

  public void condenseTree(Node n)
  {
    Set<Node> q = new HashSet<Node>();
    while (n != root)
    {
      if (n.leaf && (n.children.size() < minEntries))
      {
        q.addAll(n.children);
        n.parent.children.remove(n);
      }
      else if (!n.leaf && (n.children.size() < minEntries))
      {
        // probably a more efficient way to do this...
        LinkedList<Node> toVisit = new LinkedList<Node>(n.children);
        while (!toVisit.isEmpty())
        {
          Node c = toVisit.pop();
          if (c.leaf)
          {
            q.addAll(c.children);
          }
          else
          {
            toVisit.addAll(c.children);
          }
        }
        n.parent.children.remove(n);
      }
      else
      {
        tighten(n);
      }
      n = n.parent;
    }
    if ( root.children.size() == 0 )
    {
      root = buildRoot(true);
    }
    else if ( (root.children.size() == 1) && (!root.leaf) )
    {
      root = root.children.get(0);
      root.parent = null;
    }
    else
    {
      tighten(root);
    }
    for (Node ne : q)
    {
      @SuppressWarnings("unchecked")
      Entry e = (Entry) ne;
      insert(e.coords, e.dimensions, e.entry);
    }
    size -= q.size();
  }

  /**
   * Empties the RTree
   */
  public void clear()
  {
    root = buildRoot(true);
    // let the GC take care of the rest.
  }

  /**
   * Inserts the given entry into the RTree, associated with the given
   * rectangle.
   * 
   * @param coords
   *          the corner of the rectangle that is the lower bound in every
   *          dimension
   * @param dimensions
   *          the dimensions of the rectangle
   * @param entry
   *          the entry to insert
   */
  public void insert(float[] coords, float[] dimensions, T entry)
  {
    assert (coords.length == numDims);
    assert (dimensions.length == numDims);
    Entry e = new Entry(coords, dimensions, entry);
    Node l = chooseLeaf(root, e);
    l.children.add(e);
    size++;
    e.parent = l;
    if (l.children.size() > maxEntries)
    {
      Node[] splits = splitNode(l);
      adjustTree(splits[0], splits[1]);
    }
    else
    {
      adjustTree(l, null);
    }
  }

  /**
   * Convenience method for inserting a point
   * @param coords
   * @param entry
   */
  public void insert(float[] coords, T entry)
  {
    insert(coords, pointDims, entry);
  }

  public void adjustTree(Node n, Node nn)
  {
    if (n == root)
    {
      if (nn != null)
      {
        // build new root and add children.
        root = buildRoot(false);
        root.children.add(n);
        n.parent = root;
        root.children.add(nn);
        nn.parent = root;
      }
      tighten(root);
      return;
    }
    tighten(n);
    if (nn != null)
    {
      tighten(nn);
      if (n.parent.children.size() > maxEntries)
      {
        Node[] splits = splitNode(n.parent);
        adjustTree(splits[0], splits[1]);
      }
    }
    if (n.parent != null)
    {
      adjustTree(n.parent, null);
    }
  }

  public Node[] splitNode(Node n)
  {
    // TODO: this class probably calls "tighten" a little too often.
    // For instance the call at the end of the "while (!cc.isEmpty())" loop
    // could be modified and inlined because it's only adjusting for the addition
    // of a single node.  Left as-is for now for readability.
    @SuppressWarnings("unchecked")
    Node[] nn = new RTree.Node[]
    { n, new Node(n.coords, n.dimensions, n.leaf) };
    nn[1].parent = n.parent;
    if (nn[1].parent != null)
    {
      nn[1].parent.children.add(nn[1]);
    }
    LinkedList<Node> cc = new LinkedList<Node>(n.children);
    n.children.clear();
    Node[] ss = seedPicker == SeedPicker.LINEAR ? lPickSeeds(cc) : qPickSeeds(cc);
    nn[0].children.add(ss[0]);
    nn[1].children.add(ss[1]);
    tighten(nn);
    while (!cc.isEmpty())
    {
      if ((nn[0].children.size() >= minEntries)
          && (nn[1].children.size() + cc.size() == minEntries))
      {
        nn[1].children.addAll(cc);
        cc.clear();
        tighten(nn); // Not sure this is required.
        return nn;
      }
      else if ((nn[1].children.size() >= minEntries)
          && (nn[0].children.size() + cc.size() == minEntries))
      {
        nn[0].children.addAll(cc);
        cc.clear();
        tighten(nn); // Not sure this is required.
        return nn;
      }
      Node c = seedPicker == SeedPicker.LINEAR ? lPickNext(cc) : qPickNext(cc, nn);
      Node preferred;
      float e0 = getRequiredExpansion(nn[0].coords, nn[0].dimensions, c);
      float e1 = getRequiredExpansion(nn[1].coords, nn[1].dimensions, c);
      if (e0 < e1)
      {
        preferred = nn[0];
      }
      else if (e0 > e1)
      {
        preferred = nn[1];
      }
      else
      {
        float a0 = getArea(nn[0].dimensions);
        float a1 = getArea(nn[1].dimensions);
        if (a0 < a1)
        {
          preferred = nn[0];
        }
        else if (e0 > a1)
        {
          preferred = nn[1];
        }
        else
        {
          if (nn[0].children.size() < nn[1].children.size())
          {
            preferred = nn[0];
          }
          else if (nn[0].children.size() > nn[1].children.size())
          {
            preferred = nn[1];
          }
          else
          {
            preferred = nn[(int) Math.round(Math.random())];
          }
        }
      }
      preferred.children.add(c);
      tighten(preferred);
    }
    return nn;
  }

  // Implementation of Quadratic PickSeeds
  public RTree<T>.Node[] qPickSeeds(LinkedList<Node> nn)
  {
    @SuppressWarnings("unchecked")
    RTree<T>.Node[] bestPair = new RTree.Node[2];
    float maxWaste = -1.0f * Float.MAX_VALUE;
    for (Node n1: nn)
    {
      for (Node n2: nn)
      {
        if (n1 == n2) continue;
        float n1a = getArea(n1.dimensions);
        float n2a = getArea(n2.dimensions);
        float ja = 1.0f;
        for ( int i = 0; i < numDims; i++ )
        {
          float jc0 = Math.min(n1.coords[i], n2.coords[i]);
          float jc1 = Math.max(n1.coords[i] + n1.dimensions[i], n2.coords[i] + n2.dimensions[i]);
          ja *= (jc1 - jc0);
        }
        float waste = ja - n1a - n2a;
        if ( waste > maxWaste )
        {
          maxWaste = waste;
          bestPair[0] = n1;
          bestPair[1] = n2;
        }
      }
    }
    nn.remove(bestPair[0]);
    nn.remove(bestPair[1]);
    return bestPair;
  }

  /**
   * Implementation of QuadraticPickNext
   * @param cc the children to be divided between the new nodes, one item will be removed from this list.
   * @param nn the candidate nodes for the children to be added to.
   */
  public Node qPickNext(LinkedList<Node> cc, Node[] nn)
  {
    float maxDiff = -1.0f * Float.MAX_VALUE;
    Node nextC = null;
    for ( Node c: cc )
    {
      float n0Exp = getRequiredExpansion(nn[0].coords, nn[0].dimensions, c);
      float n1Exp = getRequiredExpansion(nn[1].coords, nn[1].dimensions, c);
      float diff = Math.abs(n1Exp - n0Exp);
      if (diff > maxDiff)
      {
        maxDiff = diff;
        nextC = c;
      }
    }
    assert (nextC != null) : "No node selected from qPickNext";
    cc.remove(nextC);
    return nextC;
  }

  // Implementation of LinearPickSeeds
  public RTree<T>.Node[] lPickSeeds(LinkedList<Node> nn)
  {
    @SuppressWarnings("unchecked")
    RTree<T>.Node[] bestPair = new RTree.Node[2];
    boolean foundBestPair = false;
    float bestSep = 0.0f;
    for (int i = 0; i < numDims; i++)
    {
      float dimLb = Float.MAX_VALUE, dimMinUb = Float.MAX_VALUE;
      float dimUb = -1.0f * Float.MAX_VALUE, dimMaxLb = -1.0f * Float.MAX_VALUE;
      Node nMaxLb = null, nMinUb = null;
      for (Node n : nn)
      {
        if (n.coords[i] < dimLb)
        {
          dimLb = n.coords[i];
        }
        if (n.dimensions[i] + n.coords[i] > dimUb)
        {
          dimUb = n.dimensions[i] + n.coords[i];
        }
        if (n.coords[i] > dimMaxLb)
        {
          dimMaxLb = n.coords[i];
          nMaxLb = n;
        }
        if (n.dimensions[i] + n.coords[i] < dimMinUb)
        {
          dimMinUb = n.dimensions[i] + n.coords[i];
          nMinUb = n;
        }
      }
      float sep = (nMaxLb == nMinUb) ? -1.0f :
                  Math.abs((dimMinUb - dimMaxLb) / (dimUb - dimLb));
      if (sep >= bestSep)
      {
        bestPair[0] = nMaxLb;
        bestPair[1] = nMinUb;
        bestSep = sep;
        foundBestPair = true;
      }
    }
    // In the degenerate case where all points are the same, the above
    // algorithm does not find a best pair.  Just pick the first 2
    // children.
    if ( !foundBestPair )
    {
      bestPair = new RTree.Node[] { nn.get(0), nn.get(1) };
    }
    nn.remove(bestPair[0]);
    nn.remove(bestPair[1]);
    return bestPair;
  }

  /**
   * Implementation of LinearPickNext
   * @param cc the children to be divided between the new nodes, one item will be removed from this list.
   */
  public Node lPickNext(LinkedList<Node> cc)
  {
    return cc.pop();
  }

  public void tighten(Node... nodes)
  {
    assert(nodes.length >= 1): "Pass some nodes to tighten!";
    for (Node n: nodes) {
      assert(n.children.size() > 0) : "tighten() called on empty node!";
      float[] minCoords = new float[numDims];
      float[] maxCoords = new float[numDims];
      for (int i = 0; i < numDims; i++)
      {
        minCoords[i] = Float.MAX_VALUE;
        maxCoords[i] = Float.MIN_VALUE;

        for (Node c : n.children)
        {
          // we may have bulk-added a bunch of children to a node (eg. in
          // splitNode)
          // so here we just enforce the child->parent relationship.
          c.parent = n;
          if (c.coords[i] < minCoords[i])
          {
            minCoords[i] = c.coords[i];
          }
          if ((c.coords[i] + c.dimensions[i]) > maxCoords[i])
          {
            maxCoords[i] = (c.coords[i] + c.dimensions[i]);
          }
        }
      }
      for (int i = 0; i < numDims; i++)
      {
        // Convert max coords to dimensions
        maxCoords[i] -= minCoords[i];
      }
      System.arraycopy(minCoords, 0, n.coords, 0, numDims);
      System.arraycopy(maxCoords, 0, n.dimensions, 0, numDims);
    }
  }

  public RTree<T>.Node chooseLeaf(RTree<T>.Node n, RTree<T>.Entry e)
  {
    if (n.leaf)
    {
      return n;
    }
    float minInc = Float.MAX_VALUE;
    Node next = null;
    for (RTree<T>.Node c : n.children)
    {
      float inc = getRequiredExpansion(c.coords, c.dimensions, e);
      if (inc < minInc)
      {
        minInc = inc;
        next = c;
      }
      else if (inc == minInc)
      {
        float curArea = 1.0f;
        float thisArea = 1.0f;
        for (int i = 0; i < c.dimensions.length; i++)
        {
          curArea *= next.dimensions[i];
          thisArea *= c.dimensions[i];
        }
        if (thisArea < curArea)
        {
          next = c;
        }
      }
    }
    return chooseLeaf(next, e);
  }

  /**
   * Returns the increase in area necessary for the given rectangle to cover the
   * given entry.
   */
  public float getRequiredExpansion(float[] coords, float[] dimensions, Node e)
  {
    float area = getArea(dimensions);
    float[] deltas = new float[dimensions.length];
    for (int i = 0; i < deltas.length; i++)
    {
      if (coords[i] + dimensions[i] < e.coords[i] + e.dimensions[i])
      {
        deltas[i] = e.coords[i] + e.dimensions[i] - coords[i] - dimensions[i];
      }
      else if (coords[i] + dimensions[i] > e.coords[i] + e.dimensions[i])
      {
        deltas[i] = coords[i] - e.coords[i];
      }
    }
    float expanded = 1.0f;
    for (int i = 0; i < dimensions.length; i++)
    {
      expanded *= dimensions[i] + deltas[i];
    }
    return (expanded - area);
  }

  public float getArea(float[] dimensions)
  {
    float area = 1.0f;
    for (int i = 0; i < dimensions.length; i++)
    {
      area *= dimensions[i];
    }
    return area;
  }

  public boolean isOverlap(float[] scoords, float[] sdimensions,
      float[] coords, float[] dimensions)
  {
    final float FUDGE_FACTOR=1.001f;
    for (int i = 0; i < scoords.length; i++)
    {
      boolean overlapInThisDimension = false;
      if (scoords[i] == coords[i])
      {
        overlapInThisDimension = true;
      }
      else if (scoords[i] < coords[i])
      {
        if (scoords[i] + FUDGE_FACTOR*sdimensions[i] >= coords[i])
        {
          overlapInThisDimension = true;
        }
      }
      else if (scoords[i] > coords[i])
      {
        if (coords[i] + FUDGE_FACTOR*dimensions[i] >= scoords[i])
        {
          overlapInThisDimension = true;
        }
      }
      if (!overlapInThisDimension)
      {
        return false;
      }
    }
    return true;
  }
  public ArrayList<Node> bottomUp(Node root){
	  ArrayList<Node> queue = new ArrayList<Node>();
	  if (root == null){
		  //System.out.println("hellos");
          return queue;
      }
	  int count=0;
      queue.add(root);
      int curcount = 1;
      String ent="Entry";
      int next = 0;
      
      while (queue.size() > 0) {
                              
          //ArrayList<Integer> level = new ArrayList<Integer>();               
          
          while (curcount > 0) {
        	  //System.out.println("hello"+ count++);
              Node cur = queue.get(0);
              //System.out.println(cur.getClass().getName());
              if((cur.getClass().getName()).equals("RTree$Entry"))
            	  {System.out.println("Done");
            	  return queue;
            	  }
              /*
              if (cur.left != null){
                  queue.add(cur.left);
                  next++;
              }
              
              if (cur.right != null){
                  queue.add(cur.right);
                  next++;
              }
              */
              for(Node n:cur.children){
            	  if(n!=null){
            		  queue.add(n);
            		  next++;
            	  }
              }
            //  level.add(cur.val);
              queue.remove(0);
              curcount--;
          }
          curcount = next;
          next = 0;
          //toret.add(level);
	}
      return queue;    
  } 
  
  public void addLeaf(String filename,int num,int epsilon){
	  try{
	  File outFile = new File(filename);
	  BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
	  //System.out.println(num);
	  ArrayList<Node> list = bottomUp(this.root);
	  ArrayList<Node> parents=new ArrayList();
	  //System.out.println(list.size());
	  //for(int k=0;k<list.size();k++){
		//  System.out.println(list.get(k).coords[0]+ " "+ list.get(k).coords[1]+ " "+ k);
	  //}
	  String temp="";
	  float[] tcoords ;
	  float[] tdim;
	  int dim;
	  int i,count,k,j=0;
	  int fix=0;
	  List<Node> borders=new ArrayList();
	  List<Node> cur=new ArrayList();
	  List<Entry> sample;
	  while(j<list.size()){
		  for(i=fix;i<fix+num;i++){
			  if(i==list.size())
				  break;
			  cur.add(list.get(i));
			  for(count=0;count<list.get(i).coords.length;count++)
			  {	  
				  temp+=Float.toString(list.get(i).coords[count])+ " " ;
			  }
			  if(!parents.contains(list.get(i).parent)){
				  parents.add(list.get(i).parent);
				  dim=list.get(i).coords.length;
				  tcoords=new float[dim];
				  tdim=new float[dim];
				  for(k=0;k<dim;k++){
					tcoords[k]=list.get(i).coords[k]-epsilon;
					tdim[k]=list.get(i).dimensions[k]+(2*epsilon);				
				  }
				 sample=search(tcoords,tdim);
				 for(int x=0;x<sample.size();x++){
					 if(!borders.contains(sample.get(x)))
						 borders.add(sample.get(x));}
			  }	
			  
			  temp=temp + ((Entry)list.get(i)).entry  +";";
			  
		  }
		
		  
		fix=i;
		
	  //System.out.println(temp);	  
	  bw.write(temp);
	  temp="";
	  for(i=0;i<borders.size();i++){
		if(!cur.contains(borders.get(i)))
		{	dim=borders.get(i).coords.length;
			for(k=0;k<dim;k++){
				temp+=Float.toString(borders.get(i).coords[k])+ " " ;
			  }
		temp=temp + ((Entry)borders.get(i)).entry  +":";
		}
		}
	  //System.out.println(temp);	
	  bw.write(temp);
	  bw.write("\n");
	  temp="";
	  j=fix;
	  cur.clear();
	  borders.clear();
	  parents.clear();
	  }
	  bw.close();
	  }catch(Exception e){}
  }
 
  class Node
  {
    final float[] coords;
    final float[] dimensions;
    final LinkedList<Node> children;
    final boolean leaf;
    
    Node parent;

    public Node(float[] coords, float[] dimensions, boolean leaf)
    {
      this.coords = new float[coords.length];
      this.dimensions = new float[dimensions.length];
      System.arraycopy(coords, 0, this.coords, 0, coords.length);
      System.arraycopy(dimensions, 0, this.dimensions, 0, dimensions.length);
      this.leaf = leaf;
      children = new LinkedList<Node>();
    }

  }


  public class Entry extends Node
  {
    final T entry;

    public Entry(float[] coords, float[] dimensions, T entry)
    {
      // an entry isn't actually a leaf (its parent is a leaf)
      // but all the algorithms should stop at the first leaf they encounter,
      // so this little hack shouldn't be a problem.
      super(coords, dimensions, true);
      this.entry = entry;
    }

    public String toString()
    {
      return "Entry: " + entry;
    }
  } 

  // The methods below this point can be used to create an HTML rendering
  // of the RTree.  Maybe useful for debugging?
  
  public static final int elemWidth = 150;
  public static final int elemHeight = 120;
  
  String visualize()
  {
    int ubDepth = (int)Math.ceil(Math.log(size)/Math.log(minEntries)) * elemHeight;
    int ubWidth = size * elemWidth;
    java.io.StringWriter sw = new java.io.StringWriter();
    java.io.PrintWriter pw = new java.io.PrintWriter(sw);
    pw.println( "<html><head></head><body>");
    visualize(root, pw, 0, 0, ubWidth, ubDepth);
    pw.println( "</body>");
    pw.flush();
    return sw.toString();
  }
  
  public void visualize(Node n, java.io.PrintWriter pw, int x0, int y0, int w, int h)
  {
    pw.printf( "<div style=\"position:absolute; left: %d; top: %d; width: %d; height: %d; border: 1px dashed\">\n",
               x0, y0, w, h);
    pw.println( "<pre>");
    pw.println( "Node: " + n.toString() + " (root==" + (n == root) + ") \n" );
    pw.println( "Coords: " + Arrays.toString(n.coords) + "\n");
    pw.println( "Dimensions: " + Arrays.toString(n.dimensions) + "\n");
    pw.println( "# Children: " + ((n.children == null) ? 0 : n.children.size()) + "\n" );
    pw.println( "isLeaf: " + n.leaf + "\n");
    pw.println( "</pre>");
    int numChildren = (n.children == null) ? 0 : n.children.size();
    for ( int i = 0; i < numChildren; i++ )
    {
      visualize(n.children.get(i), pw, (int)(x0 + (i * w/(float)numChildren)),
          y0 + elemHeight, (int)(w/(float)numChildren), h - elemHeight);
    }
    pw.println( "</div>" );
  }
}
