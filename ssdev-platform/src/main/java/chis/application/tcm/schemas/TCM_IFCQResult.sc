<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tcm.schemas.TCM_IFCQResult" alias="中医体质辨识问卷结果表（主）">
	<item id="IFCQRID" alias="记录主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
  
	<item id="empiId" alias="个人主索引" type="string" length="32" display="0"/>
	
	<item ref="b.personName" display="1" queryable="true" locked="true"/>
	<item ref="b.sexCode" display="1" queryable="true" locked="true"/>
	<item ref="b.birthday" display="1" queryable="true" locked="true"/>
	<item ref="b.idCard" display="1" queryable="true" locked="true"/>
	
	<item id="questionnaireDate" alias="问卷日期" type="date" update="false" defaultValue="%server.date.date" maxValue="%server.date.date"/>
	<item id="guideDoctorId" alias="指导医生" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="guideDoctorUnitId" alias="指导医生机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="yangxzScore" alias="阳虚质原始分" type="int" length="3" width="100" bind="阳虚质"/>
	<item id="yangxzChangeScore" alias="阳虚质转化分" type="int" length="3" width="100" bind="阳虚质"/>
	
	<item id="yinxzScore" alias="阴虚质原始分" type="int" length="3" width="100" bind="阴虚质"/>
	<item id="yinxzChangeScore" alias="阴虚质转化分" type="int" length="3" width="100" bind="阴虚质"/>
	
	<item id="qixzScore" alias="气虚质原始分" type="int" length="3" width="100" bind="气虚质"/>
	<item id="qixzChangeScore" alias="气虚质转化分" type="int" length="3" width="100" bind="气虚质"/>
	
	<item id="tanszScore" alias="痰湿质原始分" type="int" length="3" width="100" bind="痰湿质"/>
	<item id="tanszChangeScore" alias="痰湿质转化分" type="int" length="3"  width="100" bind="痰湿质"/>
	
	<item id="shirzScore" alias="湿热质原始分" type="int" length="3" width="100" bind="湿热质"/>
	<item id="shirzChangeScore" alias="湿热质转化分" type="int" length="3" width="100" bind="湿热质"/>
	
	<item id="xueyzScore" alias="血瘀质原始分" type="int" length="3" width="100" bind="血瘀质"/>
	<item id="xueyzChangeScore" alias="血瘀质转化分" type="int" length="3" width="100" bind="血瘀质"/>
	
	<item id="tebzScore" alias="特禀质原始分" type="int" length="3" width="100" bind="特禀质"/>
	<item id="tebzChangeScore" alias="特禀质转化分" type="int" length="3" width="100" bind="特禀质"/>
	
	<item id="qiyzScore" alias="气郁质原始分" type="int" length="3" width="100" bind="气郁质"/>
	<item id="qiyzChangeScore" alias="气郁质转化分" type="int" length="3" width="100" bind="气郁质"/>
	
	<item id="hepzScore" alias="平和质原始分" type="int" length="3" width="100" bind="平和质"/>
	<item id="hepzChangeScore" alias="平和质转化分" type="int" length="3" width="100" bind="平和质"/>
	
	<item id="questionnaireType" alias="问卷分类" type="string" length="1">
		<dic>
			<item key="1" text="老年人"/>
			<item key="2" text="大众"/>
		</dic>
	</item>
	<item id="questionnaireRusult" alias="问卷结果综述" type="string" length="100" display="0"/>
	<item id="status" alias="状态" type="string" length="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="questionnaireSchema" type="string" length="100" display="0"/>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>
