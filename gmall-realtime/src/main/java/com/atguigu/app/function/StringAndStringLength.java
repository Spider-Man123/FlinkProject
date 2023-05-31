package com.atguigu.app.function;

import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

@FunctionHint(output=@DataTypeHint("Row<s STRING>"))
public class StringAndStringLength extends TableFunction<Row> {
    public void eval(String s){
        for(String fen:s.split(","))
            collect(Row.of(fen));
    }
}
