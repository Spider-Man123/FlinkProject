package com.atguigu.app.dws;

import com.atguigu.bean.ProvinceStats;
import com.atguigu.utils.ClickHouseUtil;
import com.atguigu.utils.MyKafkaUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

//数据流：web/app -> nginx -> SpringBoot -> Mysql -> FlinkApp -> Kafka(ods) -> FlinkApp -> Kafka/Phoenix(dwd-dim) -> FlinkApp(redis) -> Kafka(dwm) -> FlinkApp -> ClickHouse
//程  序：         MockDb               -> Mysql -> FlinkCDC -> Kafka(ZK) -> BaseDbApp -> Kafka/Phoenix(zk/hdfs/hbase) -> OrderWideApp(Redis) -> Kafka -> ProvinceStatsSqlApp -> ClickHouse
public class ProvinceStatsSqlApp {

    public static void main(String[] args) throws Exception {

        //TODO 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //1.1 设置CK&状态后端
        //env.setStateBackend(new FsStateBackend("hdfs://hadoop102:8020/gmall-flink-210325/ck"));
        //env.enableCheckpointing(5000L);
        //env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //env.getCheckpointConfig().setCheckpointTimeout(10000L);
        //env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        //env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);

        //env.setRestartStrategy(RestartStrategies.fixedDelayRestart());

        //TODO 2.使用DDL创建表 提取时间戳生成WaterMark
        String groupId = "province_stats";
        String orderWideTopic = "dwm_order_wide";
        tableEnv.executeSql("CREATE TABLE order_wide ( " +
                "  `province_id` BIGINT, " +
                "  `province_name` STRING, " +
                "  `province_area_code` STRING, " +
                "  `province_iso_code` STRING, " +
                "  `province_3166_2_code` STRING, " +
                "  `order_id` BIGINT, " +
                "  `split_total_amount` DECIMAL, " +
                "  `create_time` STRING, " +
                "  `rt` as TO_TIMESTAMP(create_time), " +
                "  WATERMARK FOR rt AS rt - INTERVAL '1' SECOND ) with(" +
                MyKafkaUtil.getKafkaDDL(orderWideTopic, groupId) + ")");

        //TODO 3.查询数据  分组、开窗、聚合
        Table table = tableEnv.sqlQuery("select " +
                "    DATE_FORMAT(TUMBLE_START(rt, INTERVAL '10' SECOND), 'yyyy-MM-dd HH:mm:ss') stt, " +
                "    DATE_FORMAT(TUMBLE_END(rt, INTERVAL '10' SECOND), 'yyyy-MM-dd HH:mm:ss') edt, " +
                "    province_id, " +
                "    province_name, " +
                "    province_area_code, " +
                "    province_iso_code, " +
                "    province_3166_2_code, " +
                "    count(distinct order_id) order_count, " +
                "    sum(split_total_amount) order_amount, " +
                "    UNIX_TIMESTAMP()*1000 ts " +
                "from " +
                "    order_wide " +
                "group by " +
                "    province_id, " +
                "    province_name, " +
                "    province_area_code, " +
                "    province_iso_code, " +
                "    province_3166_2_code, " +
                "    TUMBLE(rt, INTERVAL '10' SECOND)");

        //TODO 4.将动态表转换为流
        DataStream<ProvinceStats> provinceStatsDataStream = tableEnv.toAppendStream(table, ProvinceStats.class);

        //TODO 5.打印数据并写入ClickHouse
        provinceStatsDataStream.print();
        provinceStatsDataStream.addSink(ClickHouseUtil.getSink("insert into province_stats_210325 values(?,?,?,?,?,?,?,?,?,?)"));

        //TODO 6.启动任务
        env.execute("ProvinceStatsSqlApp");

    }

}
