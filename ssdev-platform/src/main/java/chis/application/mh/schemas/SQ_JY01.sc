<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mh.schemas.SQ_JY01" alias="检验表"> 
	<item id="JLXH" pkey="true" alias="记录主键" type="string" length="30" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="30" startPos="1"/> 
		</key> 
	</item>  
	<item id="JKKH" alias="健康卡号" type="string" length="20" fixed="true"  display="1"/>  
	<item id="ID" alias="检验单ID" type="string" length="50" fixed="true"  display="1"/>  
	<item id="JYRQ" alias="检验日期" type="date"/>  
	<item id="JYFF" alias="检验方法" type="string" length="20" fixed="true"  display="1"/>
	
	<item ref="b.JLXH" display="0" width="70"/>
	<item ref="b.XMBH" display="1" width="70"/>
	<item ref="b.XMMC" display="1" width="70"/>
	<item ref="b.XMZ" display="1" width="70"/>
	<item ref="b.DW" display="1" width="70"/>
	<item ref="b.CKFW" display="1" width="70"/>
	<item ref="b.JYZB" display="1" width="70"/>
	<item ref="b.HBBM" display="1" width="70"/>
	<item id="KDKS" alias="开单科室" type="string" length="20" fixed="true"  display="1"/>
	<item id="KDYS" alias="开单医生" type="string" length="20" fixed="true"  display="1"/>
	<item id="JYKS" alias="检验科室" type="string" length="20" fixed="true"  display="1"/>
	<item id="JYYS" alias="检验医生" type="string" length="20" fixed="true"  display="1"/>
	<item id="CZGH" alias="操作工号" type="string" length="20" fixed="true"  display="1"/>
	<item id="SFRQ" alias="随访日期" type="date"/>   

	<item id="YCBZ" alias="随访id" type="int" length="2" fixed="true"  display="0"/>  
	<item id="GRBM" alias="个人编码" type="string" length="30" fixed="true"  display="0"/>  
	<item id="EMPIID" alias="EMPIID" type="string" length="32" fixed="true"  display="0"/>  
	<item id="JYDH" alias="检验单号" type="string" length="64" fixed="true"  display="0"/>  
	<item id="KDRQ" alias="开单日期" type="date"/>    
	<item id="FHYS" alias="复核医生" type="string" length="20" fixed="true"  display="0"/>
	<item id="YYMC" alias="医院名称" type="string" length="100" fixed="true"  display="0"/>
	<item id="INTIME" alias="引入日期" type="date"/>   
	<item id="YRBZ" alias="YRBZ" type="int" length="1" fixed="true"  display="0"/>
	<item id="UPD" alias="UPD" type="int" length="1" fixed="true"  display="0"/>
	
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
	
	<relations>
		<relation type="child" entryName="chis.application.mh.schemas.SQ_JY02" >
			<join parent="JYDH" child="JYDH" />
		</relation>		
	</relations>
</entry>
