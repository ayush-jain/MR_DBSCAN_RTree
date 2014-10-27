import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DataPartitionDriver {
	static int epsilon=2; 
	static int DIM=0;
	static int NUMR=0;
	static float[] low  ;
	static int range=5;
	static float[] dim ;
	static float [] zeroes;
	static RTree<Integer> tree ;
    static List<DataObject> rects=new ArrayList() ;
	
	static int partitions=0;
	
	public static void takeInput() {
		int i,j=0;
		
		String str;
		try{
		FileInputStream fstream1 = new FileInputStream("Data43.txt");
		BufferedReader br1 ;
		
		br1=new BufferedReader(new InputStreamReader(fstream1));
		//String tempff=br1.readLine();
		//System.out.println(tempff);
		NUMR=Integer.parseInt(br1.readLine());
		//System.out.println(NUMR);
		partitions=NUMR/6;
		//System.out.println(partitions);
		DIM=Integer.parseInt(br1.readLine());
		tree= new RTree<Integer>(10, 2, DIM, RTree.SeedPicker.LINEAR);
		Driver d= new Driver();
		rects.addAll(d.createList(DIM));
		//for(i=0;i<rects.size();i++){
			//System.out.println(rects.get(i).val[0]+" "+ rects.get(i).val[1]);
		//}
		/*
		zeroes= new float[DIM];
		float[] tempf=new float[DIM];
		for(i=0;i<DIM;i++){
			zeroes[i]=0;
		}
		j=0;
		while((str=br.readLine())!=null){
			j++;
			String[] temp=str.split(" ");
			//System.out.println(temp[0]);
			for(i=0;i<temp.length;i++){
				tempf[i]=Float.parseFloat(temp[i]);
				
			}
			 //System.out.println(tempf[1]);
			 
			 //System.out.println(o.val[0]);
			 rects.add(new DataObject(tempf,zeroes,j));
			 //System.out.println(rects.get(j-1).val[0]+ "---- "+ j);	
		}*/
		br1.close(); 
		}catch(Exception e){
			System.out.println("Hello");
		}
	}
	
	public static void main(String args[]){
		long start = new Date().getTime();
		takeInput();
		insert();
		System.out.println(tree.size +"  "+ partitions);
		String filename= "output.txt";		
		tree.addLeaf(filename,partitions,epsilon);
		long end = new Date().getTime();
        System.out.println("Job took "+(end-start) + "milliseconds");
	}
	
	
	

	
	
	public static void insert(){
		int i=0;
		DataObject pi;
		for(i=0;i<rects.size();i++){
		 pi=rects.get(i);
		 //System.out.println(pi.val[0]+" " +rects.get(i).val[1]+" "+ rects.get(i).id);
		 
		 tree.insert(pi.val,pi.dim,pi.id);
		}	
	//	System.out.println(tree.size);
	}
	
	  
		
	
/*	
	private static class DataObject {
		      final float[] val;
		      final float[] dim;
		      final Integer id;

		      DataObject(float[] val, float[] dim, int id) {
		        this.val = val;
		        this.dim = dim;
		        this.id = id;
		      }
		}     
		    

		    for ( int j = 0; j < 500; j++ )
		    {
		      RTree<Integer> tree = new RTree<Integer>(10, 2, 3, RTree.SeedPicker.LINEAR);
		      List<DataObject> rects = new ArrayList<DataObject>();

		      for (int i = 0; i < 150; i++) {
		        rects.add(new DataObject(
		            new float[]{i, i * 2, i * 3},
		            new float[]{0, 0, 0},
		            i));
		        DataObject dataObject = rects.get(i);
		        tree.insert(dataObject.val, dataObject.dim, dataObject.id);
		      }

		      for (int i = 0; i < 150; i++) {
		        DataObject dataObject = rects.get(i);
		        boolean deleted = tree.delete(dataObject.val, dataObject.dim, dataObject.id);
		        assert deleted;
		      }
		    }
*/		  
}
