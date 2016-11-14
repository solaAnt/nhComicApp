package com.app.nhandroid.libs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	/**
	 * д�ļ�
	 * 
	 * @param path
	 *            �ļ���·��
	 * @param content
	 *            д���ļ�������
	 */
	public static void writeTxt(String filePath, String content) {

		File dirFile = new File(filePath);
		try {
			if (dirFile.exists())
				dirFile.delete();

			dirFile.createNewFile();
			// FileWriter fw = new FileWriter(dirFile);// �½�һ��FileWriter
			// fw.write(content);// ���ַ���д�뵽ָ����·���µ��ļ���
			// fw.close();
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(filePath, true), "GBK");
			BufferedWriter bw1 = new BufferedWriter(write);
			bw1.write(content);
			bw1.flush();
			bw1.close();
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readTxtFile(String filePath) {
		try {
			String content = "";
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					System.out.println(lineTxt);
					content += lineTxt;
				}
				read.close();

				return content;
			} else {
				System.out.println("�Ҳ���ָ�����ļ�");
				return null;
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * ���ֽ�Ϊ��λ��ȡ�ļ��������ڶ��������ļ�����ͼƬ��������Ӱ����ļ���
	 */
	public static void readFileByBytes(String fileName) {
		File file = new File(fileName);
		InputStream in = null;
		try {
			System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
			// һ�ζ�һ���ֽ�
			in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte = in.read()) != -1) {
				System.out.write(tempbyte);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
			// һ�ζ�����ֽ�
			byte[] tempbytes = new byte[100];
			int byteread = 0;
			in = new FileInputStream(fileName);
			FileUtils.showAvailableBytes(in);
			// �������ֽڵ��ֽ������У�bytereadΪһ�ζ�����ֽ���
			while ((byteread = in.read(tempbytes)) != -1) {
				System.out.write(tempbytes, 0, byteread);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * ���ַ�Ϊ��λ��ȡ�ļ��������ڶ��ı������ֵ����͵��ļ�
	 */
	public static void readFileByChars(String fileName) {
		File file = new File(fileName);
		Reader reader = null;
		try {
			System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
			// һ�ζ�һ���ַ�
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				// ����windows�£�\r\n�������ַ���һ��ʱ����ʾһ�����С�
				// ������������ַ��ֿ���ʾʱ���ỻ�����С�
				// ��ˣ����ε�\r����������\n�����򣬽������ܶ���С�
				if (((char) tempchar) != '\r') {
					System.out.print((char) tempchar);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
			// һ�ζ�����ַ�
			char[] tempchars = new char[30];
			int charread = 0;
			reader = new InputStreamReader(new FileInputStream(fileName));
			// �������ַ����ַ������У�charreadΪһ�ζ�ȡ�ַ���
			while ((charread = reader.read(tempchars)) != -1) {
				// ͬ�����ε�\r����ʾ
				if ((charread == tempchars.length)
						&& (tempchars[tempchars.length - 1] != '\r')) {
					System.out.print(tempchars);
				} else {
					for (int i = 0; i < charread; i++) {
						if (tempchars[i] == '\r') {
							continue;
						} else {
							System.out.print(tempchars[i]);
						}
					}
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * ����Ϊ��λ��ȡ�ļ��������ڶ������еĸ�ʽ���ļ�
	 */
	public static List<String> readFileByLines(String fileName) {
		File file = new File(fileName);
		List<String> result = new ArrayList<String>();

		BufferedReader reader = null;
		try {
			System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
			while ((tempString = reader.readLine()) != null) {
				// ��ʾ�к�
				System.out.println("line " + line + ": " + tempString);
				line++;
				result.add(tempString);

			}
			reader.close();

			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * �����ȡ�ļ�����
	 */
	public static void readFileByRandomAccess(String fileName) {
		RandomAccessFile randomFile = null;
		try {
			System.out.println("�����ȡһ���ļ����ݣ�");
			// ��һ����������ļ�������ֻ����ʽ
			randomFile = new RandomAccessFile(fileName, "r");
			// �ļ����ȣ��ֽ���
			long fileLength = randomFile.length();
			// ���ļ�����ʼλ��
			int beginIndex = (fileLength > 4) ? 4 : 0;
			// �����ļ��Ŀ�ʼλ���Ƶ�beginIndexλ�á�
			randomFile.seek(beginIndex);
			byte[] bytes = new byte[10];
			int byteread = 0;
			// һ�ζ�10���ֽڣ�����ļ����ݲ���10���ֽڣ����ʣ�µ��ֽڡ�
			// ��һ�ζ�ȡ���ֽ�������byteread
			while ((byteread = randomFile.read(bytes)) != -1) {
				System.out.write(bytes, 0, byteread);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * ��ʾ�������л�ʣ���ֽ���
	 */
	private static void showAvailableBytes(InputStream in) {
		try {
			System.out.println("��ǰ�ֽ��������е��ֽ���Ϊ:" + in.available());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 
     * �ƶ�ָ���ļ����ļ���(���������ļ������ļ���) 
     *  
     * @param fromDir 
     *            Ҫ�ƶ����ļ����ļ��� 
     * @param toDir 
     *            Ŀ���ļ��� 
     * @throws Exception 
     */  
    public static void MoveFolderAndFileWithSelf(String from, String to) throws Exception {  
        try {  
            File dir = new File(from);  
            // Ŀ��  
            to +=  File.separator + dir.getName();  
            File moveDir = new File(to);  
            if(dir.isDirectory()){  
                if (!moveDir.exists()) {  
                    moveDir.mkdirs();  
                }  
            }else{  
                File tofile = new File(to);  
                dir.renameTo(tofile);  
                return;  
            }  
              
            //System.out.println("dir.isDirectory()"+dir.isDirectory());  
            //System.out.println("dir.isFile():"+dir.isFile());  
              
            // �ļ�һ��  
            File[] files = dir.listFiles();  
            if (files == null)  
                return;  
  
            // �ļ��ƶ�  
            for (int i = 0; i < files.length; i++) {  
                System.out.println("�ļ�����"+files[i].getName());  
                if (files[i].isDirectory()) {  
                    MoveFolderAndFileWithSelf(files[i].getPath(), to);  
                    // �ɹ���ɾ��ԭ�ļ�  
                    files[i].delete();  
                }  
                File moveFile = new File(moveDir.getPath() + File.separator + files[i].getName());  
                // Ŀ���ļ����´��ڵĻ���ɾ��  
                if (moveFile.exists()) {  
                    moveFile.delete();  
                }  
                files[i].renameTo(moveFile);  
            }  
            dir.delete();  
        } catch (Exception e) {  
            throw e;  
        }  
    }  
      
    /** 
     * ���Ƶ����ļ�(�ɸ�������) 
     * @param oldPathFile ׼�����Ƶ��ļ�Դ 
     * @param newPathFile �������¾���·�����ļ���(ע��Ŀ¼·������ļ���) 
     * @return 
     */  
    public static void CopySingleFile(String oldPathFile, String newPathFile) {  
        try {  
            int bytesum = 0;  
            int byteread = 0;  
            File oldfile = new File(oldPathFile);  
            if (oldfile.exists()) { //�ļ�����ʱ  
                InputStream inStream = new FileInputStream(oldPathFile); //����ԭ�ļ�  
                FileOutputStream fs = new FileOutputStream(newPathFile);  
                byte[] buffer = new byte[1444];  
                while ((byteread = inStream.read(buffer)) != -1) {  
                    bytesum += byteread; //�ֽ��� �ļ���С  
                    //System.out.println(bytesum);  
                    fs.write(buffer, 0, byteread);  
                }  
                inStream.close();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * ���Ƶ����ļ�(ԭ������) 
     * @param oldPathFile ׼�����Ƶ��ļ�Դ 
     * @param newPathFile �������¾���·�����ļ���(ע��Ŀ¼·������ļ���) 
     * @return 
     */  
    public static void CopySingleFileTo(String oldPathFile, String targetPath) {  
        try {  
            int bytesum = 0;  
            int byteread = 0;  
            File oldfile = new File(oldPathFile);  
            String targetfile = targetPath + File.separator +  oldfile.getName();  
            if (oldfile.exists()) { //�ļ�����ʱ  
                InputStream inStream = new FileInputStream(oldPathFile); //����ԭ�ļ�  
                FileOutputStream fs = new FileOutputStream(targetfile);  
                byte[] buffer = new byte[1444];  
                while ((byteread = inStream.read(buffer)) != -1) {  
                    bytesum += byteread; //�ֽ��� �ļ���С  
                    //System.out.println(bytesum);  
                    fs.write(buffer, 0, byteread);  
                }  
                inStream.close();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * ���������ļ��е�����(������) 
     * @param oldPath ׼��������Ŀ¼ 
     * @param newPath ָ������·������Ŀ¼ 
     * @return 
     */  
    public static void copyFolderWithSelf(String oldPath, String newPath) {  
        try {  
            new File(newPath).mkdirs(); //����ļ��в����� �������ļ���  
            File dir = new File(oldPath);  
            // Ŀ��  
            newPath +=  File.separator + dir.getName();  
            File moveDir = new File(newPath);  
            if(dir.isDirectory()){  
                if (!moveDir.exists()) {  
                    moveDir.mkdirs();  
                }  
            }  
            String[] file = dir.list();  
            File temp = null;  
            for (int i = 0; i < file.length; i++) {  
                if (oldPath.endsWith(File.separator)) {  
                    temp = new File(oldPath + file[i]);  
                } else {  
                    temp = new File(oldPath + File.separator + file[i]);  
                }  
                if (temp.isFile()) {  
                    FileInputStream input = new FileInputStream(temp);  
                    FileOutputStream output = new FileOutputStream(newPath +  
                            "/" +  
                            (temp.getName()).toString());  
                    byte[] b = new byte[1024 * 5];  
                    int len;  
                    while ((len = input.read(b)) != -1) {  
                        output.write(b, 0, len);  
                    }  
                    output.flush();  
                    output.close();  
                    input.close();  
                }  
                if (temp.isDirectory()) { //��������ļ���  
                    copyFolderWithSelf(oldPath + "/" + file[i], newPath);  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}