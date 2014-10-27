import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;




public class Driver {
	
	public List<DataObject> createList(int DIM){
		int i,j=0;
		String str;
		List<DataObject> rects =new ArrayList();
		float[] zeroes= new float[DIM];
		float[] tempf;
		for(i=0;i<DIM;i++){
			zeroes[i]=0;
		}
		try{
			FileInputStream fstream1 = new FileInputStream("Data43.txt");
			BufferedReader br ;
			br=new BufferedReader(new InputStreamReader(fstream1));
			br.readLine();
			br.readLine();
			while((str=br.readLine())!=null){
			j++;
			String[] temp=str.split(" ");
			tempf=new float[DIM];
			//System.out.println(temp[0]);
			for(i=0;i<DIM;i++){
				tempf[i]=Float.parseFloat(temp[i]);
				
			}
			 //System.out.println(tempf[1]);
			 
			 //System.out.println(o.val[0]);
			 rects.add(new DataObject(tempf,zeroes,j));
			 //System.out.println(rects.get(j-1).val[0]+ "---- "+ j);	
		}
		br.close();
		}catch(Exception e){
			System.out.println("hello");
		}
		//for(i=0;i<rects.size();i++){
			//System.out.println(rects.get(i).val[0]+"----- "+ rects.get(i).val[1]+ "---"+rects.get(i).id);
		//}
		//System.out.println(rects.get(0).val[0]);
		//System.out.println(rects.get(1).val[0]);
	return rects;	
	}
	
 
	   
}
