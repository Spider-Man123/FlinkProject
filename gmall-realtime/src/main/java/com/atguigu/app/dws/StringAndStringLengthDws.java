package com.atguigu.app.dws;

import com.atguigu.app.function.StringAndStringLength;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
class res{
    String s;
}
public class StringAndStringLengthDws {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> data = env.fromElements("word,ssss", "aaa,aaa");
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);
        Table str = tableEnvironment.fromDataStream(data);
        str.printSchema();
        String sql2="select * from +"+str;
        Table table1 = tableEnvironment.sqlQuery(sql2);
        //tableEnvironment.fromDataStream(table1).print();;
//        tableEnvironment.createTemporarySystemFunction("stringAndLength", StringAndStringLength.class);
//        String sql="select s from "+str+", LATERAL TABLE(stringAndLength(f0))";
//        Table table = tableEnvironment.sqlQuery(
//                sql
//        );
//        tableEnvironment.toAppendStream(table,res.class).print();
        env.execute();
    }
}
