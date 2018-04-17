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
第一步创建了一个AnnotatedGenericBeanDefinition的实例，这个东西是啥呢。
首先他是一个beanDefinition。
一个BeanDefinition描述了一个bean的实例，包括属性值，构造方法参数值和继承自它的类的更多信息。
BeanDefinition仅仅是一个最简单的接口，主要功能是允许BeanFactoryPostProcessor 例如PropertyPlaceHolderConfigure能够检索并修改属性值和别的bean的元数据。

生成之后执行abd.setInstanceSupplier(instanceSupplier);这个看起来是一个实例提供者，可以自定义用来自己代理bean？该方法中是null。
metadata暂且理解成properties，就当它是一堆配置，约束信息。
往下走ScopeMetadata.getName().equals("singleton")，emmm，看起来是个bean生成规则。

继续走到AnnotationConfigUtils.processCommonDefinitionAnnotations(abd)。这个方法中在启动类上寻找5个注解：Lazy、Primary、DependsOn、Role、Description并且set到annotatedBeanDefinition中。先不管他，反正一个没有。

接下来定义了一个BeanDefinitionHolder。顾名思义他是用来映射类名和类定义的可看成一个“map”。
然后走到最后一步BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);这个方法看起来是最终注册的方法了。

结论是register方法只是将启动类注册到了上下文（beanMap）中。真正将其他bean注册进去的方法在refresh()里。

===========================================================================================

先把代码贴上来。。AbstractApplicationContext.refresh()
``` java
    @Override
    public void refresh() throws BeansException, IllegalStateException {
        synchronized (this.startupShutdownMonitor) {
            // 准备这个上下文用来refreshing
            // Prepare this context for refreshing.
            prepareRefresh();
            // 告诉子类刷新内部bean工厂
            // Tell the subclass to refresh the internal bean factory.
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
            // 准备在这上下文中使用的bean工厂
            // Prepare the bean factory for use in this context.
            prepareBeanFactory(beanFactory);
            try {
                // 允许在上下文子类中对bean工厂进行后处理
                // Allows post-processing of the bean factory in context subclasses.
                postProcessBeanFactory(beanFactory);
                // 在上下文中调用注册为bean的工厂处理器
                // Invoke factory processors registered as beans in the context.
                invokeBeanFactoryPostProcessors(beanFactory);
                // 注册拦截bean创建的bean处理器
                // Register bean processors that intercept bean creation.
                registerBeanPostProcessors(beanFactory);
                // 初始化此上下文的消息源
                // Initialize message source for this context.
                initMessageSource();
                // 初始化此上下文的事件广播器
                // Initialize event multicaster for this context.
                initApplicationEventMulticaster();
                // 在特定的上下文子类中初始化其他特殊的bean
                // Initialize other special beans in specific context subclasses.
                onRefresh();
                // 检查监听器bean并注册它们
                // Check for listener beans and register them.
                registerListeners();
                // 实例化所有剩下的（非懒加载）单例
                // Instantiate all remaining (non-lazy-init) singletons.
                finishBeanFactoryInitialization(beanFactory);
                // 最后一步：发布相应的事件
                // Last step: publish corresponding event.
                finishRefresh();
            }
            catch (BeansException ex) {
                // 销毁已经创建的单例对象以避免资源浪费
                // Destroy already created singletons to avoid dangling resources.
                destroyBeans();
                // 重置“有效”标志
                // Reset 'active' flag.
                cancelRefresh(ex);
                // 向呼叫者传播异常
                // Propagate exception to caller.
                throw ex;
            }
            finally {
                // 在Spring的核心中重置常见的"反省"缓存，因为我们可能不再需要单例bean的元数据了
                // Reset common introspection caches in Spring's core, since we
                // might not ever need metadata for singleton beans anymore...
                resetCommonCaches();
            }
        }
    }
```
