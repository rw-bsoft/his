<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_GHMX" alias="门诊收费列表">
  <item ref="b.EMPIID" alias="empiId" type="string"  length="32" display="0" />
  <item ref="b.BRXZ" alias="病人性质" fixed="true" length="18" >
  	<dic id="phis.dictionary.patientProperties_MZ"/>
  </item>
  <item ref="b.BRXM" alias="姓名" type="string"  length="40" not-null="1" queryable="true"/>
  <item ref="b.MZHM" alias="门诊号码" type="string"  length="32" width="100" queryable="true" selected="true" />
  <item id="KSDM" alias="挂号科室"  type="long" length="18" width="120" not-null="1">
		<dic id="phis.dictionary.department_reg" />
	</item>
  <item id="JZZT" alias="就诊状态">
  	<dic>
  		<item key="0" text="挂号"/>  
  		<item key="1" text="就诊中"/>
  		<item key="2" text="暂挂"/>  
  		<item key="9" text="就诊结束"/>  
  	</dic>
  </item>
  <item id="ISSF" alias="是否收费" />
  <item id="YBMC" alias="医保疾病名称" />
  <relations>
		<relation type="children" entryName="phis.application.cic.schemas.MS_BRDA" >
			<join parent="BRID" child="BRID" />
		</relation>
  </relations>
</entry>
