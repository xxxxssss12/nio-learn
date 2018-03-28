# AbstractApplicationContext.getBean(Class<T> clazz)方法阅读
``` java
public <T> T getBean(Class<T> requiredType) throws BeansException {
   assertBeanFactoryActive();
   return getBeanFactory().getBean(requiredType);
}
```
其中的beanFactory是DefaultListableBeanFactory。
进入方法：
``` java
@Override
public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
   NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args);
   if (namedBean != null) {
      return namedBean.getBeanInstance();
   }
   BeanFactory parent = getParentBeanFactory();
   if (parent != null) {
      return (args != null ? parent.getBean(requiredType, args) : parent.getBean(requiredType));
   }
   throw new NoSuchBeanDefinitionException(requiredType);
}
```
传参中的args都看成null即可。
可以看到执行逻辑：先找有没有初始化过这个bean，如果有则直接返回实例，否则去父类的beanFactory中继续获取。
然后是resolveNamedBean方法，返回了一个NamedBeanHolder，其类解释为：A simple holder for a given bean name plus bean instance。简单可以理解为一个Map，通过name映射bean实例
（<font color=red>这个比较关键</font>）
继续贴代码：
``` java
    @SuppressWarnings("unchecked")
    @Nullable
    private <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
        Assert.notNull(requiredType, "Required type must not be null");
        String[] candidateNames = getBeanNamesForType(requiredType);// 根据class类型获取所有可被aop的bean名称
    
        if (candidateNames.length > 1) {
            List<String> autowireCandidates = new ArrayList<>(candidateNames.length);
            for (String beanName : candidateNames) {
                if (!containsBeanDefinition(beanName) || getBeanDefinition(beanName).isAutowireCandidate()) {
                    autowireCandidates.add(beanName);
                }
            }
            if (!autowireCandidates.isEmpty()) {
                candidateNames = StringUtils.toStringArray(autowireCandidates);
            }
        }
    
        if (candidateNames.length == 1) {
            String beanName = candidateNames[0];
            return new NamedBeanHolder<>(beanName, getBean(beanName, requiredType, args));
        }
        else if (candidateNames.length > 1) {
            Map<String, Object> candidates = new LinkedHashMap<>(candidateNames.length);
            for (String beanName : candidateNames) {
                if (containsSingleton(beanName) && args == null) {
                    Object beanInstance = getBean(beanName);
                    candidates.put(beanName, (beanInstance instanceof NullBean ? null : beanInstance));
                }
                else {
                    candidates.put(beanName, getType(beanName));
                }
            }
            String candidateName = determinePrimaryCandidate(candidates, requiredType);
            if (candidateName == null) {
                candidateName = determineHighestPriorityCandidate(candidates, requiredType);
            }
            if (candidateName != null) {
                Object beanInstance = candidates.get(candidateName);
                if (beanInstance == null || beanInstance instanceof Class) {
                    beanInstance = getBean(candidateName, requiredType, args);
                }
                return new NamedBeanHolder<>(candidateName, (T) beanInstance);
            }
            throw new NoUniqueBeanDefinitionException(requiredType, candidates.keySet());
        }
    
        return null;
    }
```