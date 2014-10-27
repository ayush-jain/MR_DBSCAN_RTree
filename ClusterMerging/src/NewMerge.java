import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Iterator;




public class NewMerge {

	public static void main(String[] args) {
		try{
		long start = new Date().getTime();    	
		 FileInputStream fstream1 = new FileInputStream("test.txt");
	     BufferedReader br = new BufferedReader(new InputStreamReader(fstream1));
	     File outFile = new File("abc.txt");
		 BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		double[] arr;
		String s1,s2;
		ArrayList<Double> temp;
		String str="";
		s1="";
		s2="";
		final int dim=2;
		int i,j,k=0;
		boolean flag=false;
		//int low,high=0;
		int mini=0;
		double min=0.0;
		HashMap<Double,Double> match=new HashMap<Double,Double>();
		//ArrayList<Double> carr=new ArrayList();
		ArrayList<double[]> lst= new ArrayList<double []>();
		//ArrayList<Integer> pos=new ArrayList();
		double key;
		HashMap<Double,ArrayList<Double>> hm= new HashMap<Double,ArrayList<Double>>();
		while((str=br.readLine())!=null){
			String[] sl=str.split("\t");
			String[] sl1=sl[1].split(" ");
			arr=new double[6];
			arr[0]=Double.parseDouble(sl[0]);
			for(i=1;i<=dim+3;i++){
				arr[i]=Double.parseDouble(sl1[i-1]);
			}
			lst.add(arr);
			if(!hm.containsKey(arr[dim+1])){
				temp=new ArrayList<Double>();
				temp.add(arr[dim+2]);
				hm.put(arr[dim+1], temp);
			}else{
				temp=hm.get(arr[dim+1]);
				if(!temp.contains(arr[dim+2]))
					temp.add(arr[dim+2]);
				hm.put(arr[dim+1], temp);
			}
			
			
		}
		//System.out.println(hm.size());
		
		Set<Entry<Double, ArrayList<Double>>> s = hm.entrySet();
		Iterator<Entry<Double, ArrayList<Double>>> itr =  s.iterator();
		while(itr.hasNext()){
			Map.Entry me=(Map.Entry)itr.next();
			key=(double) me.getKey();
			temp=(ArrayList<Double>)me.getValue();
			for(j=0;j<temp.size();j++){
				if(match.containsKey(temp.get(j))){
					flag=true;
					min=match.get(temp.get(j));
					//System.out.println("Hello----"+min);
					break;
				}					
			}
			if(!flag){
			Collections.sort(temp,new Comparator<Double>(){
				public int compare(Double o1, Double o2) {
			        return (int) (o1 - o2); 
			    }
			});
			min=temp.get(0);
			//System.out.println("BOO "+min);
			fill(match,temp,min);
			replaceAll(lst,temp,min,dim);
			}
			else{
				fill(match,temp,min);
				replaceAll(lst,temp,min,dim);
				flag=false;
			}
			
			//for(j=0;j<temp.size();j++){	
				//System.out.println(temp.get(j));
				
			//}
			//System.out.println("new"+key+"---"+min);
			
			
		}
		match.clear();
		//Set<Entry<Double,Double>> sm=match.entrySet();
		for(i=0;i<lst.size();i++){
			if(!match.containsKey(lst.get(i)[dim+1])){
			bw.write((int)lst.get(i)[4]+"-----"+lst.get(i)[0]+" "+lst.get(i)[1]+ " "+lst.get(i)[2]+" "+(int)lst.get(i)[3]+" "+(int)lst.get(i)[5]);
			bw.write("\n");
			match.put(lst.get(i)[dim+1],1.0);
			}
			}
		bw.close();
		long end = new Date().getTime();
        System.out.println("Job took "+(end-start) + "milliseconds");
		}catch(Exception e){}
	}
	
public static void fill(HashMap m,ArrayList<Double> t,double min){
	int i;
	for(i=0;i<t.size();i++){
		m.put(t.get(i),min);
	}
}
	
public static void replaceAll(ArrayList<double []>l,ArrayList<Double> m, double min,int dim){
		int i=0;
		for(i=0;i<l.size();i++){
			if(m.contains(l.get(i)[2+dim])){
				l.get(i)[2+dim]=min;
			}
		}
		return;

}



}	

