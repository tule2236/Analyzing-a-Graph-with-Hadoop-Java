package edu.gatech.cse6242;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Q1 {
public static class TokenizerMap extends Mapper<LongWritable, Text, Text, IntWritable> { 
    private IntWritable weight = new IntWritable();
    private Text target = new Text();
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      String line = value.toString();
      
      StringTokenizer s = new StringTokenizer(line,"\t");
      
      String one = s.nextToken();
     

      String second = s.nextToken();
      target.set(second);
      System.out.println(target);

      String third = s.nextToken();
      int w = Integer.parseInt(third);
      weight.set(w);
      
      context.write(target,weight);
       
    }
  }

  public static class IntReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable result = new IntWritable();
    
    public void reduce(Text key, Iterable <IntWritable> values,
                       Context context) throws IOException, InterruptedException {
	//int max = 0;
	//for (IntWritable val: values){
	//max += val.get();
	//}
	int max = Integer.MIN_VALUE;
	for (IntWritable val: values) {
		int a = val.get();		
		if (a > max) {
			max = a;
		}
	}
	result.set(max);
	context.write(key,result);
}
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");
    job.setJarByClass(Q1.class);
    job.setMapperClass(TokenizerMap.class);
    job.setCombinerClass(IntReducer.class);
    job.setReducerClass(IntReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

