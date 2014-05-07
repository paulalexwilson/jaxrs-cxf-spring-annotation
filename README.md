Java/Annotation Driven Spring Configuration for JAX-RS
======================================================

Proof of concept for the configuration of JAX-RS servers (to be expanded to include clients) using Spring's annotation-driven @Configuration style. 

Motivation
==========

Since its 3rd major release, Spring has shipped with support for code-centric container configuration alongside XML. This configuration style brings many advantages including:

 * Type-safe configuration
 * Editor refactoring support
 * Improved navigability
 * Opporunities for configuration APIs

Besides the more obviously advantages of type-safe configuration, easier refactoring and improved navigability, the ability to directly embed application context configuration directly within a test suite makes them both easier to read and more legible. 

Another, more subtle advantage of the code-centric configuration style, is that is allows framework developers to offer a more **graduated** response to increasing configuration complexity. With XML-namespace configuration, when the API fails, the next step is usually to abandon the approach and instead directly register the factory bean. With a code-based API, the recourse may just be to override a more general or fundamental template method higher up the configuration API's type hierarchy. 

Case Study - Spring MVC
=======================

Spring 3.1 shiped with the ``@EnableWebMvc`` annotation that, when combined with the ``@Configuration`` annotation, provides a mechanism to configure the container to support Spring's MVC framework. It's worth understanding the approach taken with ``@EnableWebMvc``, since it provides a useful casestudy of how a framework can support annotation-driven code-centric container configuration. 

The first thing to consider is that Spring's MVC implementation assumes **one dispatcher servlet per child web application context**, where the dispatcher servlet is conceptually similar to a JAX-RS server. This means that their implementation can make more assumptions about the role of a registered bean; a ``WebRequestInterceptor`` bean is unambiguously intended to be registered for the sole dispatcher servlet configured for this application context.   

The ``EnableWebMvc`` framework offers two mutually exclusive configuration styles: the first offers simplicity for the most common configuration cases, the second provides ultimate control of Web MVC configuration. The first requires that specific configuration be done by registering one or more beans that implement an interface ``WebMvcConfigurer``. The interface allows one of more callback methods to be defined that perform the various configuration, such as registering interceptors, validators and so on. Framework locates all beans implementing this interface and invokes the configuration callback methods to perform the required configuration. 

The second API involves directly extending a configuration class and overriding the various @Bean annotated methods. Extending this class allows fine-grained control over the configuration of Spring Web MVC infrastructure beans for less common cases. Transitioning from the simpler ``@EnableWebMvc`` approach to the extension approach is as simple as removing the annotation and extending a base class. 

This dual API is an example of how a framework can offer a graduated response to increasing configuration demands.

Design Goals
============

 * to offer a comparable alternative to the **jaxrs:** XML namespace
 * to clearly and declaratively define JAX-RS servers in Java code
 * to define and configure the various server collaborators such as services, interceptors and features
 * interoperability with Spring and Spring's bean lifecycle
 * to create multiple servers within a single application context
 * to allow the subclassing of production configuration class for the purposes of testing (to say, register only a single service)

A Possible API
==============

Let's consider first an API for creating a simple JAX-RS server:

    @Configuration
    @EnableJaxRsServer
    class MyServerConfig {
    }

This might create a single server within the application context and import the usual CXF specific spring beans via ``/META-INF/cxf/cxf.xml``. Of course, no actual services would be registered with the server and therefore the server bootstrapping and configuration phase would fail (service-less servers are disallowed). Let's consider adding a service:

    @Configuration
    @EnableJaxRsServer
    class MyServerConfig {
    @JaxRsService
    public EchoService echoService() {
           return new SimpleEchoService();
       }
    }

The ``@JaxRsService`` annotation is a flavour of the ``@Bean`` annotation with the additional effect of registering the bean as a service within this server. That is, the created ``SimpleEchoService`` should be a container-registered bean that is eligible for injection and subject to the typical container lifecycle events and services such as ``@PostConstruct`` and ``@PreDestroy``. It should also be compatible with other annotations that are available on factory methods of ``@Configuration`` annotated classes such as ``@Lazy``:

    @Configuration
    @EnableJaxRsServer
    class MyServerConfig {
       @Lazy
       @JaxRsService
       public EchoService echoService() {
          return new SimpleEchoService();
       }
    }

Remaining compatible with Spring's existing factory method annotations is of course desirable. A server can have various additional properties such as an ``address`` and a ``transport ID`` which we may add directly onto the server annotation:

    @Configuration
    @EnableJaxRsServer(address="/", transport="http://...")
    class MyServerConfig {
       @JaxRsService
       public EchoService echoService() {
          return new SimpleEchoService();
       }
    }

We may also want to add **multiple servers** to the configuration class, and/or allow for extension to help define the second server:

    @Configuration
    @EnableJaxRsServer(name="a", address="/a", transport="http://...")
    class MyServerConfig {
       @JaxRsService
       public EchoService echoService() {
          return new SimpleEchoService();
       }
    }
    @Configuration
    @EnableJaxRsServer(name="b", address="/b", transport="http://...")
    class MyOtherServerConfig extends MyServerConfig {
       @JaxRsService
       public EchoService echoService() {
          return new SimpleEchoService();
       }
    }

It's often desirable to discover and register services through **component scanning**. So this approach should be additionally compatible with the Spring ``@ComponentScan`` annotation.

    @Configuration
    @EnableJaxRsServer
    @ComponentScan(
       @ComponentScan.Filter({ Path.class })
    )
    class MyOtherServerConfig extends MyServerConfig {
       @Autowired private EchoService echoService;
       @JaxRsService
       public EchoService echoService() {
          return echoService;
       }
    }

In addition to this, it makes sense to also allow the wholesale registration of services by annotation:

    @Configuration
    @EnableJaxRsServer
    @ComponentScan(
       @ComponentScan.Filter({ Path.class })
    )
    @JaxRsServices( { Path.class } )
    class AnnotationBasedRegistration extends MyServerConfig {
    }

by name:

    @Configuration
    @EnableJaxRsServer
    @ComponentScan(
       @ComponentScan.Filter({ Path.class })
    )
    @JaxRsServices( named = {"firstService", "secondService"})
    class NamedServiceRegistration extends MyServerConfig {
    }

by assignable type:

    @Configuration
    @EnableJaxRsServer
    @ComponentScan(
       @ComponentScan.Filter({ Path.class })
    )
    @JaxRsServices( type = FirstRestService.class, SecondRestService.class )
    class TypeBasedRegistration extends MyServerConfig {
    }
    
and a combination of the above. 

Find grained control
====================

One particular requirement that is desirable for both production and test scenarios is composability and inheritance. Common configuration can be centralised, test code can inherit and alter production configuration for test scenarios, common set up or tear down code can be mixed together, etc. Spring bean configuration provides an analog of configuration inheritance through its ``abstract`` and ``parent`` attributes. 



Annotation Composition
======================

**Annotation composition** is a technique to simplify annotation-based APIs be combining multiple annotations onto a single annotation. Annotations that are eligble for composition onto other annotations, along with some accompanying infrastructure to read an interpret such annotations, are known as **meta-annotations**. A quick example::

    @Transactional(readonly = true)
    @Repository
    @Scope("prototype")
    public @interface AcmeRepository { } 

Here, usage of the ``@AcmeRepository`` on a component bean will enable the various Spring container services defined as meta-annotations. The meta-annotation approach has a few benefits:

 * It can decouple application code from a 3rd-party framework
 * It can centralise annotation-based configuration 

Any framework API that is based upon annotations should include support for meta-annotations. Fortunately, Spring ships with excellent support for meta-annotation discovery, as well as attribute inheritance and override support. 

Let's look at how this could be leveraged for JAX-RS annotation-based configuration.

