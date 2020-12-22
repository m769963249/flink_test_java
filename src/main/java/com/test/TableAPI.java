/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test;


import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import static org.apache.flink.table.api.Expressions.$;

/**
 * Skeleton for a Flink Batch Job.
 *
 * <p>For a tutorial how to write a Flink batch application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution,
 * change the main class in the POM.xml file to this class (simply search for 'mainClass')
 * and run 'mvn clean package' on the command line.
 */
public class TableAPI {
	public static void main(String[] args) throws Exception {


		// set up the batch execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		BatchTableEnvironment tEnv = BatchTableEnvironment.create(env);
		DataSource<String> source = env.fromElements(
				"java python hello word spark spark java java python"
		);
		FlatMapOperator<String, Tuple2<String, Integer>> stringTuple2FlatMapOperator = source.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
			@Override
			public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
				for (String word : s.split(" ")) {
					collector.collect(new Tuple2<>(word, 1));

				}
			}
		});
		Table table = tEnv.fromDataSet(stringTuple2FlatMapOperator,"word,count");
//		GroupedTable word = table.select("word,count(1)").groupBy("word");
		Table select = table.groupBy($("word")).select($("word"),$("count").count().as("cnt"));
		DataSet<Row> rowDataSet = tEnv.toDataSet(select, Row.class);
		rowDataSet.print();


	}
}
