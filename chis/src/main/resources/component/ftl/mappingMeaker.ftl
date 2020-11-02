<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC 
	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<#assign types = {
	"string":"java.lang.String",
 	"int":"java.lang.Integer",
 	"long":"java.lang.Long",
 	"double":"java.lang.Double",
 	"bigDecimal":"big_decimal",
 	"date":"date",
 	"timestamp":"timestamp"
 }>
 <#assign generator = {
	"assigned":"assigned",
 	"auto":"identity"
 }>
<hibernate-mapping>
	<#assign tableName=doc.entry.@table[0]!doc.entry.@id>
	<class entity-name="${doc.entry.@id}" table="${tableName}">
		<#list doc.entry.item as it>
		<#assign type=types[(it.@type[0])!"string"]!"">
		<#if it.@pkey[0]??>
		<id name="${it.@id}" type="${type}">
	    	<column name="${it.@id}"/>
			<generator class="${generator[it.@generator!'auto']!''}" />
		</id>
		<#else>
		<property name="${it.@id}" type="${type}" <#if it.@length[0]??>length="${it.@length}"</#if> <#if it.@notNull[0]??>not-null="${it.@notNull}"</#if>/>
		</#if>
		</#list>
	</class>
</hibernate-mapping>