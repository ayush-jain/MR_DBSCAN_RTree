import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DbscanReducer 
        extends Reducer<IntWritable, Text, IntWritable, Text>{
	int e=2;
	int minp=3;
    Text word;
	int index = 1;
	@Override
    protected void reduce(IntWritable key, Iterable<Text> values, 
            Context context)
            throws IOException, InterruptedException {
        
		List<List<Point>> resultList = new ArrayList();
    	//int sum = 0;
        Point p;
        boolean flag=false;
        String temp="";
        int i,j=0;
        List<Point> all= new ArrayList();
        List<Point> mergecandidates= new ArrayList();
        List<Point> noise=new ArrayList();
    	//List<Point> all= new ArrayList();
    	Point p1;
    	String line="";
    	Iterator<Text> itr = values.iterator();
        while (itr.hasNext()) {
            line=itr.next().toString();
        }
    	//String line = value.toString();
        String[] points = line.split(";");
        String temp1=points[points.length-1];
        points[points.length-1]="";
        String [] border=temp1.split(":");
        for (String w : points) {
        	if(!w.isEmpty()){
        		//System.out.println(w);
        		p1=new Point(w);
        		if(!all.contains(p1))
        		all.add(p1);
        	}
        }
        for(String t: border){
        	p1=new Point(t);
        	p1.setBorder(true);
        	if(!all.contains(p1))
        	all.add(p1);
        }
        
        //System.out.println(all.size());
        /*
        Iterator<List<Point>> itr = values.iterator();
        while (itr.hasNext()) {
            all.addAll(itr.next());
        }
        */
        
        
        for(i=0;i<all.size();i++){
        	flag=false; 
        	p = all.get(i);
        	if (! p.isClassed ()) {
        	List<Point> tmpLst = new ArrayList();
        	if ((tmpLst = Utility.isKeyPoint (all, p, e, minp))!= null) {
        	// End point for all clustering to mark
        	Utility.setListClassed (tmpLst);
        	resultList.add (tmpLst);
        	for(j=0;j<tmpLst.size();j++){
        		if(tmpLst.get(j).isBorder()&& !mergecandidates.contains(tmpLst.get(j))){
        			mergecandidates.add(tmpLst.get(j));
        			flag=true;
        		}
        	}
        	if(flag && !mergecandidates.contains(p))
        		mergecandidates.add(p);
        	
        	}
        	else
        		noise.add(p);
        	}
        }
        int length = resultList.size ();
        for ( i = 0; i <length;i++){
        for ( j = i+1; j <length;j++){
        if (Utility.mergeList (resultList.get (i), resultList.get(j))) {
        	resultList.get(j). clear ();
        	}
        }
        }
        
        for (Iterator <List<Point>> it = resultList.iterator(); it.hasNext ();){
        List<Point> lst = it.next ();
        if (lst.isEmpty ()) {
        continue;
        } 
        //System.out.println (" s "+ index + " a cluster ");
        for(Iterator<Point> it1 = lst.iterator(); it1.hasNext();){
        Point t1 = it1.next ();
        if(mergecandidates.contains(t1))
        	{temp=t1.print()+" "+index+" "+ "1";}
        else{
        	temp=t1.print()+" "+index+ " " + "0";}
        word=new Text();
        word.set(temp);
        context.write (key,word);
        temp="";
        }
        index++;
        }
        /*
        //System.out.println(noise.size());
        for(Iterator<Point> it2=noise.iterator();it2.hasNext();){
        	Point t2=it2.next();
        	temp=t2.print()+" "+index+ " " + "0";
        	word=new Text();
            word.set(temp);
            context.write (key,word);
            temp="";
            index++;
        }
        */

        //context.write(key, new IntWritable(sum));
    }
}