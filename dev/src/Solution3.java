/*
 * WalletHub Trial
 *
 * this solution addresses a huge file read process to print the most common phrase in the file. It basically
 * reads the file using a buffered strategy and uses disk file based structure for keeping track of the occurrences as
 * in linux for example a disk with .
 *
 * therefore the solution has a requirement of a large enough hard disk obeying the following rule :
 *
 * # df -k
 * /dev/sda3      325125624 53568444 255018632  18% / 
 * # df -i 
 * /dev/sda3      20660224 618553 20041671    3% /
 *
 * we conclude that a 325G hard disk is capable of holding 20G files. Therefore we have a 1/16 relationship (16G hard disk can hold 1G files inodes). 
 *
 * a constraint used is for each phrase max size (PHRASE_MAX_SIZE=100k). This way it is possible to calculate the minimum hard disk size and depending on
 * the amount of memory available if the max number of files (FILES_MAX_NUM) will explode doing :
 *
 * LINE_MAX_SIZE=50x100kb=5Mb
 * 
 * memory data structures max capacity in terms of file number of lines (this is the loop size) for sorting :
 *
 * MEM_CHUNK_N_LINES=MEM_AVAILABLE/LINE_MAX_SIZE
 *
 * so , for a worst case scenario of a 10Gb input file with non repeating phrases running on a 1Gb FREE RAM we will need a 10Gb hard disk space available and will generate a total
 * of 10 files of 1Gb each. The user will have to give the amount of FREE RAM available in order to enable this calc which is by itself full of aproximations. The more FREE RAM lower
 * will be the number of files and more hard disk space will be used as the files will grow in size due to the capacity of sorting a greater number of phrases and the execution will
 * consequently be faster. In the other hand, a very short RAM with a very small hard disk computer will not be able to work with a large file as either the hard disk space or the 
 * number of files generated will give a system threashold as expected !
 *
 * after hard disk sorted files generation we will need to use a merge sort strategy. The amount of MEM_AVAILABLE will be used once again but his time in half for input and the ouput
 * buffer using the other half for each loop over the generated files sorting in whatever fits into MEM_AVAILABLE/2 and then written to a final result fully sorted output file.
 *
 * desavera@gmail.com
 */ 

import java.util.*;
import java.io.*;

public class Solution3 {

  // an added constraint
  static int PHRASE_MAX_SIZE=100000;
  static int PHRASE_MAX_NUM_PER_LINE=50;
  static int LINE_MAX_SIZE=PHRASE_MAX_NUM_PER_LINE*PHRASE_MAX_SIZE;
  static int MAX_N_SORTED_OUTPUT=100000;


  public static void main (String[] args) {

    long FILES_MAX_NUM=0;
    long MEM_AVAILABLE=0;
    long MEM_CHUNK_N_LINES=0;
    long INPUT_FILE_SIZE=0;
    long HARD_DISK_AVAILABLE=0;
    
    

    FileReader inputFile = null;
    Scanner fscan = null;
    String filePath = null;
    long n_sorted_input=0;

    if (args.length == 4) { 

      filePath = args[0].toString();
      INPUT_FILE_SIZE= Long.parseLong(args[1]);
      MEM_AVAILABLE= Long.parseLong(args[2]);
      HARD_DISK_AVAILABLE=Long.parseLong(args[3]);

      FILES_MAX_NUM=HARD_DISK_AVAILABLE/16;
      MEM_CHUNK_N_LINES=MEM_AVAILABLE/LINE_MAX_SIZE;
      
      if ((HARD_DISK_AVAILABLE < INPUT_FILE_SIZE) || 
    		  (INPUT_FILE_SIZE/LINE_MAX_SIZE/MEM_CHUNK_N_LINES > FILES_MAX_NUM))
    	  
    	  System.out.println("your system cannot deal with the input file... please check !");                     
      
    } else {

      System.out.println("please specify [input file name | input file size | FREE RAM available | hard disk space available] ...");
      return;
    }

    try {
    	
        inputFile = new FileReader(filePath);
        fscan = new Scanner(inputFile);
        long total_mem_chunk_n = 0;
        while (fscan.hasNextLine()) {

        	
        	HashMap<String,Long> holder = new HashMap();
        	for (int i=0;fscan.hasNextLine() && i < MEM_CHUNK_N_LINES;i++) {

        		
        		
            	String line = fscan.nextLine();            	            
            	
            	StringTokenizer tokenizer = new StringTokenizer(line,"|");
            	
            	if (tokenizer.countTokens() > PHRASE_MAX_NUM_PER_LINE) {
            		
            		System.out.println("not a valid input phrase...");
            		return;
            		
            	}
            		
            	int n_tokens = tokenizer.countTokens();
            	for (int j=0;j < n_tokens;j++) {
            	
            		String phrase = tokenizer.nextToken().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            		
            		
            		if (!holder.containsKey(phrase)) {
            			
            			holder.put(phrase, 1l);
            			
            		} else {
            			
            			holder.put(phrase,holder.get(phrase) + 1l);
            			
            		}
            		
            	}
            	
            	
        	}           	        
        	
            PhraseReg[] sorter = new PhraseReg[holder.entrySet().size()];
            
            int count = 0; // TODO guarantee that an integer is a limit
        	for (Map.Entry<String,Long> entry : holder.entrySet()) {
        		
        		sorter[count++] = new PhraseReg(entry.getKey(), entry.getValue());
        	}
        	
        	// bubble sort for now
        	Arrays.sort(sorter);
        	
        	FileWriter outFile = new FileWriter("data" + total_mem_chunk_n + ".out");
        	
        	for (int k=0;k < sorter.length;k++) {
        		n_sorted_input++;
        		outFile.write(sorter[k].phrase + '=' + sorter[k].count + '\n');
        	}
        		
        	
        	outFile.close();
        	total_mem_chunk_n++;
        }
        
        fscan.close();
        inputFile.close();
                
        /*
         * now the merge using half mem for each buffer
         */
       
        
        MEM_AVAILABLE/=2;
        long MEM_CHUNK_N_PHRASES=MEM_AVAILABLE/PHRASE_MAX_SIZE;        
        
        

        // streams setup
     	FileWriter resultFile = new FileWriter("result");     	     	
     	FileReader[] sortedFiles = new FileReader[new Long(total_mem_chunk_n).intValue()];
     	Scanner[] fscanners = new Scanner[new Long(total_mem_chunk_n).intValue()];
        for (int i=0;i < total_mem_chunk_n;i++) {
			sortedFiles[i] = new FileReader("data" + i + ".out");
			fscanners[i] = new Scanner(sortedFiles[i]);
        }
		
        
        
        int out_n_limit=0;
        if (n_sorted_input > MAX_N_SORTED_OUTPUT)
        	out_n_limit = MAX_N_SORTED_OUTPUT;
        else
        	out_n_limit = new Long(n_sorted_input).intValue();
        
        
        for (int out_n=0;out_n < out_n_limit;out_n++) {

        	HashMap<String,Long> holder = new HashMap();			
			int count = 0; // TODO guarantee that an integer is a limit			
            for (int i=0;count < MEM_CHUNK_N_PHRASES/total_mem_chunk_n && i < total_mem_chunk_n;i++) {
				    			
    			// TODO : not sure how precise can this get... may be missing a
    			// line due to conversion from long to int ?
    			for (int j = 0; fscanners[i].hasNextLine() && count < MEM_CHUNK_N_PHRASES/total_mem_chunk_n; j++) {

    				String line = fscanners[i].nextLine();
    				StringTokenizer tokenizer = new StringTokenizer(line, "=");
    				String phrase = tokenizer.nextToken();
            		if (!holder.containsKey(phrase)) {
            			
            			holder.put(phrase,Long.parseLong(tokenizer.nextToken()));
            			
            		} else {
            			
            			holder.put(phrase,holder.get(phrase) + Long.parseLong(tokenizer.nextToken()));
            			
            		}
            		
                    count++;       
    				
    			}
    			
            }
            

        	
            PhraseReg[] sorter = new PhraseReg[holder.entrySet().size()];
            count = 0;
        	for (Map.Entry<String,Long> entry : holder.entrySet()) {
        		
        		sorter[count++] = new PhraseReg(entry.getKey(), entry.getValue());
        	}                	       

            
        	// bubble sort for now    			
			Arrays.sort(sorter);
			
			for (int k = 0; k < sorter.length; k++)
				resultFile.write(sorter[k].phrase + '=' + sorter[k].count + '\n');			    			    	    	 

        }
        
        resultFile.close();
        
        for (int i=0;i < total_mem_chunk_n;i++) {
        	fscanners[i].close();
			sortedFiles[i].close();
			
        }


    } catch (IOException fnfe) {

        System.out.println("file not found... please check !");
    
    } finally {
        if (inputFile != null) {
            try {

            inputFile.close();
            } catch (IOException ioe) {

              System.out.println("unable to close file... please check !");

            }
                
        }
        if (fscan != null) {
            fscan.close();
        }
    }
  }
}

class PhraseReg implements Comparable<PhraseReg> {
	
	String phrase;
	Long count;

	public PhraseReg(String key, Long value) {

		this.phrase = key.toString();
		this.count = value;
	}

	@Override
	public int compareTo(PhraseReg o) {
		
		if (count > o.count) return 1;
		if (count < o.count) return -1;
		
		return 0;
	}		
}