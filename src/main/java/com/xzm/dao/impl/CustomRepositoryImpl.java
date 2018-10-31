package com.xzm.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.xzm.specs.CustomerSpecs.*;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;

import com.xzm.dao.CustomRepository;
import com.xzm.utils.JwtTokenUtil;
import com.xzm.utils.ReflectHelper;
import com.xzm.utils.UUIDUtil;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;



/**
 * 自定义repository的方法接口实现类,该类主要提供自定义的公用方法
 *
 * @param <T>
 * @param <ID>
 */
public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, Serializable>
        implements CustomRepository<T, Serializable> {

    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(CustomRepositoryImpl.class);
    /**
     * 持久化上下文
     */
    private final EntityManager entityManager;

    private JwtTokenUtil jwtTokenUtil;

    public CustomRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    @Override
    public Page<T> findByAuto(T example, Pageable pageable) {
        // TODO Auto-generated method stub
        //调用CustomerSpecs的byAuto
        return findAll(byAuto(entityManager,example),pageable);
    }


    @Override
    public String refreshToken(String oldToken) {
        String token = oldToken.substring("Bearer ".length());
        if (!jwtTokenUtil.isTokenExpired(token)) {
            return jwtTokenUtil.refreshToken(token);
        }
        return "error";
    }

    @Override
    public void store(Object... item) {
        if(null!=item){
            for(Object entity : item){
                innerSave(entity);
            }
        }
    }

    @Override
    public void update(Object... item) {
        if (null != item) {
            for (Object entity : item) {
                entityManager.merge(entity);
            }
        }
    }

    @Override
    public int executeUpdate(String qlString, Object... values) {
        Query query = entityManager.createQuery(qlString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i + 1, values[i]);
            }
        }
        return query.executeUpdate();
    }

    @Override
    public int executeUpdate(String qlString, Map<String, Object> params) {
        Query query = entityManager.createQuery(qlString);
        for (String name : params.keySet()) {
            query.setParameter(name, params.get(name));
        }
        return query.executeUpdate();
    }

    @Override
    public int executeUpdate(String qlString, List<Object> values) {
        Query query = entityManager.createQuery(qlString);
        for (int i = 0; i < values.size(); i++) {
            query.setParameter(i + 1, values.get(i));
        }
        return query.executeUpdate();
    }

    /**
     * 保存对象
     * @param item 保存对象
     * @return
     */
    private Serializable innerSave(Object item) {
        try {
            if(item==null)return null;
            Class<?> clazz = item.getClass();
            Field idField = ReflectHelper.getIdField(clazz);
            Method getMethod = null;
            if(idField!=null){
                Class<?> type = idField.getType();
                Object val = idField.get(item);
                if(type == String.class && (val==null || "".equals(val))){
                    idField.set(item, UUIDUtil.uuid());
                }
            }else{
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    Id id = method.getAnnotation(Id.class);
                    if (id != null) {
                        Object val = method.invoke(item);
                        if(val==null || "".equals(val)){
                            String methodName = "s" + method.getName().substring(1);
                            Method setMethod = clazz.getDeclaredMethod(methodName, method.getReturnType());
                            if(setMethod!=null){
                                setMethod.invoke(item, UUIDUtil.uuid());
                            }
                        }
                        getMethod = method;
                        break;
                    }
                }
            }
            entityManager.persist(item);
            entityManager.flush();
            if(idField!=null){
                return (Serializable) idField.get(item);
            }
            if(getMethod!=null){
                return (Serializable)getMethod.invoke(item);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}