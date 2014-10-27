import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.mockito.asm.util.ASMifierClassVisitor;

public class Dbscan {
    public static void main(String[] args) throws Exception {        

        //String input = "test.txt";
        //String output = "out";
        //ASMifierClassVisitor.main(new String[](Dbscan.getClass().getName()));
        // Create a new job
        
        Configuration conf = new Configuration();
        Job job = new Job(conf, "Dbscan");

        // Set job name to locate it in the distributed environment
        job.setJarByClass(Dbscan.class);
        //job.setJobName("Dbscan");

        // Set input and output Path, note that we use the default input format
        // which is TextInputFormat (each record is a line of input)
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Set Mapper and Reducer class
        job.setMapperClass(DbscanMapper.class);
        job.setReducerClass(DbscanReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
        // Set Output key and value
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        
        long start = new Date().getTime();
        boolean status = job.waitForCompletion(true);            
        long end = new Date().getTime();
        System.out.println("Job took "+(end-start) + "milliseconds");
        if(status)
        System.exit(0);
        else
        System.exit(1);	
        //System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}