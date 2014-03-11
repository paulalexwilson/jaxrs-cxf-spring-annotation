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

Additionally, the ability to embed application configuration directly within a test suite makes writing integration tests easier.

Spring MVC
==========

Spring 3.1 shiped with the ``@EnableWebMvc`` annotation that, when combined with the ``@Configuration`` annotation, provides a mechanism to configure the container to support Spring's MVC framework. 

Design Goals
============

 * to clearly and declaratively define JAX-RS servers in Java code
 * to define and configure the various server collaborators such as services, interceptors and features
 * interoperability with Spring and Spring's bean lifecycle
 * to create multiple servers within a single application context
 * to override a production configuration class for the purposes of testing (to say, register only a single service)
 


