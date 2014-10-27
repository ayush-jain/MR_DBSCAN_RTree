import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class Merge {
/*
	public static void main(String[] args) {
		try{
		 FileInputStream fstream1 = new FileInputStream("test.txt");
	     BufferedReader br = new BufferedReader(new InputStreamReader(fstream1));
	     File outFile = new File("abc.txt");
		 BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		double[] arr;
		String s1,s2,temp;
		temp="";
		String str="";
		s1="";
		s2="";
		final int dim=2;
		int i,j,k=0;
		//int low,high=0;
		int mini=0;
		double min;
		ArrayList<Double> carr=new ArrayList();
		ArrayList<double[]> lst= new ArrayList();
		ArrayList<Integer> pos=new ArrayList();
		while((str=br.readLine())!=null){
			String[] sl=str.split("\t");
			String[] sl1=sl[1].split(" ");
			arr=new double[6];
			arr[0]=Double.parseDouble(sl[0]);
			for(i=1;i<=dim+3;i++){
				arr[i]=Double.parseDouble(sl1[i-1]);
			}
			lst.add(arr);
		}
		
		Collections.sort(lst,new Comparator<double[]>(){
			   public int compare(double[] o1, double[] o2) {
			        return (int) (o1[1+dim] - o2[1+dim]); 
			    }
		});
		
		for(i=0;i<lst.size();i++){
			System.out.println(lst.get(i)[0]+" "+lst.get(i)[1]+ " "+lst.get(i)[2]+" "+(int)lst.get(i)[3]+" "+(int)lst.get(i)[4]+" "+(int)lst.get(i)[5]);
		}
		
		
		mini=0;
		carr.add(lst.get(0)[2+dim]);
		//low=0;
		for(i=1;i<lst.size();i++){
			if(mini!=0)
			{mini=i;
			carr.add(lst.get(i)[2+dim]);}	
			if(lst.get(i)[3+dim]==1){
			while(i<lst.size()&&lst.get(i)[1+dim]==lst.get(i-1)[1+dim]){
				if(lst.get(i)[3+dim]==0)
					{pos.add(i);i++; continue;}
				if(lst.get(i)[2+dim]<=lst.get(mini)[2+dim])
				{
					mini=i;
					
				}
				if(!carr.contains(lst.get(i)[2+dim])){
					carr.add(lst.get(i)[2+dim]);
				}
			System.out.println(i+"---"+ lst.size());	
			i++;
			
			}
			
			//high=i-1;
			if(carr.size()>1){
			min=lst.get(mini)[2+dim];//i=low+1;
			for(int n=0;n<pos.size();n++){
				lst.get(pos.get(n))[2+dim]=min;	
			}
			
			replaceAll(lst,carr,min,dim);i--;}
			//for(int m=low;m<high;m++)
				//lst.remove(m);}
			carr.clear();
			pos.clear();
			//low=i;
			}
			
	}
	Collections.sort(lst,new Comparator<double[]>(){
		public int compare(double[] o1, double[] o2) {
	        return (int) (o1[2+dim] - o2[2+dim]); 
	    }
	});
	
	for(i=0;i<lst.size();i++){
		System.out.println(lst.get(i)[2+dim]+"-----"+lst.get(i)[1]+ " "+lst.get(i)[2]);
	}
	for(i=0;i<lst.size();i++){
		System.out.println((int)lst.get(i)[4]+"-----"+lst.get(i)[0]+" "+lst.get(i)[1]+ " "+lst.get(i)[2]+" "+(int)lst.get(i)[3]+" "+(int)lst.get(i)[5]);
	}
		
	}catch(Exception e){System.out.println("hello");}

	
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
*/
}
