package fr.utc.mylottery.dbrouter.interceptor;

import fr.utc.mylottery.dbrouter.annotation.DBRouter;
import fr.utc.mylottery.dbrouter.config.DBContext;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 用于拦截MyBatis的SQL执行，实现动态表名替换的功能
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class MybatisInterceptor implements Interceptor {

    private Pattern pattern = Pattern.compile("(from|into|update)[\\s]{1,}(\\w{1,})", Pattern.CASE_INSENSITIVE);
    private Logger logger = LoggerFactory.getLogger(MybatisInterceptor.class);
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // 获取StatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        // id为执行的mapper方法的全路径名
        String id = mappedStatement.getId();

        // 检查是否有@DBRouter注解
        String className = id.substring(0, id.lastIndexOf("."));
        String methodName = id.substring(id.lastIndexOf(".") + 1);

        // 获取接口类的 Class 对象
        Class<?> interfaceClass = Class.forName(className);

        // 获取方法对象
        Method targetMethod = null;
        for (Method method : interfaceClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                targetMethod = method;
                break;
            }
        }
        logger.info("method {},{}", targetMethod.getName(), targetMethod.isAnnotationPresent(DBRouter.class));
        if (targetMethod != null && targetMethod.isAnnotationPresent(DBRouter.class)) {
            // 执行动态表名替换逻辑
            // 获取SQL
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = boundSql.getSql();
            logger.info("sql:{}", sql);

            // 替换SQL表名
            Matcher matcher = pattern.matcher(sql);
            String tableName = null;
            if (matcher.find()) {
                tableName = matcher.group().trim();
            }
            assert null != tableName;
            logger.info("? :{}", DBContext.getTBKey());
            String replaceSql = matcher.replaceAll(tableName + "_" + DBContext.getTBKey());

            // 通过反射修改SQL语句
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, replaceSql);
            field.setAccessible(false);
        }

        return invocation.proceed();
    }

}