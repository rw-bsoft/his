<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.wl.schemas.MDC_FromHis" alias="his慢病核实" sort="a.zdsj desc">
  <item id="mdcid" pkey="true" alias="记录序号" type="string"
    width="160" length="16" not-null="1" display="1"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="brid" alias="病人编码" type="int" hidden="true"  fixed="true" />
  <item id="empiId" alias="empiid" type="string"  hidden="true" />
  <item id="jlbh" alias="疾病记录编码" type="int" hidden="true" fixed="true" />
  <item id="brxm" alias="病人姓名" type="string" queryable="true" />
  <item id="sfzh" alias="身份证号" type="string" length="20" width="160" colspan="1" queryable="true" />
  <item id="jdlx" alias="需建档类型" type="string" fixed="true" width="120" >
  	<dic render="LovCombo">
  		<item key="0" text="健康档案" />
  		<item key="1" text="高血压" />
  		<item key="2" text="糖尿病" />
  	</dic>
  </item>
  
  <item id="status" alias="确认状态" type="string" display="1" defaultValue="1" queryable="true">
    <dic render="LovCombo">
  		<item key="1" text="待确认" />
		<item key="4" text="已确认" />
		<item key="9" text="已退回" />
  	</dic>
  </item>
  <item id="brxb" alias="病人性别" type="string" width="60" >
  	<dic id="chis.dictionary.gender"/>
  </item>
  <item id="csny" alias="出生日期" type="date" queryable="true" />
  <item id="hkdz" alias="地址" type="string" />
  <item id="lxdh" alias="联系电话" type="string" />
  <item id="zdsj" alias="诊断日期" type="date" update="false" queryable="true" />
  <item id="ehrjgdm" alias="产生机构" type="string" update="false"  width="160" queryable="true" >
  	<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
  </item>
  <item id="createdate" alias="产生日期" type="date" update="false" queryable="true" />
  
  <item id="affirmType" alias="确认处理" type="string" length="1"
    fixed="true" queryable="true" display="1">
    <dic>
      <item key="1" text="同意" />
      <item key="2" text="退回" />
    </dic>
  </item>
  <item id="areagrid" alias="网格地址" type="string" colspan="2" length="25" width="200" anchor="100%"  queryable="true">
	<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"
		 parentKey="320124" />
  </item>
  <item id="targetDoctor" alias="现医生" type="string" length="20" 
    >
    <dic id="chis.dictionary.user04" render="Tree" onlySelectLeaf="true"
      parentKey="%user.manageUnit.id" />
  </item>
  <item id="targetUnit" alias="现管辖机构" type="string" length="20" fixed="true"
    width="160"  colspan="2" queryable="true">
    <dic id="chis.@manageUnit" includeParentMinLen="6" onlySelectLeaf="true" lengthLimit="9" querySliceType="0" render="Tree" rootVisible = "true" parentKey="%user.manageUnit.id"/>
  </item>
  <item id="affirmUser" alias="确认人" type="string" length="20" fixed="true"
    queryable="true" >
    <dic id="chis.dictionary.user14" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id" />
  </item>
  <item id="affirmUnit" alias="确认机构" type="string" length="20" fixed="true"
    width="250" display="2" colspan="2">
    <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
      onlySelectLeaf="true" />
  </item>
  <item id="affirmDate" alias="确认日期" type="date" queryable="true" fixed="true" />
  <item id="affirmView" alias="确认人意见" type="string" length="500"
    colspan="2" display="2"  />
 
</entry>