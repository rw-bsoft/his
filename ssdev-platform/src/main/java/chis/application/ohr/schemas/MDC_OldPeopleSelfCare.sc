<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.ohr.schemas.MDC_OldPeopleSelfCare" alias="老年人自理评估" sort="a.id desc">
	<item id="SCID" pkey="true" alias="记录标识" type="string" length="16" not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
	    <key> 
	      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
	    </key> 
  </item>
  <item id="empiId" alias="EMPIID" type="string" length="32" not-null="1" hidden="true" display="0"/>  
  <item id="phrId" alias="档案编号" type="string" not-null="1" length="30" hidden="true" display="0"/>  
  <item id="JC" alias="进餐"  type="string" length="1" not-null="1" display="2">
  	<dic>
  		<item key="1" text="独立完成"/>	
  		<item key="2" text="轻度依赖"/>		
  		<item key="3" text="需要协助，如切碎、搅拌食物等"/>		
  		<item key="4" text="完全需要帮助"/>		
  	</dic>
  </item>
  <item id="JCDJ" alias="进餐等级"  type="string" length="1" fixed="true" display="2">
  	<dic id="chis.dictionary.oldSelfCareGrade"/>
  </item>
  <item id="JCPF" alias="进餐评分" type="int" defaultValue="0" fixed="true" display="2"/>
  <item id="SX" alias="梳洗"  type="string" length="20" not-null="1" display="2">
  	<dic>
  		<item key="1" text="独立完成"/>	
  		<item key="2" text="能独立地洗头、梳头、洗脸、刷牙、剃须等；洗澡需要协助"/>		
  		<item key="3" text="在协助下和适当的时间内，能完成部分梳洗活动"/>		
  		<item key="4" text="完全需要帮助"/>		
  	</dic>
  </item>
  <item id="SXDJ" alias="梳洗等级"  type="string" length="1" fixed="true" display="2">
  	<dic id="chis.dictionary.oldSelfCareGrade"/>
  </item>
  <item id="SXPF" alias="梳洗评分" type="int" defaultValue="0" fixed="true" display="2"/>
  <item id="CY" alias="穿衣"  type="string" length="20" not-null="1" display="2">
  	<dic>
  		<item key="1" text="独立完成"/>	
  		<item key="2" text="轻度依赖"/>		
  		<item key="3" text="需要协助，在适当的时间内完成部分穿衣"/>		
  		<item key="4" text="完全需要帮助"/>		
  	</dic>
  </item>
  <item id="CYDJ" alias="穿衣等级"  type="string" length="1" fixed="true" display="2">
  	<dic id="chis.dictionary.oldSelfCareGrade"/>
  </item>
  <item id="CYPF" alias="穿衣评分" type="int" defaultValue="0" fixed="true" display="2"/>
  <item id="RC" alias="如厕"  type="string" length="20" not-null="1" display="2">
  	<dic>
  		<item key="1" text="不需协助，可自控"/>	
  		<item key="2" text="偶尔失禁，但基本上能如厕或使用便具"/>		
  		<item key="3" text="经常失禁，在很多提示和协助下尚能如厕或使用便具"/>		
  		<item key="4" text="完全失禁，完全需要帮助"/>		
  	</dic>
  </item>
  <item id="RCDJ" alias="如厕等级"  type="string" length="1" fixed="true" display="2">
  	<dic id="chis.dictionary.oldSelfCareGrade"/>
  </item>
  <item id="RCPF" alias="如厕评分" type="int" defaultValue="0" fixed="true" display="2"/>
  <item id="HD" alias="活动"  type="string" length="20" not-null="1" display="2">
  	<dic>
  		<item key="1" text="独立完成所有活动"/>	
  		<item key="2" text="借助较小的外力或辅助装置能完成站立、行走、上下楼梯等"/>		
  		<item key="3" text="借助较大的外力才能完成站立、行走，不能上下楼梯"/>		
  		<item key="4" text="卧床不起，活动完全需要帮助"/>		
  	</dic>
  </item>
  <item id="HDDJ" alias="活动等级"  type="string" length="1" fixed="true" display="2">
  	<dic id="chis.dictionary.oldSelfCareGrade"/>
  </item>
  <item id="HDPF" alias="活动评分" type="int" defaultValue="0" fixed="true" display="2"/>
  <item id="ZP" alias="总评"  type="string" length="20" fixed="true">
  	<dic>
  		<item key="1" text="可自理"/>	
  		<item key="2" text="轻度依赖"/>		
  		<item key="3" text="中度依赖"/>		
  		<item key="4" text="不能自理"/>		
  	</dic>
  </item>
  <item id="ZPDJ" alias="总评等级"  type="string" length="1" fixed="true">
  	<dic id="chis.dictionary.oldSelfCareGrade"/>
  </item>
  <item id="ZPFS" alias="总评分数" type="int" defaultValue="0" fixed="true"/>
  
	<item id="createUser" alias="创建人员" type="string" update="false"  length="20" fixed="true" defaultValue="%user.userId" width="100">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  xtype="datefield" update="false"  fixed="true"  width="100"
		  defaultValue="%server.date.date" enableKeyEvents="true" validationEvent="false">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
   <item id="createUnit" alias="创建机构" type="string" update="false" length="20" defaultValue="%user.manageUnit.id" fixed="true" width="180">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" display="0"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" width="100" 
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" width="100" 
		defaultValue="%server.date.date" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
</entry>