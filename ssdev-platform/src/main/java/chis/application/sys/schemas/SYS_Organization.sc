<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="SYS_Organization" alias="机构报表">
	<item id="organizCode" alias="组织机构代码" type="string" pkey="true" queryable="true" generator="assigned" width="100" not-null="1" length="50"/>
	<item id="organizName" alias="机构名称" type="string" queryable="true" colspan="2" width="200" not-null="1" length="50"/>
	<item id="registerNumber" alias="登记号" type="string" width="200" not-null="1" length="50"/>
	<item id="organizSecondName" alias="机构第二名称" type="string" colspan="2" width="200"  length="50"/>
	<item id="classifyCode" alias="机构分类" type="string" width="120" not-null="1">
		<dic id="platform.reg.dictionary.classifyCode"/>
	</item>
	<item id="organizType" alias="机构类型" type="string" colspan="2" width="120" not-null="1" queryable="true">
		<dic render="Tree" id="platform.reg.dictionary.organizType"/>
	</item>
	<item id="adminDivision" alias="行政区划" type="string" not-null="1">
		<dic render="Tree" rootVisible="true" parentKey="33" id="platform.reg.dictionary.adminDivision"/>
	</item>
	<item id="address" alias="地址" type="string" colspan="2" width="200" length="100"/>
	<item id="streeCode" alias="乡镇街道代码" type="string" width="90" length="50"/>
	<item id="streeName" alias="乡镇街道名称" type="string" colspan="2" width="120" length="50"/>
	<item id="hostCode" alias="主办单位" type="string" width="100">
		<dic id="platform.reg.dictionary.hostCode"/>
	</item>
	<item id="parentId" alias="上级部门" type="string" width="150" colspan="2" fixed="true" display="-1">
		<dic render="Tree" id="platform.reg.dictionary.organizationDic"/>
	</item>
	<item id="subCode" alias="隶属关系" type="string" width="200">
		<dic id="platform.reg.dictionary.subCode"/>
	</item>
	<item id="zipCode" alias="邮政编码" type="string" length="50"/>
	<item id="foundDate" alias="单位成立时间" type="date" width="100"/>
	<item id="telphone" alias="联系电话" type="string" length="50"/>
	<item id="email" alias="电子邮箱" type="string" width="150" length="50"/>
	<item id="website" alias="单位网站" type="string" width="150" length="50"/>
	<item id="legal" alias="法人代表" type="string" length="50"/>
	<item id="director" alias="负责人" type="string" length="50"/>
	<item id="pyCode" alias="拼音助记" type="string">
		<set type="exp" run="server">['py',['$','r.organizName']]</set>
	</item>
	<item id="buildingArea" alias="建筑面积(㎡)" type="string" length="10" isVisible = "true" display="2"/>
	<item id="chemicalMedNum" alias="化学药品种数" type="string" length="10" isVisible = "true" display="2"/>
	<item id="chineseMedNum" alias="中成药品种数" type="string" length="10" isVisible = "true" display="2"/>
	<item id="grade" alias="机构级别" type="string" length="10" isVisible = "true" display="2">
		<dic id="platform.reg.dictionary.grade"/>
	</item>
	<item id="institLevel" alias="机构等次" type="string" length="10"  isVisible = "true" display="2">
		<dic id="platform.reg.dictionary.institLevel"/>
	</item>
	<item id="subNum" alias="直属分站个数" type="int" length="10" minValue="0" isVisible = "true" display="2"/>
	<item id="stationNum" alias="服务站个数" type="int" length="10" minValue="0" isVisible = "true" display="2"/>
	<item id="logoff" alias="状态" type="string" display="0" defaultValue="0"/>
</entry>