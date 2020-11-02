<?xml version="1.0" encoding="UTF-8"?>

<entry alias="处方对照" entityName="YJ_JCSQ_JCXM" >
	<!--其他的键根据各地医保返回值添加-->
	<item id="XMID" alias="项目编号" type="long" length="18" pkey="true"/>
	<item id="XMMC" alias="项目名称" type="string" length="20" />
	<item id="SBM" alias="检查编码" type="string"  />
	<item id="PYDM" alias="拼音编码" type="string" queryable="true" fixed="true" display="0" />
</entry>
