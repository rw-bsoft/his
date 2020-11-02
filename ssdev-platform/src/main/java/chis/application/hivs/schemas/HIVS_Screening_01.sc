<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hivs.schemas.HIVS_Screening" alias="HIV人群筛查" sort="screeningDate desc">
	<item id="screenId" pkey="true" alias="记录序号" type="string" width="160" length="16" not-null="1" hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1" width="160" fixed="true" display="2"  update="false"/>
	<item id="empiId" type="string" length="30" not-null="1" width="160" fixed="true" display="0"  update="false"/>
	<item id="checkFlag" alias="审核" type="string" length="20" display="1" queryable="true">	
		<dic>
			<item key="0" text="未审核"/>
			<item key="1" text="已审核"/>			
		</dic>
	</item>
	<item id="screeningDate" alias="筛查日期" type="date" xtype="datefield" fixed="true" display="3" queryable="true" />
	<item ref="b.personName" alias="姓名" type="string" length="20" not-null="1" fixed="true" queryable="true"  update="false"/>	
	<item ref="b.sexCode" alias="性别" type="string" length="1" fixed="true" display="1"  update="false">	
		<dic id="chis.dictionary.gender"/>
	</item>   
	<item ref="b.birthday" alias="出生日期" type="date" maxValue="%server.date.today" update="false"/>      
	<item ref="b.idCard" alias="身份证号码" type="string" width="150" update="false"/>
	<item ref="c.checkType" type="string" display="1"  virtual = "true"  alias="是否年检" update="false"/>
	<item ref="b.mobileNumber" alias="本人电话" type="string" length="20" update="false"/>
	<item ref="b.contactPhone" alias="联系人电话" type="string" length="20" update="false"/>  
	<item ref="b.crowdType" alias="人群类型"  type="string" length="20" update="false">
      <dic render="LovCombo">
			<item key="01" text="老年人" selected="false"/>
			<item key="02" text="高血压"/>
			<item key="03" text="糖尿病"/>
			<item key="04" text="0-6岁儿童"/>
			<item key="05" text="孕产妇"/>
			<item key="06" text="精障"/>
			<item key="07" text="结核病"/>
			<item key="08" text="残疾人"/>
			<item key="09" text="建档立卡"/>
			<item key="10" text="城乡低保"/>
			<item key="11" text="城乡特困"/>
			<item key="12" text="计生特殊"/>
			<item key="13" text="慢阻肺"/>
			<item key="14" text="优抚对象"/>
			<item key="15" text="特别扶助"/>
			<item key="16" text="离休干部"/>
			<item key="17" text="创新创业人才"/>
			<item key="18" text="麻风病"/>
			<item key="19" text="肿瘤"/>
		</dic>
	</item>                               
	<item ref="b.address" alias="现住址" type="string" length="30" queryable="true" update="false"/>
	<item ref="b.educationCode" alias="文化程度" type="string" length="2" update="false">
	   	<dic id="chis.dictionary.education" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item ref="b.workCode" alias="职业" type="string" update="false">
		<dic id="chis.dictionary.jobtitle" onlySelectLeaf="true"/>
	</item>
	<item ref="b.maritalStatusCode" alias="婚姻状况" type="string" length="2" not-null="1" defaultValue="9" width="50" update="false">
		<dic id="chis.dictionary.maritals" render="Tree" minChars="1" onlySelectLeaf="true"/>
	</item>
	<item id="operation" alias="手术" type="string" not-null="1" queryable="true" fixed="true">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="transfusion" alias="输血" type="string" not-null="1" queryable="true" fixed="true">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>
	<item id="outHistory" alias="外出史" not-null="1" queryable="true" type="string" fixed="true">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="3个月以下"/>
			<item key="2" text="3-6个月"/>
			<item key="3" text="6-12个月"/>
			<item key="4" text="1年以上"/>
		</dic>
	</item>
	<item id="seperationTM" alias="夫妻分居超过三个月" not-null="1" queryable="true" type="string" fixed="true"> 
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	<item id="widowedHY" alias="丧偶超过半年" not-null="1" queryable="true" type="string" fixed="true">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>	
	</item>
	<item id="other" alias="其他" type="string" fixed="true" length="100" />
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