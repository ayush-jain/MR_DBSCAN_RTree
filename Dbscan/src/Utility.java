import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Utility {
	/**
	* Test the distance between the two points
	* @ Param p point
	* @ Param q point
	* @ Return returns the distance between two points
	*/
public static double getDistance (Point p, Point q) {
double dx = p.getX ()-q.getX ();
double dy = p.getY ()-q.getY ();
double distance = Math.sqrt (dx * dx + dy * dy);
return distance;
} /*
* Check the given point is not the central point
* @ Param lst The list storage point
* @ Param p the point to be tested
* @ Param ee radius
* @ Param minp density threshold
* @ Return a temporary storage point visited
*/
public static List<Point> isKeyPoint (List lst, Point p, int e, int minp) {
int count =0;
List<Point> tmpLst = new ArrayList<Point> ();
for (Iterator<Point> it = lst.iterator(); it.hasNext ();){
Point q = it.next();
if (getDistance(p,q) <= e) {
++count;
if (! tmpLst.contains (q)) {
tmpLst.add (q);
}
}
}
if (count >= minp) {
p.setKey(true);
return tmpLst;
}
return null;
}
public static void setListClassed (List lst) {
for (Iterator<Point> it = lst.iterator (); it.hasNext ();){
Point p = it.next ();
if (! p.isClassed ()) {
p.setClassed (true);
}
}
}
/**
Merge
* @ Param a
* @ Param b
* @ Return a
*/
public static boolean mergeList (List a, List b) {
boolean merge = false;
for (int index = 0; index < b.size(); index++){
if (a.contains (b.get (index))) {
merge = true;
break;
}
}
if (merge) {
for (int index = 0; index <b.size();index++){
if (! a.contains (b.get (index))) {
a.add (b.get (index));
}
}
}
return merge;
}
/**
* Back to the collection point in the text
* @ Return back to the mid-point of the set text
* @ Throws IOException
*/
public static List getPointsList () throws IOException {
List lst = new ArrayList ();
String txtPath = "test6.txt";
BufferedReader br = new BufferedReader (new FileReader (txtPath));
String str = "";
while ((str = br.readLine ())!= null && str !=""){
lst.add (new Point (str));
}
br.close ();
return lst;
}
}