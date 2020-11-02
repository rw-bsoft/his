<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mh.schemas.SQ_MUSE" alias="心电图表"> 
	<item id="JLXH" pkey="true" alias="记录主键" type="string" length="30" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="30" startPos="1"/> 
		</key> 
	</item>  
	<item id="JKKH" alias="健康卡号" type="string" length="20" fixed="true"  display="1"/>  
	<item id="KDRQ" alias="开单日期" type="date"/>    
	<item id="GRBM" alias="个人编码" type="string" length="30" fixed="true"  display="0"/>  
	<item id="EMPIID" alias="EMPIID" type="string" length="32" fixed="true"  display="0"/>  
	<item id="JCXM" alias="检查项目" type="string" length="40" fixed="true"  display="1"/>  
	<item id="KDKS" alias="开单科室" type="string" length="20" fixed="true"  display="1"/>
	<item id="KDYS" alias="开单医生" type="string" length="20" fixed="true"  display="1"/>
	<item id="JCYY" alias="检查医院" type="string" length="40" fixed="true"  display="1"/>
	<item id="JCLX" alias="检查类别" type="int" length="2" fixed="true"  display="1"/>
	<item id="KDKH" alias="开单卡号" type="string" length="32" fixed="true"  display="0"/>
	<item id="KDDH" alias="开单单号" type="string" length="32" fixed="true"  display="0"/>
	<item id="SFRQ" alias="随访日期" type="date" display="0"/>   
	<item id="BGMS" alias="报告描述" type="string" length="200" fixed="true"  display="1"/>
	<item id="BZ" alias="备注" type="string" length="100" fixed="true"  display="1"/>
	<item id="INTIME" alias="引入日期" type="date" display="0"/>   
	<item id="CZGH" alias="操作工号" type="string" length="20" fixed="true"  display="0"/>
	<item id="YRBZ" alias="YRBZ" type="int" length="2" fixed="true"  display="0"/>
	<item id="YCBZ" alias="异常标志" type="int" length="2" fixed="true"  display="1">
		<dic> 
			<item key="1" text="异常"/>  
			<item key="0" text="无"/>  
		</dic> 		
	</item>    

	<item id="createUnit" alias="创建单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="0" defaultValue="%user.userId">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="0" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
