package com.test.utils.file;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 2018/3/8.
 */
public class FileUtil {

    public static boolean writeCSVFile(String paths,String fileName,String content){
        try
        {
            if (!Files.exists(Paths.get(paths+fileName))) {
                Files.createFile(Paths.get(paths+fileName));
            }
            Files.write(Paths.get(paths+fileName), content.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static boolean encryptZip(String srcPath,String password,String dstPath) {
        try {
            if(!new File(srcPath).exists()) {
                return false;
            }
            ZipParameters parameters = new ZipParameters();
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(password.toCharArray());
            File srcFile = new File(srcPath);
            ZipFile destFile = new ZipFile(dstPath);
            if(srcFile.isDirectory()) {
                destFile.addFolder(srcFile, parameters);
            } else {
                destFile.addFile(srcFile, parameters);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *根据需求,直接调用静态方法start来执行操作
     *参数:
     *  rows 为多少行一个文件 int 类型
     *  sourceFilePath 为源文件路径 String 类型
     *  targetDirectoryPath 为文件分割后存放的目标目录 String 类型
     *  ---分割后的文件名为索引号(从0开始)加'_'加源文件名,例如源文件名为test.txt,则分割后文件名为0_test.txt,以此类推
     */
    public static Integer split(String sourceFilePath,int rows) throws Exception{
        int idx = sourceFilePath.lastIndexOf(".");
        String targetDirectoryPath = sourceFilePath.substring(0,idx);
        return FileUtil.split(sourceFilePath,rows,targetDirectoryPath);
    }

    public static Integer split(String sourceFilePath,int rows,String targetDirectoryPath) throws Exception{
        File sourceFile = new File(sourceFilePath);
        File targetFile = new File(targetDirectoryPath);

        if(!sourceFile.exists()||rows<=0||sourceFile.isDirectory()){
            throw new Exception("源文件不存在或者输入了错误的行数");
        }
        if(targetFile.exists()){
            if(!targetFile.isDirectory()){
                throw new Exception("目标文件夹错误,不是一个文件夹");
            }
        }else{
            targetFile.mkdirs();
        }

        InputStreamReader inStream = new InputStreamReader(new FileInputStream(sourceFile), "GBK");
        BufferedReader br = new BufferedReader(inStream);
        BufferedWriter bw = null;
        StringBuffer sb = new StringBuffer("");
        String tempData = br.readLine();

        int i=1,s=0;
        while(tempData!=null){
            sb.append(tempData+"\r\n");
            if(i%rows==0){
                bw = new BufferedWriter(new FileWriter(new File(targetFile.getAbsolutePath()+"/"+s+"_"+sourceFile.getName())));
                bw.write(sb.toString());
                bw.close();
                sb.setLength(0);
                s += 1;
            }
            i++;
            tempData = br.readLine();
        }
        if((i-1)%rows!=0){
            bw = new BufferedWriter(new FileWriter(new File(targetFile.getAbsolutePath()+"/"+s+"_"+sourceFile.getName())));
            bw.write(sb.toString());
            bw.close();
            br.close();
            s += 1;
        }
        System.out.println("文件分割结束,共分割成了"+s+"个文件");
        return s;
    }


    //测试
    public static void main(String args[]){
        try {
            int num = FileUtil.split("G:/test/test.txt",20);
            System.out.println("________________________________________________________________________\n");
            System.out.println(num);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
