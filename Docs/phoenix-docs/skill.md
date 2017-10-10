 create function FACECOMP(varchar,varchar) returns decimal as 'com.hzgc.phoenix.FaceCompFunc' using jar 'hdfs://172.18.18.135:9000/phoenix/jars/phoenix_function-1.0.jar';
 
 
 create table "objectinfo"("id" char(25) not null primary key, "person"."name" varchar, "person"."platformid" varchar, "person"."tag" varchar, "person"."pkey" varchar, "person"."idcard" varchar, "person"."sex" integer, "person"."photo" varchar, "person"."feature" varchar, "person"."reason" varchar, "person"."creator" varchar, "person"."cphone" varchar, "person"."createtime" date, "person"."updatetime" date);