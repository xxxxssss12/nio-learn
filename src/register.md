# new AnnotationConfigApplicationContext(Application.class) 学习

``` java
    /**
     * Create a new AnnotationConfigApplicationContext, deriving bean definitions
     * from the given annotated classes and automatically refreshing the context.
     * @param annotatedClasses one or more annotated classes,
     * e.g. {@link Configuration @Configuration} classes
     */
    public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
        this.reader = new AnnotatedBeanDefinitionReader(this); // reader即解析配置？
        this.scanner = new ClassPathBeanDefinitionScanner(this);    // scanner用来扫描包？
        register(annotatedClasses); // 注册该class
        refresh();  // 刷新
    }
```
对于refresh方法定义如下：（emmmm日后再翻译）
``` java
	/**
	 * Load or refresh the persistent representation of the configuration,
	 * which might an XML file, properties file, or relational database schema.
	 * <p>As this is a startup method, it should destroy already created singletons
	 * if it fails, to avoid dangling resources. In other words, after invocation
	 * of that method, either all or no singletons at all should be instantiated.
	 * @throws BeansException if the bean factory could not be initialized
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * attempts are not supported
	 */
	 谷歌翻译：
    加载或刷新配置的持久表示，这可能是XML文件，属性文件或关系数据库模式。
    由于这是一种启动方法，因此它应该销毁已经创建的单例（如果它失败），以避免悬挂资源。 
    换句话说，在调用该方法之后，全部或者全部都不应该被实例化。
    如果bean工厂不能被初始化，则抛出BeansException
    如果已经初始化并且不支持多次刷新尝试，则抛出IllegalStateException
```

register方法是注册这个class的方法，往里面走走主要看这个方法：
``` java
    /**
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations.
     * @param annotatedClass the class of the bean
     * @param instanceSupplier a callback for creating an instance of the bean
     * (may be {@code null})
     * @param name an explicit name for the bean
     * @param qualifiers specific qualifier annotations to consider, if any,
     * in addition to qualifiers at the bean class level
     * @param definitionCustomizers one or more callbacks for customizing the
     * factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
     * @since 5.0
     */
    <T> void doRegisterBean(Class<T> annotatedClass, @Nullable Supplier<T> instanceSupplier, @Nullable String name,
            @Nullable Class<? extends Annotation>[] qualifiers, BeanDefinitionCustomizer... definitionCustomizers) {
    
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(annotatedClass);
        if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
            return;
        }
    
        abd.setInstanceSupplier(instanceSupplier);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
    
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        if (qualifiers != null) {
            for (Class<? extends Annotation> qualifier : qualifiers) {
                if (Primary.class == qualifier) {
                    abd.setPrimary(true);
                }
                else if (Lazy.class == qualifier) {
                    abd.setLazyInit(true);
                }
                else {
                    abd.addQualifier(new AutowireCandidateQualifier(qualifier));
                }
            }
        }
        for (BeanDefinitionCustomizer customizer : definitionCustomizers) {
            customizer.customize(abd);
        }
    
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
    }
```