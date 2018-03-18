package com.fj2889.chatbot;

/**
 * Created by fj2889 on 2018/3/18.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import jxl.*;
import jxl.read.biff.BiffException;


public class Test {

    public static ArrayList<String> ques;


    //读取Excel中的内容
    public List<String> getExcelData(File file){
        List<String> qas=new ArrayList<>();

        try{
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            // 取第一页
            Sheet sheet = wb.getSheet(0);
//             sheet.getRows()返回该页的总行数
            ques = new ArrayList<>();
            for (int i = 0; i < sheet.getRows(); i++) {
                // sheet.getColumns()返回该页的总列数
                String qa="";
                for (int j = 0; j < sheet.getColumns(); j++) {
                    String cellinfo = sheet.getCell(j, i).getContents();
                    cellinfo.trim();
                    qa+=cellinfo;
//                    System.out.println(i+" "+j+" "+cellinfo);
                    if (j==0){
                        ques.add(cellinfo);
                    }
                }
                qas.add(qa);
            }
        }catch (FileNotFoundException e ){
            e.printStackTrace();
        } catch (BiffException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//		System.out.println(qas.get(1));
        return qas;
    }


    public static void main(String[] args){
        Test test = new Test();

        File file = new File("E:\\ChatBot\\Q_A.xls");
        List<String> qas = test.getExcelData(file);

        ArrayList<ArrayList<String>> q_as = new ArrayList<>();

        for (int i=0;i<qas.size();i++) {
            q_as.add(ChatBot.cutWords(qas.get(i)));
        }

        //单个问题测试
//        String q = "哪些人需要办理自行纳税申报";
//        ChatBot chatBot = new ChatBot(q, q_as);
//        int[] position  = chatBot.getPosition();
//        double[] similarities = chatBot.getSimilarities();
//
//        for (int i=0;i<10;i++){
//            System.out.print(String.valueOf(position[i])+" ");
//            System.out.print(similarities[i]);
//            System.out.print(" "+ques.get(position[i])+"\n");
//        }
//
//        System.out.println(ques.get(45));
//        System.out.println(ques.get(50));
//        System.out.println(ques.get(53));
//        System.out.println(ques.get(65));



//        批量问题测试
        for (int i=0;i<ques.size();i++){
            String q = ques.get(i);
            ChatBot chatBot = new ChatBot(q, q_as);
            int[] position  = chatBot.getPosition();
            double[] similarities = chatBot.getSimilarities();

            int flag = 0;
            for (int j=0;j<4;j++) {
                if (i == position[j]) {
                    flag = 1;
                    break;
                }
            }
            if (flag==0){
                System.out.println(String.valueOf(i)+" "+ String.valueOf(similarities[0])+ques.get(i));
            }
        }


    }

}
