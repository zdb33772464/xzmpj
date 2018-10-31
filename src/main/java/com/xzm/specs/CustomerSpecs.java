package com.xzm.specs;

import static com.google.common.collect.Iterables.toArray;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomerSpecs {

	/**
	 * 定义一个返回为Specification的方法，使用泛型T
	 * 参数是entityManager和当前的包含值作为查询条件的实体对象
	 * @param entityManager
	 * @param example
	 * @param <T>
	 * @return
	 */
	public static <T> Specification<T> byAuto(final EntityManager entityManager,final T example){

		//获取实体对象类型
		final Class<T> type = (Class<T>)example.getClass();
		return new Specification<T>() {
			public Predicate toPredicate(Root<T> root,CriteriaQuery<?> query, CriteriaBuilder cb) {

				//新键Predicate列表存储构造的查询条件
				List<Predicate> predicates = new ArrayList<>();

				//获得EntityType实体类，要根据EntityType获得实体类属性
				EntityType<T> entity = entityManager.getMetamodel().entity(type);
				//循环实体类所有属性
				for(Attribute<T,?> attr:entity.getDeclaredAttributes()) {
					//获取某一个属性值
					Object attrValue = getValue(example,attr);
					if(attrValue != null) {
						if(attr.getJavaType() == String.class) {//属性为字符
							if(!StringUtils.isEmpty(attrValue)) {//字符不为空
								predicates.add(cb.like(root.get(attribute(entity,attr.getName(),String.class)),
										pattern((String) attrValue)));//构造查询条件
							}
						}else {
							predicates.add(cb.equal(root.get(attribute(entity,attr.getName(),attrValue.getClass())),
									attrValue));//其它情况下，构造属性和属性值 equal查询条件，并加到查询列表
						}
					}
				}
				return predicates.isEmpty()?cb.conjunction():cb.and(toArray(predicates, Predicate.class));//转换条件列表为Predicate
				//return predicates.isEmpty()?cb.conjunction():cb.and((Predicate[])predicates.toArray());//转换条件列表为Predicate
				
			}

			//通过反射获得属性值
			private <T> Object getValue(T example,Attribute<T,?> attr) {
				return ReflectionUtils.getField((Field)attr.getJavaMember(), example);
			}

			//获得实体类当前属性的SingularAttribute，SingularAttribute包含实体类某个单独属性
			private <E,T> SingularAttribute<T,E> attribute(EntityType<T> entity,String fieldName,Class<E> fieldClass){
				return entity.getDeclaredSingularAttribute(fieldName, fieldClass);
			}
		};
	}

	//构造like查询模式
	static private String pattern(String str) {
		return "%"+str+"%";
	}

}
