<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.fdsr.schemas.FDSR" alias="家医服务记录列表" sort="recodeId desc">
	<item id="recodeId" pkey="true" alias="记录序号" type="string" width="160" length="16" not-null="1" hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1" width="160" fixed="true" display="2"  update="false"/>
	<item id="empiId" type="string" length="30" not-null="1" width="160" fixed="true" display="0"  update="false"/>

	<!--<item id="recodeDate" alias="记录日期" type="date" xtype="datefield" fixed="true" display="3" queryable="true" />-->
	<item ref="b.personName" alias="姓名" type="string" length="20" not-null="1" fixed="true" queryable="true"  update="false"/>	
	<item ref="b.sexCode" alias="性别" type="string" length="1" fixed="true" display="1"  update="false">	
		<dic id="chis.dictionary.gender"/>
	</item>   	  
	<item ref="b.idCard" alias="身份证号码" type="string" width="150" update="false"/>
	<item ref="b.birthday" alias="出生日期" type="date" maxValue="%server.date.today" update="false"/>   
	<item ref="b.mobileNumber" alias="本人电话" type="string" length="20" update="false"/>
	
	<item id="name" alias="姓名" type="string" fixed="true" length="100" display="2" />
	<item id="sex" alias="性别" type="string" fixed="true" length="100" display="2" />
	<item id="age" alias="年龄" type="string" fixed="true" length="100"  />
	<item id="telephone" alias="电话" type="string" fixed="true" length="100" display="2" />
	
	<item id="isjkgl" alias="健康管理服务" type="string" fixed="true" length="10"  queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="isjksj" alias="健康数据监测" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="ishlyy" alias="合理用药指导" type="string" fixed="true" length="10" queryable="true" display="2">
	<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="ispsyz" alias="配送医嘱内药品" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="islxdc" alias="联系代采购药品" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="isfyjk" alias="妇幼健康项目咨询服务" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="iskzmx" alias="开展慢性病等重点人群自我管理小组活动" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="iszyyj" alias="中医药健康管理" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="isbyzd" alias="避孕指导、药具发放" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="iszzyy" alias="转诊、预约就诊等联络" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="isyytj" alias="预约体检时间" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="isjdtj" alias="解读体检报告" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="isxlgh" alias="心理关怀" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="iskzjy" alias="开展家医签约" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="isqt" alias="其他" type="string" fixed="true" length="10" queryable="true" display="2">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	
	<item id="jkgl" alias="健康管理服务" type="string" fixed="true" length="100"  />
	<item id="jksj" alias="健康数据监测" type="string" fixed="true" length="100" />
	<item id="hlyy" alias="合理用药指导" type="string" fixed="true" length="100" />
	<item id="psyz" alias="配送医嘱内药品" type="string" fixed="true" length="100" />
	<item id="lxdc" alias="联系代采购药品" type="string" fixed="true" length="100" />
	<item id="fyjk" alias="妇幼健康项目咨询服务" type="string" fixed="true" length="100" />
	<item id="kzmx" alias="开展慢性病等重点人群自我管理小组活动" type="string" fixed="true" length="100" />
	<item id="zyyj" alias="中医药健康管理" type="string" fixed="true" length="100" />
	<item id="byzd" alias="避孕指导、药具发放" type="string" fixed="true" length="100" />
	<item id="zzyy" alias="转诊、预约就诊等联络" type="string" fixed="true" length="100" />
	<item id="yytj" alias="预约体检时间" type="string" fixed="true" length="100" />
	<item id="jdtj" alias="解读体检报告" type="string" fixed="true" length="100" />
	<item id="xlgh" alias="心理关怀" type="string" fixed="true" length="100" />
	<item id="kzjy" alias="开展家医签约" type="string" fixed="true" length="100" />
	<item id="qt" alias="其他" type="string" fixed="true" length="100" />
	
	<item id="createUser" alias="录入人" type="string" length="20" fixed="true" update="false"
		display="3" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false"
		fixed="true" display="3" defaultValue="%user.manageUnit.id" width="250">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间"  type="datetime"  xtype="datefield"  fixed="true" update="false"
		display="3" defaultValue="%server.date.date" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改时间"  type="datetime"  xtype="datefield"  fixed="true"
		display="1" defaultValue="%server.date.date">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
	    <relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo">
	      	<join parent="empiId" child="empiId" />
	    </relation>
	    <relation type="parent" entryName="chis.application.hr.schemas.EHR_HealthRecord">
      		<join parent="empiId" child="empiId" />
    	</relation>
	</relations>
</entry>