package com.fj2889.chatbot;

/**
 * Created by fj2889 on 2018/3/18.
 */
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;


import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.lang.Math;

public class ChatBot {

    private String q;             //提的问题
    private ArrayList<ArrayList<String>> q_as;             //所有的问答集    以每行一个问答  分好词的形式
    private int word_count;        //所有不同词汇总数
    private int[] position;
    private double[] similarities;

    public ChatBot(String q, ArrayList<ArrayList<String>> q_as){
        this.q = q;
        this.q_as = q_as;
        q_as.add(ChatBot.cutWords(q));
        word_count = 0;
        position = new int[q_as.size()-1];
        similarities = new double[q_as.size()-1];

        tfIdf();
    }


    public static ArrayList<String> cutWords(String text){

//        String text="基于java语言开发的轻量级的中文分词工具包";
        ArrayList<String> words = new ArrayList<>();
        //创建分词对象
        Analyzer anal=new IKAnalyzer(true);
        StringReader reader=new StringReader(text);
        //分词
        TokenStream ts=anal.tokenStream("", reader);
        CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);
        //遍历分词数据
        try {
            while(ts.incrementToken()){
//                System.out.print(term.toString()+" ");
                words.add(term.toString().trim());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        reader.close();
//        System.out.println("test");

//        for (int i=0;i<words.size();i++){
//            System.out.print(words.get(i)+ " ");
//        }

        return words;
    }

    private HashMap getWordFeature(){
        HashMap<String,Integer> word_feature = new HashMap<>();
        int count = 0;
//        for (int i=q_as.size()-1;i<q_as.size();i++){
        for (int i=0;i<q_as.size();i++){
            for (int j=0;j<q_as.get(i).size();j++){
                String word = q_as.get(i).get(j);
                if (!word_feature.containsKey(word)){
                    word_feature.put(word,count++);
                }
            }

        }
//        System.out.print(word_feature);
        return word_feature;
    }

    private int[][] getFrequencyMatrix(HashMap word_feature){
        int[][] frequencyMatrix = new int[q_as.size()][word_count];
        for (int i=0;i<q_as.size();i++){
            for (int j=0;j<q_as.get(i).size();j++){
                String word = q_as.get(i).get(j);
                int position = (Integer)word_feature.get(word);
                frequencyMatrix[i][position]++;
            }
        }

        return frequencyMatrix;
    }

    private double[][] calTf(int[][] frequencyMatrix){
        double[][] tf = new double[q_as.size()][word_count];
        for (int i=0;i< q_as.size();i++){
            for (int j=0;j<word_count;j++){
                tf[i][j] = frequencyMatrix[i][j]*1.0/q_as.get(i).size();
            }
        }

        return tf;
    }

    private double[][] calIdf(int[][] frequencyMatrix){
        double[][] idf = new double[q_as.size()][word_count];
        for (int i=0;i< q_as.size();i++){
            for (int j=0;j<word_count;j++){
                //统计当前单词在多少个问答集中存在
                int count =0;
                for (int k=0;k<q_as.size();k++){
                    if (frequencyMatrix[k][j]!=0) {
                        count++;
                    }
                }
                idf[i][j] =Math.log(q_as.size()/(1+count)) ;
            }
        }
        return idf;
    }

    private  double dot_product(double[] v1, double[] v2){
        double sum=0;
        for (int i=0;i<v1.length;i++){
            sum+=v1[i]*v2[i];
        }
        return sum;
    }

    private double magnitude(double[] v){
        return Math.sqrt(dot_product(v,v));
    }

    private void tfIdf(){

//        System.out.print(q_as.get(q_as.size()-1));


        //统计words向量
        HashMap word_feature = getWordFeature();
        word_count = word_feature.size();

        //计算词频矩阵
        int[][] frequencyMatrix = getFrequencyMatrix(word_feature);

        //计算tf
        double[][] tf = calTf(frequencyMatrix);

        //计算idf
        double[][] idf = calIdf(frequencyMatrix);

        //计算tf-idf
        double[][] tf_idf = new double[q_as.size()][word_count];
        for (int i=0;i<q_as.size();i++){
            for (int j=0;j<word_count;j++) {
                tf_idf[i][j] = tf[i][j] * idf[i][j];
            }
        }

        //similarities
        double[] v2 = tf_idf[q_as.size()-1];
        for (int i=0;i<q_as.size()-1;i++){
            double[] v1 = tf_idf[i];
            double similarity = dot_product(v1,v2)/(magnitude(v1)*magnitude(v2)+0.00000001);
            similarities[i]=similarity;
        }


        for (int i=0;i<q_as.size()-1;i++){
            position[i]=i;
        }
        for (int i=0;i<q_as.size()-1;i++){
            for (int j=i+1;j<q_as.size()-1;j++){
                if (similarities[i]<similarities[j]){
                    double a = similarities[i];
                    similarities[i] = similarities[j];
                    similarities[j] = a;
                    int b = position[i];
                    position[i]=position[j];
                    position[j] = b;
                }
            }
        }

        //由于传入的数组是引用所以将添加到最后的问题移除
        q_as.remove(q_as.size()-1);
    }

    public double[] getSimilarities(){
        return similarities;
    }
    public int[] getPosition(){
        return position;
    }
    public static void main(String[] args){

    }
}
