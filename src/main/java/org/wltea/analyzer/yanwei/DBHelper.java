package org.wltea.analyzer.yanwei;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.logging.Loggers;
import org.wltea.analyzer.dic.*;
import org.wltea.analyzer.dic.Dictionary;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * author  jhb
 * Date: 2019/4/9
 * Time:11:54
 */
public class DBHelper {
    Logger logger = Loggers.getLogger(DBRunnable.class);
    public static String dbTable = null;
    private Connection conn;
    public static Map<String, Date> lastImportTimeMap = new HashMap<String, Date>();

    static {
        try {
            // 加载Mysql数据驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Properties prop = new Properties();

    private Connection getConn() throws Exception {
        try {
            Dictionary dic = Dictionary.getSingleton();
            Path file = PathUtils.get(dic.getDictRoot(), "jdbc-loadext.properties");
            prop.load(new FileInputStream(file.toFile()));
            logger.info("jdbc-loadext.properties");
            for (Object key : prop.keySet()) {
                logger.info(key + "=" + prop.getProperty(String.valueOf(key)));
                dbTable = prop.getProperty("jdbc.table");
                logger.info("query hot dict from mysql, " + prop.getProperty("jdbc.url") +
                        " ,jdbc.user" + prop.getProperty("jdbc.user") + " ,jdbc.password" + prop.getProperty("jdbc.password") + "......");
                conn = DriverManager.getConnection(
                        prop.getProperty("jdbc.url"),
                        prop.getProperty("jdbc.user"),
                        prop.getProperty("jdbc.password"));
            }
        } catch (Exception e) {
            logger.error("get mysql connection fail", e);
        }
        return conn;
    }

    /**
     * @param key    数据库中的属性 扩展词 停用词 同义词等
     * @param flag
     * @param synony
     * @return
     * @throws Exception
     */

    public String getKey(String key, boolean flag, boolean... synony) throws Exception {
        conn = getConn();
        StringBuilder data = new StringBuilder();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder("select  *  from " + dbTable + "  where delete_type=0");
            //lastImportTime 最新更新时间
            Date lastImportTime = DBHelper.lastImportTimeMap.get(key);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (lastImportTime != null && flag) {
                sql.append(" and update_time > '" + sdf.format(lastImportTime) + "'");
            }

            sql.append(" and " + key + " !=''");
            lastImportTime = new Date();
            lastImportTimeMap.put(key, lastImportTime);
            //如果打印出来的时间 和本地时间不一样，则要注意JVM时区是否和服务器系统时区一致
            logger.warn("sql==={}", sql.toString());
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                String value = rs.getString(key);
                if (StringUtils.isNotBlank(value)) {
                    if (synony != null && synony.length > 0) {
                        data.append(value + "\n");
                    } else {
                        data.append(value + ",");
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();

                }
                if (rs != null) {
                    rs.close();
                }

                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data.toString();
    }
}
