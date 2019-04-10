package org.wltea.analyzer.yanwei;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.Loggers;
import org.wltea.analyzer.dic.Dictionary;

import java.util.Arrays;
import java.util.List;

/**
 * @author jhb
 *         Date: 2019/4/9
 *         Time:11:56
 */
public class DBRunnable implements Runnable {
    Logger logger = Loggers.getLogger(DBRunnable.class);
    private String extField;
    private String stopField;


    public DBRunnable(String extField, String stopField) {
        super();
        this.extField = extField;
        this.stopField = stopField;
    }


    @Override
    public void run() {
        logger.warn("开始加载词库========");
        //获取词库
        Dictionary dic = Dictionary.getSingleton();
        DBHelper dbHelper = new DBHelper();
        try {
            String extWords = dbHelper.getKey(extField, true);
            String stopWords = dbHelper.getKey(stopField, true);
            if (StringUtils.isNotBlank(extWords)) {
                List<String> extList = Arrays.asList(extWords.split(","));
                //把扩展词加载到主词库中
                dic.addWords(extList);
                logger.warn("加载扩展词成功========");
                logger.warn("extWords为==={}", extWords);
            }
            if (StringUtils.isNotBlank(stopWords)) {
                List<String> stopList = Arrays.asList(stopWords.split(","));
                //把扩展词加载到主词库中
                dic.addStopWords(stopList);
                logger.warn("加载停用词成功========");
                logger.warn("stopWords为==={}", stopWords);
            }
        } catch (Exception e) {
            logger.warn("加载扩展词失败========{}", e);
        }

    }
}
