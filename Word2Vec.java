import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Word2Vec {
	private static final int MAX_SIZE = 50;
	private static int words;
	private static HashMap<String, float[]> findwordvec = new HashMap<String, float[]>();
	protected static void setWords(int i) {
		words = i;
	}
	public static String savenulltxtpath = null;
	public static void main(String[] args) throws IOException {
		String googleBinFileName = args[0];
		String giveWord = args[1];
		String topIndex = args[2];
		//String saveFileName = args[3];
		//savenulltxtpath = args[3];
		boolean flag = checkisword( giveWord );
		if ( flag == true  ) {
			loadModel( googleBinFileName );
			int topNo = new Integer( topIndex );
			Object re_wdvec[] = fromword2distance(giveWord, topNo);
			String[] array1 = (String[]) re_wdvec[0];
			float[] array2 = (float[]) re_wdvec[1];
			//savetopwords(array1, array2, saveFileName);
			savetopwords(array1, array2);
		} else {
			String message = "this is a similarity search(nearest-neighbour search, "+
                    "we just search if a word contains only alphabets, "+
                    "please remove all Non-alphabetic components, top_number shouldn't be empty," +
                     "then try again! " +
                     "more detail please check: https://code.google.com/p/word2vec/";
			//printemptytxt(saveFileName,message);
			System.out.println(message);
		}	
	}

//	private static void printemptytxt(String savepath,String msg) {
//		try {
//			BufferedWriter out = new BufferedWriter(new FileWriter(savepath));
//			out.write(msg);
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}	
//	}

	private static boolean checkisword(String str) {//check is a word or not
		if(str.matches("\\w+") == true) {
			return true;
		} else{
			return false;
		}	
	}
private static void savetopwords(String[] Object1, float[] Object2) {
		for (int ix = 0; ix < Object2.length; ix++) {
			System.out.println(Object1[ix] + " ------ " + Object2[ix] +"\n");		
		}
}
/*private static void savetopwords(String[] Object1, float[] Object2,	String filename) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			for (int ix = 0; ix < Object2.length; ix++) {
				out.write(Object1[ix] + " ------ " + Object2[ix]);
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/

	public static Object[] fromword2distance(String giveword, int top) {
		float[] vecOfWord = findwordvec.get(giveword);
		if (vecOfWord == null) {
			String message ="the word not exists in my dictionary, in this searching process, "+
                    "we use small version of the word database " +
                    "your search is out of this database,"+
                     "please set another one and  try again! " +
                     "more detail please check: https://code.google.com/p/word2vec/";
			System.out.println(message);
			//printemptytxt(savenulltxtpath,message);
			System.err.println("a Null vector!");	//		
		}
		float[] awk = new float[words];
		String[] wordwv = new String[words];
		Map<String, Float> source = new HashMap<String, Float>();
		int r = 0;
		for (Entry<String, float[]> w : findwordvec.entrySet()) {
			float wv = 0;
			for (int ii = 0; ii < vecOfWord.length; ii++) {
				wv += (w.getValue()[ii]) * (vecOfWord[ii]);
			}
			source.put(w.getKey(), wv);
			awk[r] = wv;
			wordwv[r] = w.getKey();
			r++;
		}
		float[] bb = new float[top];
		String[] wwbb = new String[top];
		for (int ii = 0; ii < awk.length; ii++) {
			float contawk = awk[ii];
			String conttopword = wordwv[ii];
			for (int j = 0; j < bb.length; j++) {
				float temp;
				String tempw;
				if (contawk > bb[j]) {
					temp = contawk;
					contawk = bb[j];
					bb[j] = temp;
					tempw = conttopword;
					conttopword = wwbb[j];
					wwbb[j] = tempw;
				}
			}
		}
		return new Object[] { wwbb, bb };
	}

	public static HashMap<String, float[]> getFindwordvec() {
		return findwordvec;
	}

	public static void setFindwordvec(HashMap<String, float[]> findwordvec) {
		Word2Vec.findwordvec = findwordvec;
	}

	public float[] getWordVector(String word) {
		return findwordvec.get(word);
	}

	public static void loadModel(String path) {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			dis = new DataInputStream(bis);
			int words = new Integer(readString(dis));
			int size = new Integer(readString(dis));
			String word;
			float[] vectors = null;
			//words = 50000;
			setWords(words);//
			//setWords(50000);
			for (int i = 0; i < words; i++) {// words=100000
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);
				for (int j = 0; j < vectors.length; j++) {
					vectors[j] = (float) (vectors[j] / len);
				}
				findwordvec.put(word, vectors);
				dis.read();
			}
		} catch (FileNotFoundException e) {
			String message = "word2vec file can not found, about this file,"+
							"please check: https://code.google.com/p/word2vec/";
			System.out.println(message);
			//printemptytxt(savenulltxtpath,message);
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(bis != null) {
					bis.close();
					bis = null;
				}
				if(dis != null) {
					dis.close();
					dis= null;
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	private static String readString(DataInputStream dis) throws IOException {
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}
}