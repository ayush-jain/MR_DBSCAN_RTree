

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DbscanMapper
        extends Mapper<LongWritable, Text, IntWritable, Text>{
    //private static final IntWritable one = new IntWritable(1);
    //private Text word = new Text();
	int count=0;
	
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
    	List<Point> lst= new ArrayList();
    	Point p;
    	String line = value.toString();
        String[] points = line.split(";");
        String temp=points[points.length-1];
        points[points.length-1]="";
        String [] border=temp.split(":");
        for (String w : points) {
        	if(!w.isEmpty()){
        		//System.out.println(w);
        		lst.add(new Point(w));
        	}
        }
        for(String t: border){
        	p=new Point(t);
        	p.setBorder(true);
        	lst.add(p);
        }
        
        context.write(new IntWritable(count++), value);
    
    }
    
}