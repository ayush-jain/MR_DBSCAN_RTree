import java.io.*;
import java.util.*;

public class Dbscan {
private static List<Point> pointsList ;// store the set of all points
private static List<List>  resultList = new ArrayList <List>();// storage DBSCAN algorithm to return the result set
private static int e = 1; // e radius
private static int minp = 3; // density threshold
/**
* Extract all the points in the text and stored in the pointsList
* @ Throws IOException
*/
private static void display () {
int index = 1;
for (Iterator <List> it = resultList.iterator(); it.hasNext ();){
List lst = it.next ();
if (lst.isEmpty ()) {
continue;
} 
System.out.println (" s "+ index + " a cluster ");
for(Iterator<Point> it1 = lst.iterator(); it1.hasNext();){
Point p = it1.next ();
System.out.println (p.print());
}
index++;
}
}
// Find all the cluster can be directly
private static void applyDbscan () {
try {
pointsList = Utility.getPointsList ();
for (Iterator<Point> it = pointsList.iterator (); it.hasNext ();){
Point p = it.next ();
if (! p.isClassed ()) {
List tmpLst = new ArrayList ();
if ((tmpLst = Utility.isKeyPoint (pointsList, p, e, minp))!= null) {
// End point for all clustering to mark
Utility.setListClassed (tmpLst);
resultList.add (tmpLst);
}
}
}
} catch (IOException e) {
// TODO Auto-generated catch block
	e.printStackTrace ();
	}
}
// Direct access to the clustering of all the merger, that is up to the point and find the indirect merger
private static List  getResult () {
applyDbscan ();// find all the direct clustering
int length = resultList.size ();
for (int i = 0; i <length;i++){
for (int j = i+1; j <length;j++){
if (Utility.mergeList (resultList.get (i), resultList.get(j))) {
	resultList.get(j). clear ();
	}
}
}
return resultList;
}
/**
* Program main function
* @ Param args
*/
public static void main (String [] args) {
	long start = new Date().getTime();            
	getResult();
	display();
	long end = new Date().getTime();
	System.out.println("Job took "+(end-start) + "milliseconds");
// System.out.println (Utility.getDistance (new Point (0,0), new Point (0,2)));
}
}