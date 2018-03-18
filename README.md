# TfIDF-based ChatBot
提出一个问题，此包通过分词以及TF-IDF算法从已有问答集中找出最符合的几组问答集

并按相似度排序

所有需要的jar包都已经在`ChatBot_jar.rar`中了



## 效果

测试问答集一共有95组四川大学教务处问答

如果按照原问题输入：

* 设置容错率为0，即返回的`position`数组的第一个值即为正确答案 错误率为7/95
* 设置容错率为1，即返回的`position`数组的前两个包含正确答案的index，错误率为2/95
* 设置容错率为2，错误率为2/95
* 设置容错率为3，错误率为0/95  即全部通过

如果不按照原问题输入，目前还没有比较好的方法测试

手工测试结果为，改变句子，只要包含问题中的所有关键词，效果还是比较好的

## 导入jar包

导入之后需要`import com.fj2889.chatbot.*;`

## 接口

### 创建ChatBot实例

唯一的构造函数`public ChatBot(String q, ArrayList<ArrayList<String>> q_as)`

需要传入的参数为**一个未分词的问题文本** `q`和 **已经分好词的问答集** `q_as`每一行为一个问题和对应答案的分词结果 类型为`ArrayList<ArrayList<String>>`  

```java
ArrayList<ArrayList<String>> q_as = new ArrayList<>();

        for (int i=0;i<qas.size();i++) {
            q_as.add(ChatBot.cutWords(qas.get(i)));
        }

        //单个问题测试
        String q = "图书怎么报账？";
        ChatBot chatBot = new ChatBot(q, q_as);
```

### 分词接口

静态函数  外部可不实例化直接调用分词函数

`public static ArrayList<String> cutWords(String text)`

只需传入需要分词的`String`文本即可

```java
for (int i=0;i<qas.size();i++) {
            q_as.add(ChatBot.cutWords(qas.get(i)));
        }
```

### 最匹配问答集

在实例化之后可通过`public double[] getSimilarities()`及`public int[] getPosition()`分别取得**已经排序**的相似度数组以及对应问答集位置的位置数组

例如`Simlarities[0]`表示相似度最大的值是多少

而`position[0]`表示与输入问题相似度最大的一个问答在原问答集中的索引值

```java
//单个问题测试
        String q = "我想报账图书？";
        ChatBot chatBot = new ChatBot(q, q_as);
        int[] position  = chatBot.getPosition();
        double[] similarities = chatBot.getSimilarities();

        for (int i=0;i<5;i++){
            System.out.print(String.valueOf(position[i])+" ");
            System.out.print(similarities[i]);
            System.out.print(" "+ques.get(position[i])+"\n");
        }
```

