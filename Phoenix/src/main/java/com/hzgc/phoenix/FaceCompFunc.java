package com.hzgc.phoenix;


import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.phoenix.expression.Expression;
import org.apache.phoenix.expression.LiteralExpression;
import org.apache.phoenix.expression.function.ScalarFunction;
import org.apache.phoenix.parse.FunctionParseNode.Argument;
import org.apache.phoenix.parse.FunctionParseNode.BuiltInFunction;
import org.apache.phoenix.schema.tuple.Tuple;
import org.apache.phoenix.schema.types.PDataType;
import org.apache.phoenix.schema.types.PFloat;
import org.apache.phoenix.schema.types.PVarchar;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;

// 定义函数名，传过来的参数个数以及参数的数据类型
@BuiltInFunction(name = FaceCompFunc.NAME,  args = {
        @Argument(allowedTypes = {PVarchar.class}),
        @Argument(allowedTypes = {PVarchar.class})})
public class FaceCompFunc extends ScalarFunction {
    Logger LOG = Logger.getLogger(FaceCompFunc.class);
    public static final String NAME = "FACECOMP";

    private String thePassStr = null;

    public FaceCompFunc(){}

    public FaceCompFunc(List<Expression> children) throws SQLException{
        super(children);
        init();
    }

    // 初始化操作，读取传过来的照片
    private void init() {
        LOG.info("start init............");
        Expression strToSearchExpression = getChildren().get(1);
        if (strToSearchExpression instanceof LiteralExpression) {
            Object strToSearchValue = ((LiteralExpression) strToSearchExpression).getValue();
            if (strToSearchValue != null) {
                this.thePassStr = strToSearchValue.toString();
            }
        }
    }

    // 返回函数名，让其可以使用Phoenix 的排序功能
    @Override
    public String getName() {
        return NAME;
    }

    // 主要的实现方法
    @Override
    public boolean evaluate(Tuple tuple, ImmutableBytesWritable ptr) {
        Expression child = getChildren().get(0);

        if (!child.evaluate(tuple, ptr)) {
            return false;
        }

        float related;
        if (ptr.getLength() == 0) {
            related = 0f;
            ptr.set(PFloat.INSTANCE.toBytes(related));
            return true;
        }

        //Logic for Empty string search
        if (thePassStr == null){
            related = 0f;
            ptr.set(PFloat.INSTANCE.toBytes(related));
            return true;
        }

        String sourceStr = (String) PVarchar.INSTANCE.toObject(ptr, getChildren().get(0).getSortOrder());

        related = featureCompare(thePassStr, sourceStr);

        ptr.set(PFloat.INSTANCE.toBytes(related));
        return true;
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        super.readFields(input);
        init();
    }

    //自定义函数的返回值
    @Override
    public PDataType getDataType() {
        return PFloat.INSTANCE;
    }

    public static float featureCompare(String currentFeatureStr, String historyFeatureStr) {
        float[] currentFeature = string2floatArray(currentFeatureStr);
        float[] historyFeature = string2floatArray(historyFeatureStr);
        return featureCompare(currentFeature, historyFeature);
    }

    public static float featureCompare(float[] currentFeature, float[] historyFeature) {
        double similarityDegree = 0;
        double currentFeatureMultiple = 0;
        double historyFeatureMultiple = 0;
        for (int i = 0; i < currentFeature.length; i++) {
            similarityDegree = similarityDegree + currentFeature[i] * historyFeature[i];
            currentFeatureMultiple = currentFeatureMultiple + Math.pow(currentFeature[i], 2);
            historyFeatureMultiple = historyFeatureMultiple + Math.pow(historyFeature[i], 2);
        }

        double tempSim = similarityDegree / Math.sqrt(currentFeatureMultiple) / Math.sqrt(historyFeatureMultiple);
        double actualValue = new BigDecimal((0.5 + (tempSim / 2)) * 100).
                setScale(2, BigDecimal.ROUND_HALF_UP).
                doubleValue();
        if (actualValue >= 100) {
            return 100;
        }
        return (float) actualValue;
    }

    private static final String SPLIT = ":";

    /**
     * 将特征值（float[]）转换为字符串（String）（内）（赵喆）
     *
     * @param feature 传入float[]类型的特征值
     * @return 输出指定编码为UTF-8的String
     */
    public static String floatArray2string(float[] feature) {
        if (feature != null && feature.length == 512) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < feature.length; i++) {
                if (i == 511) {
                    sb.append(feature[i]);
                } else {
                    sb.append(feature[i]).append(":");
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 将特征值（String）转换为特征值（float[]）（内）（赵喆）
     *
     * @param feature 传入编码为UTF-8的String
     * @return 返回float[]类型的特征值
     */
    public static float[] string2floatArray(String feature) {
        if (feature != null && feature.length() > 0) {
            float[] featureFloat = new float[512];
            String[] strArr = feature.split(SPLIT);
            for (int i = 0; i < strArr.length; i++) {
                featureFloat[i] = Float.valueOf(strArr[i]);
            }
            return featureFloat;
        }
        return new float[0];
    }

    /**
     * 将byte[]型特征转化为float[]
     *
     * @param fea byte[]型特征
     * @return float[]
     */
    public static float[] byteArr2floatArr(byte[] fea) {
        if (null != fea && fea.length > 0) {
            return string2floatArray(new String(fea));
        }
        return new float[0];
    }
}
