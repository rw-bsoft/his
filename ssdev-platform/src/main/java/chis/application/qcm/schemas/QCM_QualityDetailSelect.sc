<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.qcm.schemas.QCM_QualityDetailSelect" tableName="chis.application.qcm.schemas.QCM_QualityDetail" alias="质控明细表">
	<item id="QDID" alias="记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	
	<item ref="b.personName"/>
	<item ref="c.manaUnitId"/>
	<item ref="c.qualityKind"/>
	<item ref="c.zkLevel"/>
	<item ref="c.periodKind"/>
	
	<item id="QMID" alias="质控主表ID" type="string" length="16" display="0"/>
	<item id="visitId" alias="原随访ID" type="string" length="16" display="0"/>
	<item id="qualityVisitId" alias="质控随访ID" type="string" length="16" display="0"/>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="alreadyQuality" alias="已质控" type="string" length="1"/>
	<item id="alreadyGrade" alias="已评分" type="string" length="1"/>
	<item id="visitDate" alias="随访日期" type="date"/>
	<item id="qualityDate" alias="质控日期" type="date"/>
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" colspan="2">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	
	<item id="createUser" alias="录入医生" type="string" length="20" update="false"  defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="parent" entryName="chis.application.qcm.schemas.QCM_QualityMain">
			<join parent = "QMID" child = "QMID" />
		</relation>
	</relations>
	
</entry>